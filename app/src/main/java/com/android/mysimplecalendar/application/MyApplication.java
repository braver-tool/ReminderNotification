/*
 * Copyright 2019 ~ https://github.com/braver-tool
 */

package com.android.mysimplecalendar.application;

import android.app.Application;

import com.android.mysimplecalendar.localdb.LocalDataBase;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;


public class MyApplication extends Application {
    public static final boolean IS_DEBUG = true;
    private static MyApplication INSTANCE;

    @Override
    public void onCreate() {
        INSTANCE = this;
        FlowManager.init(new FlowConfig.Builder(this)
                //.addDatabaseConfig(LocalDataBase.getConfig(LocalDataBase.class))
                .build());
        super.onCreate();
    }

    public static MyApplication getInstance() {
        return INSTANCE;
    }
}
