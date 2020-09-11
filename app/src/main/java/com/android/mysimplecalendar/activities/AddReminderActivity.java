package com.android.mysimplecalendar.activities;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.android.mysimplecalendar.R;
import com.android.mysimplecalendar.fragments.CalenderDialogFragment;
import com.android.mysimplecalendar.localdb.LocalDataBase;
import com.android.mysimplecalendar.localdb.MyNotifications;
import com.android.mysimplecalendar.localdb.MyNotifications_Table;
import com.android.mysimplecalendar.notifications.NotificationUtils;
import com.android.mysimplecalendar.utils.AppUtils;
import com.android.mysimplecalendar.utils.PreferencesManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.queriable.StringQuery;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static com.android.mysimplecalendar.utils.AppUtils.PREF_SELECTED_DATE_HOME;
import static com.android.mysimplecalendar.utils.AppUtils.PREF_SELECTED_NOTIFICATION_ID_HOME;

/**
 * Created by Hariharan Eswaran on 01/01/2019
 */
public class AddReminderActivity extends AppCompatActivity implements View.OnClickListener, CalenderDialogFragment.onDateSelected {
    private String TAG = AddReminderActivity.class.getSimpleName();
    private EditText reminderTitleEditText, reminderAboutEditText, reminderAlertEditText;
    private TextView reminderDateTextView;
    private TextView reminderTimeTextView;
    private String format = "";
    private RelativeLayout addTotalRelativeLayout;
    private TextInputLayout reminderAboutTextInputLayout;
    private PreferencesManager prefManager;
    private boolean isEditableNotification = false;
    private boolean isSubmitClicked = false;
    private MyNotifications myNotifications;
    private NotificationUtils notificationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        initializeUIComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isSubmitClicked = false;
    }

    @Override
    public void sendData(String date) {
        reminderDateTextView.setText(date);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reminderDateTextView:
                getDateFromCalendar();
                break;
            case R.id.reminderTimeTextView:
                getTimeFromPicker();
                break;
            case R.id.reminderDoneTextView:
                if (!isSubmitClicked) {
                    saveReminderToLocalDatabase();
                }
                break;
            case R.id.reminderCancelTextView:
                onBackPressed();
                break;

        }

    }


    private void saveReminderToLocalDatabase() {
        if (validateFields()) {
            String randomID = UUID.randomUUID().toString();
            String currentDate = getCurrentDate(1);
            String reminderStartDate = reminderDateTextView.getText().toString();
            String startTime = reminderTimeTextView.getText().toString();
            String validTime = AppUtils.convertTo24Hour(startTime);
            int alertTime = Integer.parseInt(reminderAlertEditText.getText().toString());
            if ((reminderStartDate.equals(getCurrentDate(2))) && (validTime.compareTo(getCurrentDate(3)) < 0)) {
                AppUtils.showAlertMsgDialog("Please select Valid Time!", this);
            } else {
                boolean isFav = false;
                if (isEditableNotification) {
                    if (myNotifications != null) {
                        isFav = myNotifications.isFavorite();
                        notificationUtils.clearLocalNotification(this, myNotifications.getNotificationID());
                        SQLite.delete(MyNotifications.class)
                                .where(MyNotifications_Table.ID.eq(myNotifications.getID()))
                                .async()
                                .execute();
                    }
                }
                List<MyNotifications> myNotificationsList = new ArrayList<>();
                String dateTime = AppUtils.addTimeInMinutes(validTime, alertTime);
                String notificationTime = reminderStartDate.concat(" ").concat(dateTime);
                String reminderTitle = reminderTitleEditText.getText().toString();
                String reminderDetails = reminderAboutEditText.getText().toString();
                myNotificationsList.add(new MyNotifications(randomID, currentDate, notificationTime, reminderTitle, reminderDetails, alertTime, startTime, false, isFav));
                if (myNotificationsList.size() > 0) {
                    DatabaseDefinition databaseDefinition = FlowManager.getDatabase(LocalDataBase.class);
                    ProcessModelTransaction<MyNotifications> processModelTransaction = new ProcessModelTransaction.Builder<>((ProcessModelTransaction.ProcessModel<MyNotifications>) (review, wrapper) -> review.save()).addAll(myNotificationsList).build();
                    Transaction pcTransaction = databaseDefinition.beginTransactionAsync(processModelTransaction).runCallbacksOnSameThread(true).success(transaction -> AppUtils.printLogConsole("##saveReminderToLocalDatabase", "----->Data Save Success")).error((transaction, error) -> AppUtils.printLogConsole("##saveReminderToLocalDatabase", "----->Data Save Error")).build();
                    pcTransaction.executeSync();
                    // Schedule Local Notifications
                    StringQuery<MyNotifications> notificationsStringQuery = new StringQuery<>(MyNotifications.class, "select * from MyNotifications where isScheduled = 0 AND datetime(ReminderDateTime) > datetime('" + getCurrentDate(1) + "')");
                    List<MyNotifications> mdNotificationsList = notificationsStringQuery.queryList();
                    if (mdNotificationsList.size() > 0) {
                        for (int i = 0; i < mdNotificationsList.size(); i++) {
                            notificationUtils.scheduleNotificationModel(mdNotificationsList.get(i));
                        }
                        String alertMsg = isEditableNotification ? "Reminder Updated!!" : "Reminder Scheduled Success!!";
                        Snackbar snackbar = Snackbar.make(addTotalRelativeLayout, alertMsg, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        isSubmitClicked = true;
                        snackbar.addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                isSubmitClicked = false;
                                onBackPressed();
                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                            }
                        });

                    }
                }
            }

        }

    }

    /**
     * Method used to get Current date
     *
     * @return current date
     */
    private String getCurrentDate(int formatID) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        if (formatID == 1) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        } else if (formatID == 2) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        } else if (formatID == 3) {
            dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        }
        Date c = Calendar.getInstance().getTime();
        return dateFormat.format(c);
    }

    private boolean validateFields() {
        boolean isValid = true;
        if (reminderTitleEditText.getText().toString().trim().isEmpty()) {
            isValid = false;
            AppUtils.showAlertMsgDialog("Please enter reminder title!", this);
        } else if (reminderAboutEditText.getText().toString().trim().isEmpty()) {
            isValid = false;
            AppUtils.showAlertMsgDialog("Please enter about reminder!", this);
        } else if (reminderAlertEditText.getText().toString().trim().isEmpty()) {
            isValid = false;
            AppUtils.showAlertMsgDialog("Please enter reminder alert!", this);
        } else if (reminderDateTextView.getText().toString().trim().isEmpty()) {
            isValid = false;
            AppUtils.showAlertMsgDialog("Please enter reminder date!", this);
        } else if (reminderTimeTextView.getText().toString().trim().isEmpty()) {
            isValid = false;
            AppUtils.showAlertMsgDialog("Please enter reminder time!", this);
        }
        return isValid;
    }

    private void getTimeFromPicker() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.time_picker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setCancelable(true);
        TimePicker CommontimePicker = dialog.findViewById(R.id.CommontimePicker);
        TextView commonTimePickerCancelText = dialog.findViewById(R.id.CommonTimePickerCancelText);
        TextView commonTimePickerOkText = dialog.findViewById(R.id.CommonTimePickerOkText);
        commonTimePickerOkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reminderTimeTextView.setText(currentTime());
                dialog.dismiss();
            }

            private String currentTime() {
                int hour = CommontimePicker.getCurrentHour();
                int minute = CommontimePicker.getCurrentMinute();
                String zero = "0";
                if (hour == 0) {
                    hour += 12;
                    format = "AM";
                } else if (hour == 12) {
                    format = "PM";
                } else if (hour > 12) {
                    hour -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }
                String currentTime;
                if (minute < 10 && hour >= 10) {
                    currentTime = hour + ":" + zero + minute + " " + format;
                } else if (hour < 10 && minute >= 10) {
                    currentTime = zero + hour + ":" + minute + " " + format;
                } else if (minute >= 10) {
                    currentTime = hour + ":" + minute + " " + format;
                } else {
                    currentTime = zero + hour + ":" + zero + minute + " " + format;
                }
                return currentTime;
            }
        });
        commonTimePickerCancelText.setOnClickListener(v -> dialog.dismiss());
        dialog.show();

    }

    private void getDateFromCalendar() {
        DialogFragment newFragment = new CalenderDialogFragment(this, reminderDateTextView.getText().toString());
        newFragment.show(getSupportFragmentManager(), TAG);
    }

    private void initializeUIComponents() {
        prefManager = PreferencesManager.getInstance(this);
        reminderTitleEditText = findViewById(R.id.reminderTitleEditText);
        addTotalRelativeLayout = findViewById(R.id.addTotalRelativeLayout);
        reminderAboutEditText = findViewById(R.id.reminderAboutEditText);
        reminderAlertEditText = findViewById(R.id.reminderAlertEditText);
        reminderDateTextView = findViewById(R.id.reminderDateTextView);
        reminderTimeTextView = findViewById(R.id.reminderTimeTextView);
        reminderAboutTextInputLayout = findViewById(R.id.reminderAboutTextInputLayout);
        TextView reminderDoneTextView = findViewById(R.id.reminderDoneTextView);
        TextView reminderCancelTextView = findViewById(R.id.reminderCancelTextView);
        reminderDateTextView.setOnClickListener(this);
        reminderTimeTextView.setOnClickListener(this);
        reminderDoneTextView.setOnClickListener(this);
        reminderCancelTextView.setOnClickListener(this);
        notificationUtils = new NotificationUtils(this);

        reminderAboutEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                reminderAboutTextInputLayout.setCounterEnabled(!charSequence.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        if (prefManager.getStringValue(PREF_SELECTED_NOTIFICATION_ID_HOME).isEmpty()) {
            isEditableNotification = false;
            reminderDateTextView.setText(prefManager.getStringValue(PREF_SELECTED_DATE_HOME));
        } else {
            editNotification();
        }
        reminderDoneTextView.setText(isEditableNotification ? "Update" : "Done");
    }

    private void editNotification() {
        myNotifications = new StringQuery<>(MyNotifications.class, "select * from MyNotifications where ID = '" + prefManager.getStringValue(PREF_SELECTED_NOTIFICATION_ID_HOME) + "'").querySingle();
        if (myNotifications != null) {
            try {
                reminderTitleEditText.setText(myNotifications.getReminderTitle());
                reminderAboutEditText.setText(myNotifications.getReminderDetails());
                reminderAlertEditText.setText(String.valueOf(myNotifications.getAlertBefore()));
                String[] parts = AppUtils.addTimeInMinutesForEdit(myNotifications.getReminderDateTime(), myNotifications.getAlertBefore()).split("\\s");
                reminderDateTextView.setText(parts[0]);
                reminderTimeTextView.setText(parts.length == 3 ? parts[1].concat(" ").concat(parts[2]) : parts[1]);
                isEditableNotification = true;
            } catch (Exception e) {
                AppUtils.printLogConsole("##editNotification", "-------->" + e.getMessage());
            }

        }
    }
}
