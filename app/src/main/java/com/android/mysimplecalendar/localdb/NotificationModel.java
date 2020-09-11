package com.android.mysimplecalendar.localdb;


/**
 * Created by Hariharan Eswaran on 01/01/2019
 */
public class NotificationModel {
    private String ReminderDateTime;
    private String ID;
    private String CreatedDateTime;
    private String ReminderTitle;
    private String ReminderDetails;
    private String ReminderTime;
    private boolean isFavorite;
    private boolean isEditable;

    public String getReminderDateTime() {
        return ReminderDateTime;
    }

    public void setReminderDateTime(String reminderDateTime) {
        ReminderDateTime = reminderDateTime;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getReminderTime() {
        return ReminderTime;
    }

    public void setReminderTime(String reminderTime) {
        ReminderTime = reminderTime;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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

    private boolean isDeletable;

    public NotificationModel() {
    }

    public NotificationModel(String reminderDateTime, String ID, String createdDateTime, String reminderTitle, String reminderDetails, String reminderTime, boolean isFavorite, boolean isEditable, boolean isDeletable) {
        ReminderDateTime = reminderDateTime;
        this.ID = ID;
        CreatedDateTime = createdDateTime;
        ReminderTitle = reminderTitle;
        ReminderDetails = reminderDetails;
        ReminderTime = reminderTime;
        this.isFavorite = isFavorite;
        this.isEditable = isEditable;
        this.isDeletable = isDeletable;
    }
}
