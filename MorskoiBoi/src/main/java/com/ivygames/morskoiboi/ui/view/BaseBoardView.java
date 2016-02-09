package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.utils.UiUtils;

import java.util.Collection;

abstract class BaseBoardView extends View {

    private final Paint mTurnBorderPaint;
    private final Paint mBorderPaint;

    protected final Paint mShipPaint;
    protected final Paint mAimingPaint;
    private final DisplayMetrics mDisplayMetrics;

    private final Paint mLinePaint;

    private final Paint mHitOuterPaint;
    private final Paint mHitBgPaint;
    private final Paint mHitInnerPaint;
    private final Paint mMissOuterPaint;

    private final Paint mMissBgPaint;
    private final Paint mMissInnerPaint;

    protected BasePresenter mPresenter;
    protected BaseBoardRenderer mRenderer;

    protected Board mBoard;

    public BaseBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        Resources res = getResources();
        mShipPaint = UiUtils.newStrokePaint(res, R.color.ship_border, R.dimen.ship_border);
        mTurnBorderPaint = UiUtils.newStrokePaint(res, R.color.turn_highliter, R.dimen.turn_border);
        mLinePaint = UiUtils.newStrokePaint(res, R.color.line);
        mAimingPaint = UiUtils.newFillPaint(res, R.color.aim);

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

        mBorderPaint = UiUtils.newStrokePaint(res, R.color.line, R.dimen.board_border);

        mPresenter = getPresenter();
        mRenderer = getRenderer();

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplayMetrics = getDisplayMetrics(windowManager);
    }

    protected abstract BaseBoardRenderer getRenderer();

    protected abstract BasePresenter getPresenter();

    public final void setBoard(Board board) {
        mBoard = board;
        invalidate();
    }

    protected int calcSmallestWidth(int w, int h) {
        int paddedWidth = w - getPaddingLeft() - getPaddingRight();
        int paddedHeight = h - getPaddingTop() - getPaddingBottom();

        return paddedWidth < paddedHeight ? paddedWidth : paddedHeight;
    }

    private void drawMark(Canvas canvas, boolean isMiss, int x, int y) {
        Mark mark = mPresenter.getMark(x, y);
        canvas.drawCircle(mark.centerX, mark.centerY, mark.outerRadius, isMiss ? mMissBgPaint : mHitBgPaint);
        canvas.drawCircle(mark.centerX, mark.centerY, mark.outerRadius, isMiss ? mMissOuterPaint : mHitOuterPaint);
        canvas.drawCircle(mark.centerX, mark.centerY, mark.innerRadius, isMiss ? mMissInnerPaint : mHitInnerPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int boardWidth = mBoard.getHorizontalDim();

        Paint borderPaint = mPresenter.isTurn() ? mTurnBorderPaint : mBorderPaint;
        mRenderer.renderBoard(canvas, mPresenter.getBoard(), borderPaint);

        // draw board
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < mBoard.getVerticalDim(); j++) {
                Cell cell = mBoard.getCell(i, j);
                if (cell.isHit()) {
                    drawMark(canvas, false, i, j);
                } else if (cell.isMiss()) {
                    drawMark(canvas, true, i, j);
                }
            }
        }

        drawShips(canvas);
    }

//    private void drawBoard(Canvas canvas, int boardWidth) {
//        // draw vertical lines
//        for (int i = 0; i < boardWidth + 1; i++) {
//            canvas.drawLines(mPresenter.getVertical(i), mLinePaint);
//        }
//
//        // draw horizontal lines
//        for (int i = 0; i < boardWidth + 1; i++) {
//            canvas.drawLines(mPresenter.getHorizontal(i), mLinePaint);
//        }
//
//        // draw border
//        if (mPresenter.isTurn()) {
//            canvas.drawRect(mPresenter.getTurnRect(), mTurnBorderPaint);
//        } else {
//            canvas.drawRect(mPresenter.getTurnRect(), mBorderPaint);
//        }
//    }

    private void drawShips(Canvas canvas) {
        Collection<Ship> ships = mBoard.getShips();
        for (Ship ship : ships) {
            int left = mPresenter.getLeft(ship.getX());
            int top = mPresenter.getTop(ship.getY());
            UiUtils.drawShip(canvas, ship, left, top, mPresenter.getCellSize(), ship.isDead() ? mShipPaint : mShipPaint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        mPresenter.measure(w, h, 0, 0, calcSmallestWidth(w, h));
    }

    private DisplayMetrics getDisplayMetrics(WindowManager wm) {
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = mDisplayMetrics.widthPixels;
        int desiredHeight = mDisplayMetrics.heightPixels;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        if (width > height) {
            width = height;
        } else {
            height = width;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    public final void hideTurnBorder() {
        mPresenter.hideTurn();
        invalidate();
    }

    public final void showTurnBorder() {
        mPresenter.showTurn();
        invalidate();
    }

    @Override
    public String toString() {
        return mPresenter.toString();
    }
}
