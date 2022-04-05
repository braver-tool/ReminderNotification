/*
 *
 *  * Created by https://github.com/braver-tool on 11/09/20, 03:30 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 05/04/22, 11:00 AM
 *
 */

package com.android.mysimplecalendar.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.mysimplecalendar.utils.AppUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;


public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public static final boolean IS_DEBUG = true;
    public static final AtomicBoolean applicationBackgrounded = new AtomicBoolean(true);
    private static final long INTERVAL_BACKGROUND_STATE_CHANGE = 750L;
    private static WeakReference<Activity> currentActivityReference;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void determineForegroundStatus() {
        if (applicationBackgrounded.get()) {
            MyApplication.onEnterForeground();
            applicationBackgrounded.set(false);
        }
    }

    private void determineBackgroundStatus() {
        new Handler().postDelayed(() -> {
            if (!applicationBackgrounded.get() && currentActivityReference == null) {
                applicationBackgrounded.set(true);
                onEnterBackground();
            }
        }, INTERVAL_BACKGROUND_STATE_CHANGE);
    }

    public static void onEnterForeground() {
        //This is where you'll handle logic you want to execute when your application enters the foreground
        AppUtils.printLogConsole("##ActivityLifecycleCallbacks", "------>onEnterForeground");
    }

    public static void onEnterBackground() {
        //This is where you'll handle logic you want to execute when your application enters the background
        AppUtils.printLogConsole("##ActivityLifecycleCallbacks", "------>onEnterBackground");
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        MyApplication.currentActivityReference = new WeakReference<>(activity);
        determineForegroundStatus();

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        MyApplication.currentActivityReference = null;
        determineBackgroundStatus();
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }
}
