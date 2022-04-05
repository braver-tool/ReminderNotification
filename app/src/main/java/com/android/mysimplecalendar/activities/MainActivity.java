/*
 *
 *  * Created by https://github.com/braver-tool on 11/09/20, 03:30 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 05/04/22, 11:00 AM
 *
 */

package com.android.mysimplecalendar.activities;

import static com.android.mysimplecalendar.utils.AppUtils.ACTION_EDIT_REMINDER;
import static com.android.mysimplecalendar.utils.AppUtils.PREF_SELECTED_DATE_HOME;
import static com.android.mysimplecalendar.utils.AppUtils.PREF_SELECTED_NOTIFICATION_ID_HOME;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mysimplecalendar.R;
import com.android.mysimplecalendar.adapter.RemindersAdapter;
import com.android.mysimplecalendar.listener.DataListener;
import com.android.mysimplecalendar.localdb.MCNotification;
import com.android.mysimplecalendar.localdb.NotificationRepository;
import com.android.mysimplecalendar.utils.AppUtils;
import com.android.mysimplecalendar.utils.PreferencesManager;
import com.android.mysimplecalendar.utils.SuperscriptFormatter;
import com.calendar.android.droidcalendar.DroidCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


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
    private NotificationRepository notificationRepository;
    private boolean isDataReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(() -> isDataReady = true, 800);
        setupOnPreDrawListenerToRootView();
        setContentView(R.layout.activity_main);
        initializeViews();
    }


    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            finishAndRemoveTask();
        } else {
            super.onBackPressed();
        }
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

    /**
     * Method handle to validate android 12 splash screen dismiss
     */
    private void setupOnPreDrawListenerToRootView() {
        View mViewContent = findViewById(android.R.id.content);
        mViewContent.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        if (isDataReady) {
                            mViewContent.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        } else {
                            // The content is not ready; suspend.
                            return false;
                        }
                    }
                });
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
        notificationRepository = new NotificationRepository(MainActivity.this);
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
        String finalDate = date;
        notificationRepository.getNotificationsFromTheDate(date).observe(this, notificationList -> {
            if (notificationList != null && notificationList.size() > 0) {
                showValuesOnView(notificationList);
            } else {
                calenderRecyclerView.setVisibility(View.GONE);
                noRemindersTextView.setVisibility(View.VISIBLE);
                removeMarker(finalDate);
                noRemindersTextView.setText("No events on ".concat(AppUtils.getSuperscriptFormatter(AppUtils.callDateFormatChangeMethod(finalDate, ymd_date_format, dmy_date_format))));
                superscriptFormatter.format(noRemindersTextView);
            }
        });
    }

    private void showValuesOnView(List<MCNotification> notificationModelList) {
        noRemindersTextView.setVisibility(View.GONE);
        calenderRecyclerView.setVisibility(View.VISIBLE);
        RemindersAdapter settingsAdapter = new RemindersAdapter(this, notificationRepository, this, notificationModelList);
        calenderRecyclerView.setAdapter(settingsAdapter);
    }

    private void getAllReminderDots(String startDate, String endDate) {
        try {
            notificationRepository.getNotificationsForDots(startDate, endDate).observe(this, notificationList -> {
                if (notificationList != null && notificationList.size() > 0) {
                    for (int n = 0; n < notificationList.size(); n++) {
                        Date date1 = null;
                        try {
                            date1 = ymd_date_format.parse(notificationList.get(n).getReminderDateTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTime(Objects.requireNonNull(date1));
                        if (calendar1.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
                            calendarView.addMarkerInCalendarView(calendar1.getTime());
                        }
                    }
                }
            });
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
