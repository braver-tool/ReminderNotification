/*
 * Copyright 2019 ~ https://github.com/braver-tool
 */

package com.android.mysimplecalendar.activities;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mysimplecalendar.R;
import com.android.mysimplecalendar.utils.AppUtils;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        int SPLASH_TIME_OUT = 800;
        new Handler().postDelayed(() -> AppUtils.navigateUtilsWidFinish(SplashActivity.this, MainActivity.class), SPLASH_TIME_OUT);
    }
}
