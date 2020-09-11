package com.calendar.android.calendar;


/**
 * Created by Hariharan Eswaran on 01/01/2019
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressLint("AppCompatCustomView")
public class DayView extends TextView {
    private Date date;

    private List<DayDecorator> decorators;

    public DayView(Context context) {
        this(context, null, 0);
    }

    public DayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode())
            return;
    }

    public void bind(Date date, List<DayDecorator> decorators) {
        this.date = date;
        this.decorators = decorators;
        final SimpleDateFormat df = new SimpleDateFormat("d", Locale.getDefault());
        int day = Integer.parseInt(df.format(date));
        setText(String.valueOf(day));
    }

    public void decorate() {
        //Set custom decorators
        if (null != decorators) {
            for (DayDecorator decorator : decorators) {
                decorator.decorate(this);
            }
        }
    }

    public Date getDate() {
        return date;
    }
}