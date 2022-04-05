/*
 *
 *  * Created by https://github.com/braver-tool on 11/09/20, 03:30 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 05/04/22, 11:00 AM
 *
 */

package com.android.mysimplecalendar.localdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoInterface {
    @Insert
    void insertTask(MCNotification mcNotification);

    @Delete
    void deleteTask(MCNotification mcNotification);

    @Query("SELECT * FROM MCNotification WHERE ID = :notificationId")
    MCNotification getNotificationFromTable(String notificationId);

    @Query("SELECT * FROM MCNotification where isScheduled = 0 AND DATETIME(ReminderDateTime) > DATETIME('now','localtime')")
    LiveData<List<MCNotification>> getNotificationListForAlarm();

    @Query("SELECT *,(Case WHEN datetime(ReminderDateTime) < DATETIME('now','localtime') THEN 0 else 1 END) as isEditable From MCNotification WHERE date(ReminderDateTime) =:selectedDate ORDER BY datetime(ReminderDateTime) ASC")
    LiveData<List<MCNotification>> getNotifications(String selectedDate);

    @Query("SELECT *,strftime('%Y-%m-%d', ReminderDateTime) as ReminderDateTime from MCNotification Where date(ReminderDateTime) BETWEEN :startDate AND :endDate GROUP BY date(ReminderDateTime) ORDER BY datetime(CreatedDateTime) ASC")
    LiveData<List<MCNotification>> getNotificationsForDotView(String startDate, String endDate);

    @Query("UPDATE MCNotification SET isScheduled= :isSchedule WHERE ID = :notificationId")
    void updateIsScheduledNotification(String notificationId, boolean isSchedule);

    @Query("UPDATE MCNotification SET isFavorite= :isFav WHERE ID = :notificationId")
    void updateIsFavoriteNotification(String notificationId, boolean isFav);
}


