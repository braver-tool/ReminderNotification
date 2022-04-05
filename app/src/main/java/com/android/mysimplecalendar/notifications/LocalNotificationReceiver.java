/*
 *
 *  * Created by https://github.com/braver-tool on 11/09/20, 03:30 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 05/04/22, 11:00 AM
 *
 */

package com.android.mysimplecalendar.notifications;

import static android.app.AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED;
import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;
import static com.android.mysimplecalendar.utils.AppUtils.ACTION_LOCAL_NOTIFICATION;
import static com.android.mysimplecalendar.utils.AppUtils.IS_ALREADY_SHOWN_ALARM_PERMISSION_INTENT;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
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
import com.android.mysimplecalendar.activities.TransparentActivity;
import com.android.mysimplecalendar.localdb.MCNotification;
import com.android.mysimplecalendar.localdb.NotificationRepository;
import com.android.mysimplecalendar.utils.AppUtils;
import com.android.mysimplecalendar.utils.PreferencesManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class LocalNotificationReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "braver_channel_id";
    public static final String CHANNEL_NAME = "braver_channel_name";
    private MCNotification mcNotificationFromPermission;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED)) {
            if (mcNotificationFromPermission != null) {
                scheduleNotificationModel(context, mcNotificationFromPermission);
            }
        } else if (intent.getAction() != null && intent.getAction().contains(ACTION_LOCAL_NOTIFICATION) && intent.getExtras() != null) {
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
            MCNotification mcNotification = gson.fromJson(responseData, MCNotification.class);
            //Navigation
            Intent notificationIntent = new Intent();
            notificationIntent.setComponent(new ComponentName(context, TransparentActivity.class));
            notificationIntent.setAction(ACTION_LOCAL_NOTIFICATION + "(" + System.currentTimeMillis() + ")");
            notificationIntent.putExtra("DATA", responseData);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            String title = "You scheduled " + mcNotification.getReminderTitle() + " at " + mcNotification.getReminderTime();
            Notification notification = builder.setContentTitle(title)
                    .setSmallIcon(getNotificationIcon())
                    .setContentText(mcNotification.getReminderDetails())
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
            Objects.requireNonNull(notificationManager).notify(mcNotification.getNotificationID(), notification);
        } catch (NullPointerException | IllegalStateException e) {
            Log.d("##Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * Schedules an alarm using {@link AlarmManager}.
     *
     * @param mcNotification the alarm to be scheduled
     */
    public void scheduleNotificationModel(Context context, MCNotification mcNotification) {
        PreferencesManager prefManager = PreferencesManager.getInstance(context);
        NotificationRepository notificationRepository = new NotificationRepository(context);
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        String data = new Gson().toJson(mcNotification, MCNotification.class);
        Intent intent = new Intent(context, LocalNotificationReceiver.class);
        intent.setAction(ACTION_LOCAL_NOTIFICATION + "(" + System.currentTimeMillis() + ")");
        intent.putExtra("DATA", data);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, mcNotification.getNotificationID(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        Calendar alarmTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date;
        try {
            date = timeFormat.parse(mcNotification.getReminderDateTime());
            if (date != null) {
                alarmTime.setTime(date);
            }
            if (mAlarmManager != null) {
                long scheduledTime = alarmTime.getTimeInMillis();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (mAlarmManager.canScheduleExactAlarms()) {
                        AppUtils.printLogConsole("##LocalPN##AlarmManager", "------------>" + scheduledTime);
                        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent);
                    } else {
                        if (!prefManager.getBooleanValue(IS_ALREADY_SHOWN_ALARM_PERMISSION_INTENT)) {
                            prefManager.setBooleanValue(IS_ALREADY_SHOWN_ALARM_PERMISSION_INTENT, true);
                            mcNotificationFromPermission = mcNotification;
                            AppUtils.printLogConsole("##LocalPN##AlarmManager", "------------>Android 12 and above need special access for SCHEDULE_EXACT_ALARM");
                            context.startActivity(new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
                        } else {
                            notificationRepository.updateIsScheduleParamNotifications(mcNotification.getID(), false);
                        }
                    }
                } else {
                    AppUtils.printLogConsole("##LocalPN##AlarmManager", "------------>" + scheduledTime);
                    mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent);
                }
            }
            notificationRepository.updateIsScheduleParamNotifications(mcNotification.getID(), true);
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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        if (mAlarmManager != null) {
            mAlarmManager.cancel(pendingIntent);
        }
    }

    /**
     * Method is return the notification icon based on build version
     *
     * @return icon
     */
    public static int getNotificationIcon() {
        return R.drawable.ic_alarm_white;
    }
}
