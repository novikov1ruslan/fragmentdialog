package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;

import org.commons.logger.Ln;

import java.util.concurrent.TimeUnit;

public class DigitalTimerView extends TextView implements TimerViewInterface {
    private static final String DEFAULT_TEXT = "00:00";

    private final int mNormalColor;
    private final int mAlarmColor;

    private int mAlarmTime;
    private int mTime;
    private final Paint mPaint;

    public DigitalTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "digital_font.ttf"));
        setText(DEFAULT_TEXT);
        mNormalColor = getResources().getColor(R.color.main);
        mAlarmColor = getResources().getColor(R.color.hit);
        setTextColor(mNormalColor);
        mPaint = new Paint();
    }

    private void updateColors() {
        setTextColor(mTime > mAlarmTime ? mNormalColor : mAlarmColor);
    }

    private String format(int millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }


    @Override
    public void setTotalTime(int time) {

    }

    @Override
    public void setCurrentTime(int time) {
        mTime = time;
        updateColors();
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

        mPaint.setTextSize(textSize);
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        if (width <= 0) {
            return;
        }
        float measuredText = mPaint.measureText(DEFAULT_TEXT);
        if (measuredText > width) {
            textSize = (textSize * width) / measuredText;
        }

        // fine tuning
        while (mPaint.measureText(DEFAULT_TEXT) > width) {
            if (textSize < 8) {
                break;
            }
            mPaint.setTextSize(--textSize);
        }
//        Ln.v("w=" + getMeasuredWidth() + "; h=" + getMeasuredHeight() + "; size=" + textSize);

        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
}
