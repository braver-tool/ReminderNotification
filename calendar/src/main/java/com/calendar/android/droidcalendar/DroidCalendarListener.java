/*
 *
 *  * Created by https://github.com/braver-tool on 11/09/20, 03:30 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 05/04/22, 11:00 AM
 *
 */

package com.calendar.android.droidcalendar;

import java.util.Calendar;
import java.util.Date;




public interface DroidCalendarListener {
    void onDayClick(Date date);

    void onDayLongClick(Date date);

    void onRightButtonClick(Calendar calendar);

    void onLeftButtonClick(Calendar calendar);
}
