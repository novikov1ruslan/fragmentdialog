package com.ivygames.morskoiboi.ui.view;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.ivygames.morskoiboi.GameConstants;

public class BaseBoardRenderer {
    Paint mDebugPain = new Paint();

    public void render(Canvas canvas, Aiming aiming, Paint paint) {
        canvas.drawRect(aiming.horizontal, paint);
        canvas.drawRect(aiming.vertical, paint);
    }

    public void render(Canvas canvas, TouchState mTouchState) {
        if (GameConstants.IS_TEST_MODE) {
            canvas.drawCircle(mTouchState.getTouchX(), mTouchState.getTouchY(), 5, mDebugPain);
        }
    }
}
