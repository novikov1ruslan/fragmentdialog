package com.ivygames.morskoiboi.ui.view;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.utils.UiUtils;

public class BaseBoardRenderer {
    private Paint debug_paint = new Paint();
    private final Paint mLinePaint;

    public BaseBoardRenderer(Resources res) {
        mLinePaint = UiUtils.newStrokePaint(res, R.color.line);
    }

    public void render(Canvas canvas, Aiming aiming, Paint paint) {
        canvas.drawRect(aiming.horizontal, paint);
        canvas.drawRect(aiming.vertical, paint);
    }

    public void render(Canvas canvas, TouchState mTouchState) {
        if (GameConstants.IS_TEST_MODE) {
            canvas.drawCircle(mTouchState.getTouchX(), mTouchState.getTouchY(), 5, debug_paint);
        }
    }

    public void renderBoard(Canvas canvas, BoardG board, Paint turnPaint) {
        for (float[] line: board.lines) {
            canvas.drawLines(line, mLinePaint);
        }

        canvas.drawRect(board.frame, turnPaint);
    }
}
