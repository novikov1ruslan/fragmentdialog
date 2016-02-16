package com.ivygames.morskoiboi.ui.view;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.utils.UiUtils;

public class BaseBoardRenderer {
    private Paint debug_paint = new Paint();
    private final Paint mLinePaint;

    private final Paint mHitOuterPaint;
    private final Paint mHitBgPaint;
    private final Paint mHitInnerPaint;
    private final Paint mMissOuterPaint;

    private final Paint mMissBgPaint;
    private final Paint mMissInnerPaint;
    private final Paint mConflictCellPaint;

    public BaseBoardRenderer(Resources res) {
        mLinePaint = UiUtils.newStrokePaint(res, R.color.line);

        mHitOuterPaint = UiUtils.newStrokePaint(res, R.color.hit);
        mHitOuterPaint.setAntiAlias(true);
        mHitInnerPaint = UiUtils.newFillPaint(res, R.color.hit);
        mHitInnerPaint.setAntiAlias(true);
        mHitBgPaint = UiUtils.newFillPaint(res, R.color.hit_background);

        mMissOuterPaint = UiUtils.newStrokePaint(res, R.color.miss);
        mMissOuterPaint.setAntiAlias(true);
        mMissOuterPaint.setAlpha(63);
        mMissInnerPaint = UiUtils.newFillPaint(res, R.color.miss);
        mMissInnerPaint.setAntiAlias(true);
        mMissInnerPaint.setAlpha(80);
        mMissBgPaint = UiUtils.newFillPaint(res, R.color.miss_background);
        mMissBgPaint.setAlpha(80);

        mConflictCellPaint = UiUtils.newFillPaint(res, R.color.conflict_cell);
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

    public void drawShip(Canvas canvas, Rect ship, int left, int top, Paint paint) {
        ship.left += left;
        ship.top += top;
        ship.right += left;
        ship.bottom += top;
        canvas.drawRect(ship, paint);
    }

    public void drawShip(Canvas canvas, Rect ship, Paint paint) {
        drawShip(canvas, ship, 0, 0, paint);
    }

    public void drawHitMark(Canvas canvas, Mark mark) {
        drawMark(canvas, mark, false);
    }

    public void drawMissMark(Canvas canvas, Mark mark) {
        drawMark(canvas, mark, true);
    }

    private void drawMark(Canvas canvas, Mark mark, boolean isMiss) {
        canvas.drawCircle(mark.centerX, mark.centerY, mark.outerRadius, isMiss ? mMissBgPaint : mHitBgPaint);
        canvas.drawCircle(mark.centerX, mark.centerY, mark.outerRadius, isMiss ? mMissOuterPaint : mHitOuterPaint);
        canvas.drawCircle(mark.centerX, mark.centerY, mark.innerRadius, isMiss ? mMissInnerPaint : mHitInnerPaint);
    }

    public void renderConflictingCell(Canvas canvas, RectF invalidRect) {
        canvas.drawRect(invalidRect, mConflictCellPaint);
    }
}
