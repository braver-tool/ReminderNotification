/*
 * Copyright 2019 ~ https://github.com/braver-tool
 */

package com.android.mysimplecalendar.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.android.mysimplecalendar.R;
import com.android.mysimplecalendar.utils.AppUtils;
import com.calendar.android.calendar.CalendarListener;
import com.calendar.android.calendar.CustomCalendarView;
import com.calendar.android.calendar.utils.CalendarUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;



public class CalenderDialogFragment extends DialogFragment {
    private static final String TAG = CalenderDialogFragment.class.getSimpleName();
    private onDateSelected onDateSelected;
    //private SimpleDateFormat mdy_date_format_1 = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
    private SimpleDateFormat ymd_date_format_1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private String currentDate;

    public CalenderDialogFragment(onDateSelected onDateSelected, String selectedDate) {
        this.onDateSelected = onDateSelected;
        this.currentDate = selectedDate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_simple_calendar, container, false);
        initializeViews(v);
        return v;
    }

    private void initializeViews(View v) {
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        getDialog().getWindow().setAttributes(p);
        //ImageView calendarUpArrow = v.findViewById(R.id.calenderUpArrowGreen);
        //RelativeLayout calendarParent = v.findViewById(R.id.calenderParent);
        final Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        if (currentDate.isEmpty()) {
            currentDate = ymd_date_format_1.format(currentCalendar.getTime());
        }
        Date date = null;
        try {
            date = ymd_date_format_1.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final CustomCalendarView calendarView = v.findViewById(R.id.calendar_view);

        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setShowOverflowDate(false);
        currentCalendar.setTime(date);
        calendarView.refreshCalendar(currentCalendar);
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                VitalsDateGetter(date);
            }

            @Override
            public void onMonthChanged(Date date) {
                Log.e(TAG, "onMonthChanged:" + ymd_date_format_1.format(date));
            }
        });
    }

    private void VitalsDateGetter(Date date) {
        if (CalendarUtils.isPastDay(date)) {
            AppUtils.showAlertMsgDialog("Please Select valid date!!", getContext());
        } else {
            onDateSelected.sendData(ymd_date_format_1.format(date));
            getDialog().dismiss();
        }
    }

    public interface onDateSelected {
        void sendData(String date);
    }
}
