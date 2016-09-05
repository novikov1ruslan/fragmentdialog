package com.ivygames.morskoiboi.screen.view;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.GraphicsUtils;
import com.ivygames.morskoiboi.model.Ship;

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
    private final Paint mTurnBorderPaint;
    @NonNull
    private final Paint mBorderPaint;
    @NonNull
    private final Paint mShipPaint;
    @NonNull
    private final Paint mAimingPaint;
    @NonNull
    private final Paint mAimingLockedPaint;
    @NonNull
    private final BasePresenter mPresenter;

    public BaseBoardRenderer(@NonNull Resources res, @NonNull BasePresenter presenter) {
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

        mShipPaint = GraphicsUtils.newStrokePaint(res, R.color.ship_border, R.dimen.ship_border);
        mTurnBorderPaint = GraphicsUtils.newStrokePaint(res, R.color.turn_highliter, R.dimen.turn_border);
        mAimingPaint = GraphicsUtils.newFillPaint(res, R.color.aim);

        mBorderPaint = GraphicsUtils.newStrokePaint(res, R.color.line, R.dimen.board_border);

        mAimingLockedPaint = GraphicsUtils.newFillPaint(res, R.color.aim_locked);

        mPresenter = presenter;
    }

    public void drawAiming(Canvas canvas, @NonNull Aiming aiming, boolean locked) {
        drawAiming(canvas, mPresenter.getAimingG(aiming), locked);
    }

    protected void drawAiming(@NonNull Canvas canvas, @NonNull AimingG aiming) {
        drawAiming(canvas, aiming, false);
    }

    private void drawAiming(@NonNull Canvas canvas, @NonNull AimingG aiming, boolean locked) {
        Paint paint = locked ? mAimingLockedPaint : mAimingPaint;
        drawAiming(canvas, aiming, paint);
    }

    private void drawAiming(@NonNull Canvas canvas, @NonNull AimingG aiming, @NonNull Paint paint) {
        canvas.drawRect(aiming.horizontal, paint);
        canvas.drawRect(aiming.vertical, paint);
    }

    public void drawDebug(Canvas canvas, int x, int y) {
        canvas.drawCircle(x, y, 5, debug_paint);
    }

    public void drawBoard(@NonNull Canvas canvas, boolean myTurn) {
        drawBoard(canvas,  mPresenter.getBoard(), myTurn);
    }

    public void drawBoard(@NonNull Canvas canvas, @NonNull BoardG board, boolean myTurn) {
        for (float[] line: board.lines) {
            canvas.drawLines(line, mLinePaint);
        }

        Paint borderPaint = myTurn ? mTurnBorderPaint : mBorderPaint;
        canvas.drawRect(board.frame, borderPaint);
    }

    public void drawRect(@NonNull Canvas canvas, @NonNull Rect rect, int left, int top) {
        rect.left += left;
        rect.top += top;
        rect.right += left;
        rect.bottom += top;
        canvas.drawRect(rect, mShipPaint);
    }

    public void drawShip(@NonNull Canvas canvas, @NonNull Ship ship) {
        drawRect(canvas, mPresenter.getRectForShip(ship));
    }

    public void drawRect(@NonNull Canvas canvas, @NonNull Rect rect) {
        drawRect(canvas, rect, 0, 0);
    }

    public void drawHitMark(@NonNull Canvas canvas, int i, int j) {
        drawHitMark(canvas, mPresenter.getMark(i, j));
    }

    public void drawMissMark(@NonNull Canvas canvas, int i, int j) {
        drawMissMark(canvas, mPresenter.getMark(i, j));
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

    public void measure(int w, int h, int horizontalPadding, int verticalPadding) {
        mPresenter.measure(w, h, horizontalPadding, verticalPadding);
    }

    @Override
    public String toString() {
        return mPresenter.toString();
    }
}
