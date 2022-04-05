/*
 *
 *  * Created by https://github.com/braver-tool on 11/09/20, 03:30 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 05/04/22, 11:00 AM
 *
 */

package com.android.mysimplecalendar.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mysimplecalendar.utils.AppUtils;


public class LaunchActivity extends AppCompatActivity {

    private boolean isDataReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int SPLASH_TIME_OUT = 1000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setupOnPreDrawListenerToRootView();
            new Handler(Looper.getMainLooper()).postDelayed(() -> isDataReady = true, SPLASH_TIME_OUT);
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> AppUtils.navigateUtilsWidFinish(LaunchActivity.this, MainActivity.class), SPLASH_TIME_OUT);
        }
    }


    /**
     * Method handle to validate android 12 splash screen dismiss
     */
    private void setupOnPreDrawListenerToRootView() {
        View mViewContent = findViewById(android.R.id.content);
        mViewContent.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        if (isDataReady) {
                            mViewContent.getViewTreeObserver().removeOnPreDrawListener(this);
                            AppUtils.navigateUtilsWidFinish(LaunchActivity.this, MainActivity.class);
                            return true;
                        } else {
                            // The content is not ready; suspend.
                            return false;
                        }
                    }
                });
    }
}