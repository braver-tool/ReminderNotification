/*
 *
 *  * Created by https://github.com/braver-tool on 11/09/20, 03:30 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 05/04/22, 11:00 AM
 *
 */

package com.android.mysimplecalendar.localdb;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MCNotification.class}, version = 1, exportSchema = false)
public abstract class NotificationDatabase extends RoomDatabase {
    public abstract DaoInterface daoInterface();

}
