/*
 *
 *  * Created by https://github.com/braver-tool on 11/09/20, 03:30 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 05/04/22, 11:00 AM
 *
 */

package com.android.mysimplecalendar.localdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "MCNotification")
public class MCNotification implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int NotificationID;
    @ColumnInfo(name = "ID")
    private String ID;
    @ColumnInfo
    private String ReminderDateTime;
    @ColumnInfo
    private String CreatedDateTime;
    @ColumnInfo
    private String ReminderTitle;
    @ColumnInfo
    private String ReminderDetails;
    @ColumnInfo
    private boolean isFavorite;
    @ColumnInfo
    private String ReminderTime;
    @ColumnInfo
    private boolean isScheduled;
    @ColumnInfo
    private int alertBefore;
    @ColumnInfo
    private boolean isEditable;
    @ColumnInfo
    private boolean isDeletable;

    public int getNotificationID() {
        return NotificationID;
    }

    public void setNotificationID(int notificationID) {
        NotificationID = notificationID;
    }

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
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getReminderTime() {
        return ReminderTime;
    }

    public void setReminderTime(String reminderTime) {
        ReminderTime = reminderTime;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public int getAlertBefore() {
        return alertBefore;
    }

    public void setAlertBefore(int alertBefore) {
        this.alertBefore = alertBefore;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean deletable) {
        isDeletable = deletable;
    }

    public MCNotification() {
    }

    public MCNotification(String ID, String createdDateTime, String reminderDateTime, String reminderTitle, String reminderDetails, int alertBefore, String reminderTime, boolean isScheduled, boolean isFavorite, boolean isEditable, boolean isDeletable) {
        this.ID = ID;
        ReminderDateTime = reminderDateTime;
        CreatedDateTime = createdDateTime;
        ReminderTitle = reminderTitle;
        ReminderDetails = reminderDetails;
        this.isFavorite = isFavorite;
        ReminderTime = reminderTime;
        this.isScheduled = isScheduled;
        this.alertBefore = alertBefore;
        this.isEditable = isEditable;
        this.isDeletable = isDeletable;
    }

}
