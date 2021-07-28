/*
 * Copyright 2019 ~ https://github.com/braver-tool
 */

package com.android.mysimplecalendar.localdb;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


@Table(database = LocalDataBase.class)
public class MyNotifications extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    private int NotificationID;
    @Column
    private String ReminderDateTime;
    @Column
    private String ID;

    public int getNotificationID() {
        return NotificationID;
    }

    public void setNotificationID(int notificationID) {
        NotificationID = notificationID;
    }

    @Column
    private String CreatedDateTime;
    @Column
    private String ReminderTitle;

    public String getReminderTime() {
        return ReminderTime;
    }

    public void setReminderTime(String reminderTime) {
        ReminderTime = reminderTime;
    }

    @Column
    private String ReminderDetails;

    @Column
    private boolean IsFavorite;

    @Column
    private String ReminderTime;

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    @Column
    private boolean isScheduled;

    public int getAlertBefore() {
        return alertBefore;
    }

    public void setAlertBefore(int alertBefore) {
        this.alertBefore = alertBefore;
    }

    @Column
    private int alertBefore;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getReminderDateTime() {
        return ReminderDateTime;
    }

    public void setReminderDateTime(String reminderDateTime) {
        ReminderDateTime = reminderDateTime;
    }

    public String getCreatedDateTime() {
        return CreatedDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        CreatedDateTime = createdDateTime;
    }

    public String getReminderTitle() {
        return ReminderTitle;
    }

    public void setReminderTitle(String reminderTitle) {
        ReminderTitle = reminderTitle;
    }

    public String getReminderDetails() {
        return ReminderDetails;
    }

    public void setReminderDetails(String reminderDetails) {
        ReminderDetails = reminderDetails;
    }

    public boolean isFavorite() {
        return IsFavorite;
    }

    public void setFavorite(boolean favorite) {
        IsFavorite = favorite;
    }

    public MyNotifications() {

    }

    public MyNotifications(String ID, String createdDateTime, String reminderDateTime, String reminderTitle, String reminderDetails, int alertBeforeTime, String remTime, boolean isScheduledNoti, boolean isFav) {
        this.ID = ID;
        ReminderDateTime = reminderDateTime;
        CreatedDateTime = createdDateTime;
        ReminderTitle = reminderTitle;
        ReminderDetails = reminderDetails;
        ReminderTime = remTime;
        isScheduled = isScheduledNoti;
        IsFavorite = isFav;
        alertBefore = alertBeforeTime;
    }
}
