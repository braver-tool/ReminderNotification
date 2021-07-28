/*
 * Copyright 2019 ~ https://github.com/braver-tool
 */

package com.android.mysimplecalendar.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.mysimplecalendar.R;
import com.android.mysimplecalendar.application.MyApplication;
import com.android.mysimplecalendar.listener.DataListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class AppUtils {
    public static final int ACTION_EDIT_REMINDER = 111;
    public static final int ACTION_OK_BUTTON_FROM_ALERT_POPUP = 112;
    public static final int ACTION_CANCEL_BUTTON_FROM_ALERT_POPUP = 113;

    public static final String PREF_SELECTED_DATE_HOME = "pref_selected_date_home";
    public static final String PREF_SELECTED_NOTIFICATION_ID_HOME = "pref_selected_notification_id_home";

    public static final String DELETE_REMINDER_ALERT = "Are you sure want to delete this reminder?";
    public static final String FAVORITE_REMINDER_ALERT = "Are you sure want to add this reminder in favorite?";
    public static final String NOT_EDITABLE_REMINDER_ALERT = "Can\'t edit this reminder!";


    public static SimpleDateFormat normal_time_format_1 = new SimpleDateFormat("h:mm a", Locale.getDefault());


    /**
     * Method handle to navigate one activity to another activity without finish
     *
     * @param fromActivity   - Current Activity
     * @param targetActivity - destination activity
     */
    public static void navigateUtils(Activity fromActivity, Class targetActivity) {
        fromActivity.startActivity(new Intent(fromActivity, targetActivity));
        fromActivity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }


    /**
     * Method handle to navigate one activity to another activity with finish activity
     *
     * @param fromActivity   - Current Activity
     * @param targetActivity - destination activity
     */
    public static void navigateUtilsWidFinish(Activity fromActivity, Class targetActivity) {
        fromActivity.startActivity(new Intent(fromActivity, targetActivity));
        fromActivity.finish();
        fromActivity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * @param msg - Alert Message --> DialogView contains Single Button 'OK' and a Message
     */

    public static void showAlertMsgDialog(String msg, Context context) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.popup_alert_single_button);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            }
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp;
            if (window != null) {
                wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
            }
            TextView alertDialogOkTextView = dialog.findViewById(R.id.alertDialogOkTextView);
            TextView alertMsgTextView = dialog.findViewById(R.id.alertMsgTextView);
            alertMsgTextView.setText(msg);
            alertDialogOkTextView.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    /**
     * @param context      - Dialog call from
     * @param dataListener - Mapping class from {@link DataListener}
     * @param alertMsg     - Alert Message --> DialogView contains Two Button widgets --> 'OK','Cancel' and a message
     *                     Method handle Alert Dialog box with Two Button widgets
     */
    public static void showAlertDialogWidTwoWidget(Context context, DataListener dataListener, String alertMsg) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_alert_double_button);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        }
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp;
        if (window != null) {
            wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
        }
        TextView alertDialogOkTextView = dialog.findViewById(R.id.commonAlertTwoDialogueOkText);
        TextView commonAlertTwoDialogueCancelText = dialog.findViewById(R.id.commonAlertTwoDialogueCancelText);
        TextView alertMsgTextView = dialog.findViewById(R.id.commonAlertTwoDialogueHeaderText);
        alertMsgTextView.setText(alertMsg);
        commonAlertTwoDialogueCancelText.setOnClickListener(v -> {
            dataListener.sendData(ACTION_CANCEL_BUTTON_FROM_ALERT_POPUP, true);
            dialog.dismiss();
        });
        alertDialogOkTextView.setOnClickListener(v -> {
            dataListener.sendData(ACTION_OK_BUTTON_FROM_ALERT_POPUP, true);
            dialog.dismiss();
        });
        dialog.show();
    }

    /**
     * Method used to get Current date
     *
     * @return current date
     */
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date c = Calendar.getInstance().getTime();
        return dateFormat.format(c);
    }

    /**
     * Method used to get given date format into change format
     *
     * @param givenDate     - date string
     * @param givenFormat   - current date format
     * @param changedFormat - changed date format
     * @return changed date format
     */
    public static String callDateFormatChangeMethod(String givenDate, SimpleDateFormat givenFormat, SimpleDateFormat changedFormat) {
        Date resultDate;
        try {
            resultDate = givenFormat.parse(givenDate);
        } catch (ParseException e) {
            e.printStackTrace();
            String str = android.os.Build.MODEL;

            if (str.contains("Moto") && (givenDate.toLowerCase().contains("am") || givenDate.toLowerCase().contains("pm"))) {
                if (givenFormat.equals(normal_time_format_1) && givenDate.length() == 7) {
                    givenDate = "0".concat(givenDate);
                } else {
                    givenDate = givenDate.substring(0, 1).equals("0") ? givenDate.substring(1) : givenDate;
                }
            }
            return givenDate;
        }
        return changedFormat.format(resultDate);
    }

    /**
     * Method used to convert normal time format to Railway time format
     *
     * @param dateString - date string
     * @return changed time
     */
    public static String convertTo24Hour(String dateString) {
        String result = "";
        if (!dateString.isEmpty()) {
            //dateString = dateString.replaceAll("\\s", "");
            SimpleDateFormat formatterOld = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date = null;
            try {
                date = formatterOld.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat formatterNew = new SimpleDateFormat("HH:mm", Locale.getDefault());
            if (date == null) {
                result = motoTimeTo24Hr(dateString);
            } else {
                result = formatterNew.format(date);
            }
        }
        return result;
    }

    /**
     * Method for MOTO devices ----> Time conversion
     *
     * @param dateString - date string
     * @return changed time
     */
    private static String motoTimeTo24Hr(String dateString) {
        String result = "";
        if (!dateString.isEmpty()) {
            String format = dateString.substring(6, 8);
            if ((format.equals("PM") || (format.equals("pm")))) {
                int hour = Integer.parseInt(dateString.substring(0, 2));
                String hhh = String.valueOf(hour + 12);
                String minute = dateString.substring(3, 5);
                result = hhh.concat(":").concat(minute);
            } else {
                result = dateString.substring(0, 5);
            }
        }
        return result;
    }

    /**
     * Method used to set reminder with before time in Add screen
     *
     * @param cTime      - current time as string
     * @param addedValue - add reminder value
     * @return - added time
     */
    public static String addTimeInMinutes(String cTime, int addedValue) {
        String resultTime;
        SimpleDateFormat railwayTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = railwayTimeFormat.parse(cTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        if (addedValue != 0) {
            calendar.add(Calendar.MINUTE, -addedValue);
        }
        resultTime = railwayTimeFormat.format(calendar.getTime());
        return resultTime;

    }

    /**
     * Method used to set reminder with before time in Edit screen
     *
     * @param cTime      - current time as string
     * @param addedValue - add reminder value
     * @return - added time
     */
    public static String addTimeInMinutesForEdit(String cTime, int addedValue) {
        String resultTime;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat outPutFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = inputFormat.parse(cTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        if (addedValue != 0) {
            calendar.add(Calendar.MINUTE, addedValue);
        }
        resultTime = outPutFormat.format(calendar.getTime());
        return resultTime;

    }

    /**
     * Method convert date with 'rd','th','st' value
     *
     * @param createdDate - date string
     * @return - date with Superscript
     */
    public static String getSuperscriptFormatter(String createdDate) {
        String date = createdDate.substring(5, 6);
        String date2 = createdDate.substring(4, 5);
        String date1 = createdDate.substring(4, 6);
        String mesure;
        switch (date) {
            case "1":
                if (date.equals(date2)) {
                    mesure = "th";
                } else {
                    mesure = "st";
                }
                break;
            case "2":
                if (date2.equals("1")) {
                    mesure = "th";
                } else {
                    mesure = "nd";
                }
                break;
            case "3":
                if (date2.equals("1")) {
                    mesure = "th";
                } else {
                    mesure = "rd";
                }
                break;
            default:
                mesure = "th";
                break;
        }
        String month = createdDate.substring(0, 3);
        String year = createdDate.substring(createdDate.length() - 4);
        mesure = month.concat(" ").concat(date1).concat(mesure).concat(" ").concat(year);
        String ddd = date2.equals("0") ? mesure.substring(5) : mesure.substring(4);
        mesure = month.concat(" ").concat(ddd);
        return mesure;
    }

    /**
     * @param tag - Contains class name
     * @param msg - Log message as String
     *            Method used to print log in console for development
     */
    public static void printLogConsole(String tag, String msg) {
        if (MyApplication.IS_DEBUG) {
            Log.d(tag, msg);
        }
    }
}