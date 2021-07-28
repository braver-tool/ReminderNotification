/*
 * Copyright 2019 ~ https://github.com/braver-tool
 */

package com.android.mysimplecalendar.notifications;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.mysimplecalendar.R;
import com.android.mysimplecalendar.activities.MainActivity;
import com.android.mysimplecalendar.localdb.MyNotifications;
import com.android.mysimplecalendar.localdb.MyNotifications_Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.NotificationManager.IMPORTANCE_HIGH;


public class LocalNotificationReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "mycalendar_channel_id";
    public static final String CHANNEL_NAME = "mycalendar_channel_name";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            scheduleNotification(context, intent);
        }
    }

    /**
     * @param context - application context
     * @param intent  - action data
     *                Method used to schedule local notification
     */
    private void scheduleNotification(Context context, Intent intent) {
        try {
            Gson gson = new GsonBuilder().create();
            String responseData = intent.getStringExtra("DATA");
            MyNotifications myNotifications = gson.fromJson(responseData, MyNotifications.class);
            //Navigation
            Intent notificationIntent;
            notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            String title = "You scheduled " + myNotifications.getReminderTitle() + " at " + myNotifications.getReminderTime();
            Notification notification = builder.setContentTitle(title)
                    .setSmallIcon(getNotificationIcon())
                    .setContentText(myNotifications.getReminderDetails())
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_logo))
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH).build();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH);
                builder.setChannelId(CHANNEL_ID);
                builder.setBadgeIconType(Notification.BADGE_ICON_SMALL);
                AudioAttributes att = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
                channel.setDescription(CHANNEL_NAME);
                channel.setSound(defaultSoundUri, att);
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{1000, 1000, 1000});
                channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
            }
            Objects.requireNonNull(notificationManager).notify(myNotifications.getNotificationID(), notification);
        } catch (NullPointerException | IllegalStateException e) {
            Log.d("##Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * Schedules an alarm using {@link AlarmManager}.
     *
     * @param myNotifications the alarm to be scheduled
     */
    public void scheduleNotificationModel(Context context, MyNotifications myNotifications) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        String data = new Gson().toJson(myNotifications, MyNotifications.class);
        Intent intent = new Intent(context, LocalNotificationReceiver.class);
        intent.putExtra("DATA", data);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, myNotifications.getNotificationID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar alarmTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date;
        try {
            date = timeFormat.parse(myNotifications.getReminderDateTime());
            if (date != null) {
                alarmTime.setTime(date);
            }
            if (mAlarmManager != null) {
                if (Build.VERSION.SDK_INT >= 23) {
                    mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
                } else {
                    mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
                }
            }
            SQLite.update(MyNotifications.class)
                    .set(MyNotifications_Table.isScheduled.eq(true))
                    .where(MyNotifications_Table.ID.eq(myNotifications.getID()))
                    .async()
                    .execute();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context        - current screen
     * @param notificationID - int data
     *                       Method used to cancel scheduled notification
     */
    public void clearLocalNotification(Context context, int notificationID) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LocalNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (mAlarmManager != null) {
            mAlarmManager.cancel(pendingIntent);
        }
    }


    /**
     * Method is return boolean values based on app is foreground or Background
     *
     * @return boolean
     */
    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = null;
        if (activityManager != null) {
            appProcesses = activityManager.getRunningAppProcesses();
        }
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Method is return the notification icon based on build version
     *
     * @return icon
     */
    public static int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_alarm_white : R.mipmap.app_logo;
    }
}
