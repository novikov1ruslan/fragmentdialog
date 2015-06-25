package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.utils.UiUtils;

import java.util.Collection;

/*package*/abstract class BaseBoardView extends TouchView {

    private final Paint mTurnBorderPaint;
    private final Paint mBorderPaint;

    protected final Paint mShipPaint;
    protected final Paint mAimingPaint;
    protected int mCellSize;
    protected int mHalfCellSize;
    protected Board mBoard;
    protected Rect mBoardRect;
    private Rect mTurnRect;

    private final Paint mLinePaint;
    private final Paint mHitOuterPaint;
    private final Paint mHitBgPaint;
    private final Paint mHitInnerPaint;

    private final Paint mMissOuterPaint;
    private final Paint mMissBgPaint;
    private final Paint mMissInnerPaint;

    // private final int mBorder;
    private boolean mShowTurn;
    private int mBoardHeight;

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

        if (isInEditMode()) {
            setBoard(new Board());
        }
    }

    public final void setBoard(Board board) {
        mBoard = board;
        mBoardHeight = mBoard.getHeight();
        invalidate();
    }

    private void drawMark(Canvas canvas, boolean isMiss, int left, int top) {
        float cx = left + mHalfCellSize;
        float cy = top + mHalfCellSize;
        // float cellPadding = mCellSize / 5;
        // if (cellPadding < 1) {
        // cellPadding = 1;
        // }
        float radius = mHalfCellSize - mCellSize / 5;
        canvas.drawCircle(cx, cy, radius, isMiss ? mMissBgPaint : mHitBgPaint);
        canvas.drawCircle(cx, cy, radius, isMiss ? mMissOuterPaint : mHitOuterPaint);
        canvas.drawCircle(cx, cy, radius - mCellSize / 6, isMiss ? mMissInnerPaint : mHitInnerPaint);
        // if (!isMiss) {
        // canvas.drawCircle(cx, cy, radius - mCellSize / 6, mHitInnerPaint);
        // }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int boardWidth = mBoard.getWidth();

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        w = w < h ? w : h;
        setMeasuredDimension(w, w);

        int availableSize = w - getPaddingLeft() - getPaddingRight();
        mCellSize = availableSize / mBoard.getWidth();
        int boardSize = mCellSize * mBoard.getWidth();

        mBoardRect.left = (w - boardSize) / 2;
        mBoardRect.top = getPaddingTop();
        mBoardRect.right = mBoardRect.left + boardSize;
        mBoardRect.bottom = mBoardRect.top + boardSize;

        mHalfCellSize = mCellSize / 2;

        calcFrameRect();
    }

    protected final void calcFrameRect() {
        mTurnRect.left = mBoardRect.left - getPaddingLeft() / 2;
        mTurnRect.right = mBoardRect.right + getPaddingRight() / 2;
        mTurnRect.top = mBoardRect.top - getPaddingTop() / 2;
        mTurnRect.bottom = mBoardRect.bottom + getPaddingBottom() / 2;
    }

    private void drawShips(Canvas canvas) {
        Collection<Ship> ships = mBoard.getShips();
        for (Ship ship : ships) {
            UiUtils.drawShip(canvas, ship, mBoardRect, mCellSize, ship.isDead() ? mShipPaint : mShipPaint);
        }
    }

    protected final void drawAiming(Canvas canvas, int i, int j, int width, int height) {
        // aiming
        if (mBoard.containsCell(i, j)) {
            Paint paint = getAimingPaint(mBoard.getCell(i, j));

            // horizontal
            int leftHor = mBoardRect.left;
            int topHor = mBoardRect.top + j * mCellSize;
            int rightHor = mBoardRect.right;
            int bottomHor = topHor + height * mCellSize;

            // vertical
            int leftVer = mBoardRect.left + i * mCellSize;
            int topVer = mBoardRect.top;
            int rightVer = leftVer + width * mCellSize;
            if (rightVer > mBoardRect.right) {
//                rightVer = mBoardRect.right;
                return;
            }
            int bottomVer = mBoardRect.bottom;

            canvas.drawRect(leftHor, topHor, rightHor, bottomHor, paint);
            canvas.drawRect(leftVer, topVer, rightVer, bottomVer, paint);
        }
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
