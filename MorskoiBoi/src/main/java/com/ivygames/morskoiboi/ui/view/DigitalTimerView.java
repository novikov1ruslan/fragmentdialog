package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.utils.UiUtils;

import java.util.concurrent.TimeUnit;

public class DigitalTimerView extends TextView implements TimerViewInterface {
    private static final String DEFAULT_TEXT = "00:00";

    private final Paint mInnerPaint;
    private final Paint mInnerWarningPaint;
//    private final Rect mTextBounds = new Rect();

    private int mAlarmTime;
    private int mTime;

    public DigitalTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInnerPaint = UiUtils.newFillPaint(getResources(), R.color.main);
        mInnerWarningPaint = UiUtils.newFillPaint(getResources(), R.color.hit);
        setTextColor(getInnerPaint().getColor());
        Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "digital_font.ttf");
        setTypeface(myTypeface);
        setText(DEFAULT_TEXT);
    }

    private Paint getInnerPaint() {
        return mTime > mAlarmTime ? mInnerPaint : mInnerWarningPaint;
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
        setTextColor(getInnerPaint().getColor());
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
        mInnerPaint.setTextSize(textSize);
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        float measuredText = mInnerPaint.measureText(DEFAULT_TEXT);
        if (measuredText > width) {
            textSize = (textSize * width) / measuredText;
        }

        // fine tuning
        while (mInnerPaint.measureText(DEFAULT_TEXT) > width) {
            mInnerPaint.setTextSize(--textSize);
        }

        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
}
