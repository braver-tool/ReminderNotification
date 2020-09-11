package com.calendar.android.droidcalendar;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hariharan Eswaran on 01/01/2019
 */


public interface DroidCalendarListener {
    void onDayClick(Date date);

    void onDayLongClick(Date date);

    void onRightButtonClick(Calendar calendar);

    void onLeftButtonClick(Calendar calendar);
}
