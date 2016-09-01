package com.ivygames.morskoiboi.screen.view;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.GraphicsUtils;

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

    private final Paint mTurnBorderPaint;
    private final Paint mBorderPaint;

    protected final Paint mShipPaint;
    protected final Paint mAimingPaint;

    @NonNull
    private final Paint mAimingLockedPaint;

    public BaseBoardRenderer(Resources res) {
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

    public void render(@NonNull Canvas canvas, @NonNull Aiming aiming) {
        render(canvas, aiming, false);
    }

    public void render(@NonNull Canvas canvas, @NonNull Aiming aiming, boolean locked) {
        Paint paint = locked ? mAimingLockedPaint : mAimingPaint;
        render(canvas, aiming, paint);
    }

    private void render(@NonNull Canvas canvas, @NonNull Aiming aiming, @NonNull Paint paint) {
        canvas.drawRect(aiming.horizontal, paint);
        canvas.drawRect(aiming.vertical, paint);
    }

    public void render(Canvas canvas, int x, int y) {
        canvas.drawCircle(x, y, 5, debug_paint);
    }

    public void renderBoard(@NonNull Canvas canvas, @NonNull BoardG board, boolean myTurn) {
        for (float[] line: board.lines) {
            canvas.drawLines(line, mLinePaint);
        }

        Paint borderPaint = myTurn ? mTurnBorderPaint : mBorderPaint;
        canvas.drawRect(board.frame, borderPaint);
    }

    public void drawShip(Canvas canvas, Rect ship, int left, int top) {
        ship.left += left;
        ship.top += top;
        ship.right += left;
        ship.bottom += top;
        canvas.drawRect(ship, mShipPaint);
    }

    public void drawShip(Canvas canvas, Rect rect) {
        drawShip(canvas, rect, 0, 0);
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

    public void renderConflictingCell(Canvas canvas, Rect invalidRect) {
        canvas.drawRect(invalidRect, mConflictCellPaint);
    }
}
