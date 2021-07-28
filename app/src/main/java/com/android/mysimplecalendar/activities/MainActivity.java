/*
 * Copyright 2019 ~ https://github.com/braver-tool
 */

package com.android.mysimplecalendar.activities;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mysimplecalendar.R;
import com.android.mysimplecalendar.adapter.RemindersAdapter;
import com.android.mysimplecalendar.listener.DataListener;
import com.android.mysimplecalendar.localdb.MyNotifications;
import com.android.mysimplecalendar.localdb.NotificationModel;
import com.android.mysimplecalendar.utils.AppUtils;
import com.android.mysimplecalendar.utils.PreferencesManager;
import com.android.mysimplecalendar.utils.SuperscriptFormatter;
import com.calendar.android.droidcalendar.DroidCalendarView;
import com.raizlabs.android.dbflow.sql.queriable.StringQuery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.android.mysimplecalendar.utils.AppUtils.ACTION_EDIT_REMINDER;
import static com.android.mysimplecalendar.utils.AppUtils.PREF_SELECTED_DATE_HOME;
import static com.android.mysimplecalendar.utils.AppUtils.PREF_SELECTED_NOTIFICATION_ID_HOME;


public class MainActivity extends AppCompatActivity implements DroidCalendarView.DroidCalendarListener, DataListener, View.OnClickListener {
    private DroidCalendarView calendarView;
    private Calendar currentCalendar = Calendar.getInstance();
    private String currentMonthStartDate, currentMonthEndDate;
    private TextView noRemindersTextView;
    private SuperscriptFormatter superscriptFormatter;
    private RecyclerView calenderRecyclerView;
    private PreferencesManager prefManager;
    private final SimpleDateFormat ymd_date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat dmy_date_format = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
    private final SimpleDateFormat month_format = new SimpleDateFormat("MMM", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onDayClick(Date date) {
        prefManager.setStringValue(PREF_SELECTED_DATE_HOME, "");
        getReminders(ymd_date_format.format(date));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.noRemindersTextView || id == R.id.addReminderImageView) {
            AppUtils.navigateUtils(MainActivity.this, AddReminderActivity.class);
        }
    }

    @Override
    public void onDayLongClick(Date date) {

    }

    @Override
    public void sendData(int action, Object data) {
        if (data != null) {
            if (action == ACTION_EDIT_REMINDER) {
                prefManager.setStringValue(PREF_SELECTED_NOTIFICATION_ID_HOME, (String) data);
                AppUtils.navigateUtils(MainActivity.this, AddReminderActivity.class);
            }
        }

    }

    @Override
    public void onRightButtonClick(Calendar calendar) {
        currentCalendar = calendar;
        prefManager.setStringValue(PREF_SELECTED_DATE_HOME, "");
        getStartAndEndDateOfMonth(currentCalendar);
        getAllReminderDots(currentMonthStartDate, currentMonthEndDate);
        isCurrentMonth();

    }

    @Override
    public void onLeftButtonClick(Calendar calendar) {
        currentCalendar = calendar;
        prefManager.setStringValue(PREF_SELECTED_DATE_HOME, "");
        getStartAndEndDateOfMonth(currentCalendar);
        getAllReminderDots(currentMonthStartDate, currentMonthEndDate);
        isCurrentMonth();

    }

    private void isCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        String cMonth = month_format.format(calendar.getTime());
        if (!cMonth.equals(month_format.format(currentCalendar.getTime()))) {
            noRemindersTextView.setVisibility(View.GONE);
            calenderRecyclerView.setVisibility(View.GONE);
        } else {
            getReminders(ymd_date_format.format(new Date()));
        }
    }

    private void getStartAndEndDateOfMonth(Calendar calendar) {
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        currentMonthStartDate = ymd_date_format.format(calendar.getTime());
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        currentMonthEndDate = ymd_date_format.format(calendar.getTime());
    }

    private void initializeViews() {
        superscriptFormatter = new SuperscriptFormatter(new SpannableStringBuilder());
        prefManager = PreferencesManager.getInstance(this);
        calendarView = findViewById(R.id.droidCalendarPicker);
        noRemindersTextView = findViewById(R.id.noRemindersTextView);
        ImageView addReminderImageView = findViewById(R.id.addReminderImageView);
        calenderRecyclerView = findViewById(R.id.calenderRecyclerView);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        calenderRecyclerView.setLayoutManager(layoutManager1);
        calenderRecyclerView.setHasFixedSize(true);
        calendarView.setDroidCalendarListener(this);
        calendarView.setShortWeekDays(false);
        calendarView.showDateTitle(true);
        addReminderImageView.setOnClickListener(this);
        noRemindersTextView.setOnClickListener(this);
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentYear = currentCalendar.get(Calendar.YEAR);
        getStartAndEndDateOfMonth(currentCalendar);
        calendarView.setDate(new Date(), currentMonth, currentYear);
        prefManager.setStringValue(PREF_SELECTED_DATE_HOME, "");
        getReminders(ymd_date_format.format(new Date()));
        getAllReminderDots(currentMonthStartDate, currentMonthEndDate);
    }

    private void getReminders(String date) {
        date = !prefManager.getStringValue(PREF_SELECTED_DATE_HOME).isEmpty() ? prefManager.getStringValue(PREF_SELECTED_DATE_HOME) : date;
        prefManager.setStringValue(PREF_SELECTED_DATE_HOME, date);
        prefManager.setStringValue(PREF_SELECTED_NOTIFICATION_ID_HOME, "");
        List<NotificationModel> notificationModelList = new ArrayList<>();
        String currentFormDate = AppUtils.getCurrentDate();
        List<MyNotifications> myNotificationsList = new StringQuery<>(MyNotifications.class, "SELECT * From MyNotifications WHERE date(ReminderDateTime) = '" + date + "' ORDER BY datetime(ReminderDateTime) ASC").queryList();
        for (int n = 0; n < myNotificationsList.size() && myNotificationsList.size() > 0; n++) {
            MyNotifications myNotifications = myNotificationsList.get(n);
            boolean isEditable = true;
            if (((myNotifications.getReminderDateTime())).compareTo(currentFormDate) < 0) {
                isEditable = false;
            }
            notificationModelList.add(new NotificationModel(myNotifications.getReminderDateTime(), myNotifications.getID(), myNotifications.getCreatedDateTime(), myNotifications.getReminderTitle(), myNotifications.getReminderDetails(), myNotifications.getReminderTime(), myNotifications.isFavorite(), isEditable, false));
        }
        if (notificationModelList.size() > 0) {
            noRemindersTextView.setVisibility(View.GONE);
            calenderRecyclerView.setVisibility(View.VISIBLE);
            RemindersAdapter settingsAdapter = new RemindersAdapter(this, this, notificationModelList);
            calenderRecyclerView.setAdapter(settingsAdapter);
        } else {
            calenderRecyclerView.setVisibility(View.GONE);
            noRemindersTextView.setVisibility(View.VISIBLE);
            removeMarker(date);
            noRemindersTextView.setText("No events on ".concat(AppUtils.getSuperscriptFormatter(AppUtils.callDateFormatChangeMethod(date, ymd_date_format, dmy_date_format))));
            superscriptFormatter.format(noRemindersTextView);
        }
    }

    private void getAllReminderDots(String startDate, String endDate) {
        try {
            List<MyNotifications> myNotificationsList = new StringQuery<>(MyNotifications.class, "SELECT strftime('%Y-%m-%d', ReminderDateTime) as ReminderDateTime from MyNotifications Where date(ReminderDateTime) BETWEEN '" + startDate + "' AND '" + endDate + "' GROUP BY date(ReminderDateTime) ORDER BY datetime(CreatedDateTime) ASC").queryList();
            for (int n = 0; n < myNotificationsList.size() && myNotificationsList.size() > 0; n++) {
                Date date1 = null;
                try {
                    date1 = ymd_date_format.parse(myNotificationsList.get(n).getReminderDateTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(Objects.requireNonNull(date1));
                if (calendar1.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
                    calendarView.addMarkerInCalendarView(calendar1.getTime());
                }
            }
        } catch (Exception e) {
            AppUtils.printLogConsole("##getAllReminderDots", "----->" + e.getMessage());
        }
    }

    private void removeMarker(String cDate) {
        try {
            Calendar calendar1 = Calendar.getInstance();
            try {
                calendar1.setTime(Objects.requireNonNull(ymd_date_format.parse(cDate)));
                if (calendar1.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
                    calendarView.removeMarkerInCalendarView(calendar1.getTime());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            AppUtils.printLogConsole("##removeMarker", "----->" + e.getMessage());
        }
    }

}
