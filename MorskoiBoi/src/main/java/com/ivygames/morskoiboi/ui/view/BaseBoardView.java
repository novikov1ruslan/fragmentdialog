package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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
    private final DisplayMetrics mDisplayMetrics;

    private final Paint mLinePaint;

    private final Paint mHitOuterPaint;
    private final Paint mHitBgPaint;
    private final Paint mHitInnerPaint;
    private final Paint mMissOuterPaint;

    private final Paint mMissBgPaint;
    private final Paint mMissInnerPaint;

    public static class Presenter {

        private boolean mShowTurn;
        private float mTurnBorderSize;
        private Board mBoard;
        private Rect mTurnRect = new Rect(0, 0, 0, 0);
        protected int mCellSize;
        protected int mHalfCellSize;
        protected Rect mBoardRect = new Rect(0, 0, 0, 0);
        private int mMarkRadius;
        private float[] line = new float[4];
        private int left;
        private final Rect rect = new Rect();
        private final RectF rectF = new RectF();

        /**
         * Frame Rect is larger by border
         */
        private final void calcFrameRect() {
            mTurnRect.left = (int) (mBoardRect.left - mTurnBorderSize / 2);
            mTurnRect.right = (int) (mBoardRect.right + mTurnBorderSize / 2);
            mTurnRect.top = (int) (mBoardRect.top - mTurnBorderSize / 2);
            mTurnRect.bottom = (int) (mBoardRect.bottom + mTurnBorderSize / 2);
        }

        public final void calculateBoardRect(int w, int h, int horOffset, int verOffset) {
            int paddedWidth = w;
            int paddedHeight = h;

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

        public void setBoard(Board board) {
            this.mBoard = board;
        }

        public int getBoardWidth() {
            return mBoard.getHorizontalDim();
        }

        public int getBoardHeight() {
            return mBoard.getVerticalDim();
        }

        @Override
        public String toString() {
            return mBoard.toString();
        }

        public Cell getCell(int x, int y) {
            return mBoard.getCell(x, y);
        }

        public boolean containsCell(int x, int y) {
            return mBoard.containsCell(x, y);
        }

        public Collection<Ship> getShips() {
            return mBoard.getShips();
        }

        public void rotateShipAt(int x, int y) {
            mBoard.rotateShipAt(x, y);
        }

        public Board getBoard() {
            return mBoard;
        }

        public Rect getTurnRect() {
            return mTurnRect;
        }

        public float[] getVertical(int i) {
            float startX = mBoardRect.left + i * mCellSize;
            float startY = mBoardRect.top;
            float stopY = mBoardRect.bottom;

            line[0] = startX;
            line[1] = startY;
            line[2] = startX;
            line[3] = stopY;

            return line;
        }

        public float[] getHorizontal(int i) {
            float startX = mBoardRect.left;
            float startY = mBoardRect.top + i * mCellSize;
            float stopX = mBoardRect.right;

            line[0] = startX;
            line[1] = startY;
            line[2] = stopX;
            line[3] = startY;

            return line;
        }

        public int getLeft(int i) {
            return i * mCellSize + mBoardRect.left;
        }

        public int getTop(int j) {
            return j * mCellSize + mBoardRect.top;
        }

        public Rect getBoardRect() {
            return mBoardRect;
        }

        public int getCellSize() {
            return mCellSize;
        }

        public Rect getVerticalRect(int i, int width) {
            int leftVer = mBoardRect.left + i * mCellSize;
            int rightVer = leftVer + width * mCellSize;
            if (rightVer > mBoardRect.right) {
                return null;
            }
            int topVer = mBoardRect.top;
            int bottomVer = mBoardRect.bottom;

            rect.left = leftVer;
            rect.right = rightVer;
            rect.top = topVer;
            rect.bottom = bottomVer;

            return rect;
        }

        public Rect getHorizontalRect(int j, int height) {
            int leftHor = mBoardRect.left;
            int rightHor = mBoardRect.right;
            int topHor = mBoardRect.top + j * mCellSize;
            int bottomHor = topHor + height * mCellSize;

            rect.left = leftHor;
            rect.right = rightHor;
            rect.top = topHor;
            rect.bottom = bottomHor;

            return rect;
        }

        // TODO: used in SetupBoardView
        public RectF getInvalidRect(int i, int j) {
            float left = mBoardRect.left + i * mCellSize + 1;
            float top = mBoardRect.top + j * mCellSize + 1;
            float right = left + mCellSize;
            float bottom = top + mCellSize;

            rectF.left = left + 1;
            rectF.top = top + 1;
            rectF.right = right;
            rectF.bottom = bottom;

            return rectF;
        }

        public int getCellY(int mTouchY) {
            return (mTouchY - mBoardRect.top) / mCellSize;
        }

        public int getCellX(int mTouchX) {
            return (mTouchX - mBoardRect.left) / mCellSize;
        }
    }

    protected Presenter mPresenter = new Presenter();

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

        mPresenter.mTurnBorderSize = res.getDimension(R.dimen.ship_border);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplayMetrics = getDisplayMetrics(windowManager);
    }

    public final void setBoard(Board board) {
        mPresenter.setBoard(Validate.notNull(board));
        invalidate();
    }

    private void drawMark(Canvas canvas, boolean isMiss, int left, int top) {
        float cx = left + mPresenter.mHalfCellSize;
        float cy = top + mPresenter.mHalfCellSize;
        canvas.drawCircle(cx, cy, mPresenter.mMarkRadius, isMiss ? mMissBgPaint : mHitBgPaint);
        canvas.drawCircle(cx, cy, mPresenter.mMarkRadius, isMiss ? mMissOuterPaint : mHitOuterPaint);
        canvas.drawCircle(cx, cy, mPresenter.mMarkRadius - mPresenter.mCellSize / 6, isMiss ? mMissInnerPaint : mHitInnerPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int boardWidth = mPresenter.getBoardWidth();

        // draw vertical lines
        for (int i = 0; i < boardWidth + 1; i++) {
            canvas.drawLines(mPresenter.getVertical(i), mLinePaint);
        }

        // draw horizontal lines
        for (int i = 0; i < boardWidth + 1; i++) {
            canvas.drawLines(mPresenter.getHorizontal(i), mLinePaint);
        }

        // draw border
        if (mPresenter.mShowTurn) {
            canvas.drawRect(mPresenter.getTurnRect(), mTurnBorderPaint);
        } else {
            canvas.drawRect(mPresenter.getTurnRect(), mBorderPaint);
        }

        // draw board
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < mPresenter.getBoardHeight(); j++) {
                int left = mPresenter.getLeft(i);
                int top = mPresenter.getTop(j);

                Cell cell = mPresenter.getCell(i, j);
                if (cell.isHit()) {
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

        int w = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int h = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        mPresenter.calculateBoardRect(w, h, 0, 0);
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

    private void drawShips(Canvas canvas) {
        Collection<Ship> ships = mPresenter.getShips();
        for (Ship ship : ships) {
            UiUtils.drawShip(canvas, ship, mPresenter.getBoardRect(), mPresenter.getCellSize(), ship.isDead() ? mShipPaint : mShipPaint);
        }
    }

    protected final void drawAiming(Canvas canvas, int i, int j, int width, int height) {
        // aiming
        if (!mPresenter.containsCell(i, j)) {
            return;
        }


        Rect verticalRect = mPresenter.getVerticalRect(i, width);
        if (verticalRect == null) {
            return;
        }

        Rect horizontalRect = mPresenter.getHorizontalRect(j, width);

        Paint paint = getAimingPaint(mPresenter.getCell(i, j));
        canvas.drawRect(horizontalRect, paint);
        canvas.drawRect(verticalRect, paint);
    }

    protected Paint getAimingPaint(Cell cell) {
        return mAimingPaint;
    }

    public final void hideTurnBorder() {
        mPresenter.mShowTurn = false;
        invalidate();
    }

    public final void showTurnBorder() {
        mPresenter.mShowTurn = true;
        invalidate();
    }

    @Override
    public String toString() {
        return mPresenter.toString();
    }
}
