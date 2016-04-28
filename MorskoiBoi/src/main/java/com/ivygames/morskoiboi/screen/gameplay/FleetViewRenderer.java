package com.ivygames.morskoiboi.screen.gameplay;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.utils.UiUtils;

public class FleetViewRenderer {
    private static final String WIDEST_LETTER = "4";

    private final Paint mLinePaint;
    private final Paint mTextPaint = new Paint();

    private final int mTextColor;
    private final int mZeroTextColor;
    private final float mLetterWidth;

    public FleetViewRenderer(Resources resources) {
        mLinePaint = UiUtils.newStrokePaint(resources, R.color.line);
        float textSize = resources.getDimension(R.dimen.status_text_size);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        mLetterWidth = mTextPaint.measureText(WIDEST_LETTER);

        mTextColor = resources.getColor(R.color.status_text);
        mZeroTextColor = resources.getColor(R.color.status_zero_text);
    }

    public void drawLine(Canvas canvas, int numOfMyShips, int numOfEnemyShips, int w, float unitHeight) {

        mTextPaint.setColor(getTextColor(numOfMyShips));
        int textLeft = 0;
        canvas.drawText(String.valueOf(numOfMyShips), textLeft, unitHeight, mTextPaint);

        mTextPaint.setColor(getTextColor(numOfEnemyShips));
        int textRight = (int) (w - mLetterWidth);
        canvas.drawText(String.valueOf(numOfEnemyShips), textRight, unitHeight, mTextPaint);

        canvas.drawLine(0, 0, w, 0, mLinePaint);
        canvas.drawLine(0, unitHeight, w, unitHeight, mLinePaint);
    }

    public void drawShip(Canvas canvas, Bitmap bitmap, Rect src, Rect dst, int w, int unitHeight) {
        canvas.save();
        canvas.translate((w - dst.width()) / 2, unitHeight - dst.height());
        canvas.drawBitmap(bitmap, src, dst, null);
        canvas.restore();
    }

    public float getLetterWidth() {
        return mLetterWidth;
    }

    private int getTextColor(int numOfShips) {
        if (numOfShips == 0) {
            return mZeroTextColor;
        }

        return mTextColor;
    }

}
