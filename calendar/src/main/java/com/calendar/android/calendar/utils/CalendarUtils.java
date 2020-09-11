package com.calendar.android.calendar.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hariharan Eswaran on 01/01/2019
 */

public class CalendarUtils {

    public static boolean isSameMonth(Calendar c1, Calendar c2) {
        if (c1 != null || c2 != null) {
            return (c1.get(Calendar.ERA) == c2.get(Calendar.ERA)
                    && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                    && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH));
        } else {
            return false;
        }


    }


    public static boolean isPastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return date.before(calendar.getTime());
    }


}
