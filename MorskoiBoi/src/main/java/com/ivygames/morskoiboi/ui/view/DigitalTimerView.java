package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;

import java.util.concurrent.TimeUnit;

public class DigitalTimerView extends TextView implements TimerViewInterface {
    private static final String DEFAULT_TEXT = "00:00";

    private final int mNormalBackground;
    private final int mAlarmBackground;
    private final int mNormalColor;
    private final int mAlarmColor;

    private int mAlarmTime;
    private int mTime;

    public DigitalTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "digital_font.ttf"));
        setText(DEFAULT_TEXT);
        mNormalColor = getResources().getColor(R.color.main);
        mAlarmColor = getResources().getColor(R.color.hit);
        mNormalBackground = getResources().getColor(R.color.digital_clock_background);
        mAlarmBackground = getResources().getColor(R.color.digital_clock_alarm_background);

        setTextColor(mNormalColor);
//        setBackgroundColor(mNormalBackground);
    }

    private void update() {
        setTextColor(mTime > mAlarmTime ? mNormalColor : mAlarmColor);
//        setBackgroundColor(mTime > mAlarmTime ? mNormalBackground : mAlarmBackground);
    }

    private String format(int millis) {
        millis += 1000;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes);
        if (minutes == 2 && seconds == 1) {
            seconds = 0;
        }
        return String.format("%02d:%02d", minutes, seconds);
    }


    @Override
    public void setTotalTime(int time) {

    }

    @Override
    public void setCurrentTime(int time) {
        mTime = time;
        update();
        setText(format(time));
    }

    @Override
    public void setAlarmThreshold(int millis) {
        mAlarmTime = millis;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float textSize = 256;
        Paint paint = new Paint();

        paint.setTextSize(textSize);
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        float measuredText = paint.measureText(DEFAULT_TEXT);
        if (measuredText > width) {
            textSize = (textSize * width) / measuredText;
        }

        // fine tuning
        while (paint.measureText(DEFAULT_TEXT) > width) {
            paint.setTextSize(--textSize);
        }

        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
}
