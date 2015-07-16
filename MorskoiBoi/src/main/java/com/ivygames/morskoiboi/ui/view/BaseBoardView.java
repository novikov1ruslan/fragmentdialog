package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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

import org.apache.commons.lang3.Validate;

import java.util.Collection;

abstract class BaseBoardView extends View {

    private final Paint mTurnBorderPaint;
    private final Paint mBorderPaint;

    protected final Paint mShipPaint;
    protected final Paint mAimingPaint;
    private final float mTurnBorderSize;
    private final DisplayMetrics mDisplayMetrics;
    protected int mCellSize;
    protected int mHalfCellSize;
    protected Board mBoard;
    protected final Rect mBoardRect;
    private final Rect mTurnRect;

    private final Paint mLinePaint;
    private final Paint mHitOuterPaint;
    private final Paint mHitBgPaint;
    private final Paint mHitInnerPaint;

    private final Paint mMissOuterPaint;
    private final Paint mMissBgPaint;
    private final Paint mMissInnerPaint;

    private boolean mShowTurn;
    private int mBoardHeight;
    private int mMarkRadius;

    public BaseBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        Resources res = context.getResources();
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

        mBoardRect = new Rect(0, 0, 0, 0);
        mTurnRect = new Rect(0, 0, 0, 0);

        mTurnBorderSize = getResources().getDimension(R.dimen.ship_border);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplayMetrics = getDisplayMetrics(windowManager);
    }

    public final void setBoard(Board board) {
        mBoard = Validate.notNull(board);
        mBoardHeight = mBoard.getVerticalDim();
        invalidate();
    }

    private void drawMark(Canvas canvas, boolean isMiss, int left, int top) {
        float cx = left + mHalfCellSize;
        float cy = top + mHalfCellSize;
        canvas.drawCircle(cx, cy, mMarkRadius, isMiss ? mMissBgPaint : mHitBgPaint);
        canvas.drawCircle(cx, cy, mMarkRadius, isMiss ? mMissOuterPaint : mHitOuterPaint);
        canvas.drawCircle(cx, cy, mMarkRadius - mCellSize / 6, isMiss ? mMissInnerPaint : mHitInnerPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int boardWidth = mBoard.getHorizontalDim();

        // draw vertical lines
        for (int i = 0; i < boardWidth + 1; i++) {
            float startX = mBoardRect.left + i * mCellSize;
            float startY = mBoardRect.top;
            float stopY = mBoardRect.bottom;
            canvas.drawLine(startX, startY, startX, stopY, mLinePaint);
        }

        // draw horizontal lines
        for (int i = 0; i < boardWidth + 1; i++) {
            float startX = mBoardRect.left;
            float startY = mBoardRect.top + i * mCellSize;
            float stopX = mBoardRect.right;
            canvas.drawLine(startX, startY, stopX, startY, mLinePaint);
        }

        // draw border
        if (mShowTurn) {
            canvas.drawRect(mTurnRect, mTurnBorderPaint);
        } else {
            canvas.drawRect(mTurnRect, mBorderPaint);
        }

        // draw board
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < mBoardHeight; j++) {
                int left = i * mCellSize + mBoardRect.left;
                int top = j * mCellSize + mBoardRect.top;

                Cell cell = mBoard.getCell(i, j);
                if (cell.isHit()/* || cell.isSunk() */) {
                    drawMark(canvas, false, left, top);
                } else if (cell.isMiss()) {
                    drawMark(canvas, true, left, top);
                }
            }
        }

        drawShips(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        calculateBoardRect(w, h, 0, 0);
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
        }
        else {
            height = width;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    protected final void calculateBoardRect(int w, int h, int horOffset, int verOffset) {
        int paddedWidth = w - getPaddingLeft() - getPaddingRight();
        int paddedHeight = h - getPaddingTop() - getPaddingBottom();

        int smallestWidth = paddedWidth < paddedHeight ? paddedWidth : paddedHeight;

        mCellSize = smallestWidth / mBoard.getHorizontalDim();
        int boardSize = mCellSize * mBoard.getHorizontalDim();

        mBoardRect.left = (w - boardSize) / 2 + horOffset;
        mBoardRect.top = (h - boardSize) / 2 + verOffset;
        mBoardRect.right = mBoardRect.left + boardSize;
        mBoardRect.bottom = mBoardRect.top + boardSize;

        mHalfCellSize = mCellSize / 2;
        mMarkRadius = mHalfCellSize - mCellSize / 5;

        calcFrameRect();
    }

    /**
     * Frame Rect is larger by border
     */
    protected final void calcFrameRect() {
        mTurnRect.left = (int) (mBoardRect.left - mTurnBorderSize / 2);
        mTurnRect.right = (int) (mBoardRect.right + mTurnBorderSize / 2);
        mTurnRect.top = (int) (mBoardRect.top - mTurnBorderSize / 2);
        mTurnRect.bottom = (int) (mBoardRect.bottom + mTurnBorderSize / 2);
    }

    private void drawShips(Canvas canvas) {
        Collection<Ship> ships = mBoard.getShips();
        for (Ship ship : ships) {
            UiUtils.drawShip(canvas, ship, mBoardRect, mCellSize, ship.isDead() ? mShipPaint : mShipPaint);
        }
    }

    protected final void drawAiming(Canvas canvas, int i, int j, int width, int height) {
        // aiming
        if (!mBoard.containsCell(i, j)) {
            return;
        }

        // vertical
        int leftVer = mBoardRect.left + i * mCellSize;
        int rightVer = leftVer + width * mCellSize;
        if (rightVer > mBoardRect.right) {
            return;
        }
        int topVer = mBoardRect.top;
        int bottomVer = mBoardRect.bottom;

        // horizontal
        int leftHor = mBoardRect.left;
        int rightHor = mBoardRect.right;
        int topHor = mBoardRect.top + j * mCellSize;
        int bottomHor = topHor + height * mCellSize;

        Paint paint = getAimingPaint(mBoard.getCell(i, j));
        canvas.drawRect(leftHor, topHor, rightHor, bottomHor, paint);
        canvas.drawRect(leftVer, topVer, rightVer, bottomVer, paint);
    }

    protected Paint getAimingPaint(Cell cell) {
        return mAimingPaint;
    }

    public final void hideTurnBorder() {
        mShowTurn = false;
        invalidate();
    }

    public final void showTurnBorder() {
        mShowTurn = true;
        invalidate();
    }

    @Override
    public String toString() {
        return mBoard.toString();
    }
}
