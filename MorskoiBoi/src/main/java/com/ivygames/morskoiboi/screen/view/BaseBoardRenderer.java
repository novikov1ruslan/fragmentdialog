package com.ivygames.morskoiboi.screen.view;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.GraphicsUtils;

public class BaseBoardRenderer {
    @NonNull
    private Paint debug_paint = new Paint();
    @NonNull
    private final Paint mLinePaint;
    @NonNull
    private final Paint mHitOuterPaint;
    @NonNull
    private final Paint mHitBgPaint;
    @NonNull
    private final Paint mHitInnerPaint;
    @NonNull
    private final Paint mMissOuterPaint;
    @NonNull
    private final Paint mMissBgPaint;
    @NonNull
    private final Paint mMissInnerPaint;
    @NonNull
    private final Paint mConflictCellPaint;
    @NonNull
    private final Paint mTurnBorderPaint;
    @NonNull
    private final Paint mBorderPaint;
    @NonNull
    private final Paint mShipPaint;
    @NonNull
    private final Paint mAimingPaint;
    @NonNull
    private final Paint mAimingLockedPaint;

    public BaseBoardRenderer(@NonNull Resources res) {
        mLinePaint = GraphicsUtils.newStrokePaint(res, R.color.line);

        mHitOuterPaint = GraphicsUtils.newStrokePaint(res, R.color.hit);
        mHitOuterPaint.setAntiAlias(true);
        mHitInnerPaint = GraphicsUtils.newFillPaint(res, R.color.hit);
        mHitInnerPaint.setAntiAlias(true);
        mHitBgPaint = GraphicsUtils.newFillPaint(res, R.color.hit_background);

        mMissOuterPaint = GraphicsUtils.newStrokePaint(res, R.color.miss);
        mMissOuterPaint.setAntiAlias(true);
        mMissOuterPaint.setAlpha(63);
        mMissInnerPaint = GraphicsUtils.newFillPaint(res, R.color.miss);
        mMissInnerPaint.setAntiAlias(true);
        mMissInnerPaint.setAlpha(80);
        mMissBgPaint = GraphicsUtils.newFillPaint(res, R.color.miss_background);
        mMissBgPaint.setAlpha(80);

        mConflictCellPaint = GraphicsUtils.newFillPaint(res, R.color.conflict_cell);

        mShipPaint = GraphicsUtils.newStrokePaint(res, R.color.ship_border, R.dimen.ship_border);
        mTurnBorderPaint = GraphicsUtils.newStrokePaint(res, R.color.turn_highliter, R.dimen.turn_border);
        mAimingPaint = GraphicsUtils.newFillPaint(res, R.color.aim);

        mBorderPaint = GraphicsUtils.newStrokePaint(res, R.color.line, R.dimen.board_border);

        mAimingLockedPaint = GraphicsUtils.newFillPaint(res, R.color.aim_locked);
    }

    public void drawAiming(@NonNull Canvas canvas, @NonNull Aiming aiming) {
        drawAiming(canvas, aiming, false);
    }

    public void drawAiming(@NonNull Canvas canvas, @NonNull Aiming aiming, boolean locked) {
        Paint paint = locked ? mAimingLockedPaint : mAimingPaint;
        drawAiming(canvas, aiming, paint);
    }

    private void drawAiming(@NonNull Canvas canvas, @NonNull Aiming aiming, @NonNull Paint paint) {
        canvas.drawRect(aiming.horizontal, paint);
        canvas.drawRect(aiming.vertical, paint);
    }

    public void drawDebug(Canvas canvas, int x, int y) {
        canvas.drawCircle(x, y, 5, debug_paint);
    }

    public void drawBoard(@NonNull Canvas canvas, @NonNull BoardG board, boolean myTurn) {
        for (float[] line: board.lines) {
            canvas.drawLines(line, mLinePaint);
        }

        Paint borderPaint = myTurn ? mTurnBorderPaint : mBorderPaint;
        canvas.drawRect(board.frame, borderPaint);
    }

    public void drawShip(@NonNull Canvas canvas, @NonNull Rect rect, int left, int top) {
        rect.left += left;
        rect.top += top;
        rect.right += left;
        rect.bottom += top;
        canvas.drawRect(rect, mShipPaint);
    }

    public void drawShip(@NonNull Canvas canvas, @NonNull Rect rect) {
        drawShip(canvas, rect, 0, 0);
    }

    public void drawHitMark(@NonNull Canvas canvas, @NonNull Mark mark) {
        drawMark(canvas, mark, false);
    }

    public void drawMissMark(@NonNull Canvas canvas, @NonNull Mark mark) {
        drawMark(canvas, mark, true);
    }

    private void drawMark(@NonNull Canvas canvas, @NonNull Mark mark, boolean isMiss) {
        canvas.drawCircle(mark.centerX, mark.centerY, mark.outerRadius, isMiss ? mMissBgPaint : mHitBgPaint);
        canvas.drawCircle(mark.centerX, mark.centerY, mark.outerRadius, isMiss ? mMissOuterPaint : mHitOuterPaint);
        canvas.drawCircle(mark.centerX, mark.centerY, mark.innerRadius, isMiss ? mMissInnerPaint : mHitInnerPaint);
    }

    public void renderConflictingCell(@NonNull Canvas canvas, @NonNull Rect invalidRect) {
        canvas.drawRect(invalidRect, mConflictCellPaint);
    }
}
