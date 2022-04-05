/*
 *
 *  * Created by https://github.com/braver-tool on 11/09/20, 03:30 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 05/04/22, 11:00 AM
 *
 */

package com.android.mysimplecalendar.activities;


import static com.android.mysimplecalendar.utils.AppUtils.ACTION_LOCAL_NOTIFICATION;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mysimplecalendar.application.MyApplication;

public class TransparentActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            final String action = intent.getAction();
            if (action != null && intent.getAction().contains(ACTION_LOCAL_NOTIFICATION)) {
                //String notificationDetailData = intent.getStringExtra("DATA");
                Intent notificationIntent = new Intent(this, MainActivity.class);
                if (MyApplication.applicationBackgrounded.get()) {
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(notificationIntent);
                finish();
            }
        }
    }

}