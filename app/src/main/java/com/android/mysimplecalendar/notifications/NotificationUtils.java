package com.android.mysimplecalendar.notifications;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.android.mysimplecalendar.localdb.MyNotifications;
import com.android.mysimplecalendar.localdb.MyNotifications_Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Hariharan Eswaran on 01/01/2019
 */
@SuppressLint("NewApi")
public class NotificationUtils {
    private final Context mContext;
    private final AlarmManager mAlarmManager;
    private static final Gson gson = new GsonBuilder().create();


    public NotificationUtils(Context context) {
        mContext = context;
        mAlarmManager = mContext.getSystemService(AlarmManager.class);
    }

    /**
     * Schedules an alarm using {@link AlarmManager}.
     *
     * @param myNotifications the alarm to be scheduled
     */
    public void scheduleNotificationModel(MyNotifications myNotifications) {
        String data = gson.toJson(myNotifications, MyNotifications.class);
        Intent intent = new Intent(mContext, LocalNotificationReceiver.class);
        intent.putExtra("DATA", data);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, myNotifications.getNotificationID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar alarmTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = timeFormat.parse(myNotifications.getReminderDateTime());
            if (date != null) {
                alarmTime.setTime(date);
            }
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
            SQLite.update(MyNotifications.class)
                    .set(MyNotifications_Table.isScheduled.eq(true))
                    .where(MyNotifications_Table.ID.eq(myNotifications.getID()))
                    .async()
                    .execute();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public void clearLocalNotification(Context context, int notificationID) {
        Intent intent = new Intent(context, LocalNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);

    }
}

