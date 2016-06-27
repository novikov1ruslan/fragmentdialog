package com.ivygames.morskoiboi.screen.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
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

public abstract class BaseBoardView extends View {

    private final Paint mTurnBorderPaint;
    private final Paint mBorderPaint;

    protected final Paint mShipPaint;
    protected final Paint mAimingPaint;
    private final DisplayMetrics mDisplayMetrics;

    protected BasePresenter mPresenter;
    protected BaseBoardRenderer mRenderer;

    protected Board mBoard;

    public BaseBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        Resources res = getResources();
        mShipPaint = UiUtils.newStrokePaint(res, R.color.ship_border, R.dimen.ship_border);
        mTurnBorderPaint = UiUtils.newStrokePaint(res, R.color.turn_highliter, R.dimen.turn_border);
        mAimingPaint = UiUtils.newFillPaint(res, R.color.aim);

        mBorderPaint = UiUtils.newStrokePaint(res, R.color.line, R.dimen.board_border);

        mPresenter = presenter();
        mRenderer = renderer();

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplayMetrics = getDisplayMetrics(windowManager);
    }

    @NonNull
    protected abstract BaseBoardRenderer renderer();

    @NonNull
    protected abstract BasePresenter presenter();

    public final void setBoard(Board board) {
        mBoard = board;
        invalidate();
    }

    protected final int getHorizontalPadding() {
        return getPaddingLeft() + getPaddingRight();
    }

    protected final int getVerticalPadding() {
        return getPaddingTop() + getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint borderPaint = mPresenter.isTurn() ? mTurnBorderPaint : mBorderPaint;
        mRenderer.renderBoard(canvas, mPresenter.getBoard(), borderPaint);
        drawCells(canvas);
        drawShips(canvas);
    }

    private void drawCells(Canvas canvas) {
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                Cell cell = mBoard.getCell(i, j);
                if (cell.isHit()) {
                    mRenderer.drawHitMark(canvas, mPresenter.getMark(i, j));
                } else if (cell.isMiss()) {
                    mRenderer.drawMissMark(canvas, mPresenter.getMark(i, j));
                }
            }
        }
    }

    private void drawShips(Canvas canvas) {
        Collection<Ship> ships = mBoard.getShips();
        for (Ship ship : ships) {
            mRenderer.drawShip(canvas, mPresenter.getRectForShip(ship), mShipPaint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        mPresenter.measure(w, h, getHorizontalPadding(), getVerticalPadding());
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

        int boardSide = width > height ? height : width;

        //MUST CALL THIS
        setMeasuredDimension(boardSide, boardSide);
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
