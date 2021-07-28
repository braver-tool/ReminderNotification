package com.calendar.android.calendar;

import java.util.Date;




public interface CalendarListener {
    void onDateSelected(Date date);

    void onMonthChanged(Date time);
}
