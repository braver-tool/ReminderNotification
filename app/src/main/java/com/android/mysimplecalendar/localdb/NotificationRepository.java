/*
 *
 *  * Created by https://github.com/braver-tool on 11/09/20, 03:30 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 05/04/22, 11:00 AM
 *
 */

package com.android.mysimplecalendar.localdb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

public class NotificationRepository {
    private final String DB_NAME = "NotificationDB";
    private final NotificationDatabase noteDatabase;

    public NotificationRepository(Context context) {
        noteDatabase = Room.databaseBuilder(context, NotificationDatabase.class, DB_NAME).allowMainThreadQueries().build();
    }

    // Insert
    public void insertNotifications(MCNotification mcNotification) {
        new insertTask().execute(mcNotification);
    }

    @SuppressLint("StaticFieldLeak")
    private class insertTask extends AsyncTask<MCNotification, Void, String> {

        @Override
        protected String doInBackground(MCNotification... params) {
            MCNotification note = params[0];
            noteDatabase.daoInterface().insertTask(note);
            return null;
        }
    }

    // Delete
    public void deleteNotifications(String notificationID) {
        final MCNotification singleNotification = getSingleNotification(notificationID);
        if (singleNotification != null) {
            new deleteTask().execute(singleNotification);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class deleteTask extends AsyncTask<MCNotification, Void, String> {

        @Override
        protected String doInBackground(MCNotification... params) {
            MCNotification mcNotification = params[0];
            noteDatabase.daoInterface().deleteTask(mcNotification);
            return null;
        }
    }

    //Update
    public void updateIsScheduleParamNotifications(String nID, boolean isSchedule) {
        new updateScheduleTask().execute(nID, isSchedule ? "1" : "0");
    }

    @SuppressLint("StaticFieldLeak")
    private class updateScheduleTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String ID = params[0];
            boolean isSchedule = params[1].equals("1");
            noteDatabase.daoInterface().updateIsScheduledNotification(ID, isSchedule);
            return null;
        }
    }

    public void updateIsFavParamNotifications(String nID, boolean isFav) {
        new updateFavoriteTask().execute(nID, isFav ? "1" : "0");
    }

    @SuppressLint("StaticFieldLeak")
    private class updateFavoriteTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String ID = params[0];
            boolean isFav = params[1].equals("1");
            noteDatabase.daoInterface().updateIsFavoriteNotification(ID, isFav);
            return null;
        }
    }

    // Fetch
    public MCNotification getSingleNotification(String notiId) {
        return noteDatabase.daoInterface().getNotificationFromTable(notiId);
    }

    public LiveData<List<MCNotification>> getNotificationsForScheduling() {
        return noteDatabase.daoInterface().getNotificationListForAlarm();
    }

    public LiveData<List<MCNotification>> getNotificationsFromTheDate(String date) {
        return noteDatabase.daoInterface().getNotifications(date);
    }

    public LiveData<List<MCNotification>> getNotificationsForDots(String startDate, String endDate) {
        return noteDatabase.daoInterface().getNotificationsForDotView(startDate, endDate);
    }
}
