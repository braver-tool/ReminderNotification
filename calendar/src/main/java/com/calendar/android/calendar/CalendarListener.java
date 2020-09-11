package com.calendar.android.calendar;

import java.util.Date;

/**
 * Created by Hariharan Eswaran on 01/01/2019
 */


public interface CalendarListener {
    void onDateSelected(Date date);

    void onMonthChanged(Date time);
}
