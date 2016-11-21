package com.ivygames.morskoiboi.screen.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.renderer.BaseBoardRenderer;
import com.ivygames.morskoiboi.screen.boardsetup.BoardUtils;

import java.util.List;

public abstract class BaseBoardView extends View {

    @NonNull
    private final DisplayMetrics mDisplayMetrics;

    protected final BaseBoardRenderer mRenderer;

    protected Board mBoard;

    private boolean mShowTurn;
    private boolean mAllowAdjacentShips;

    public BaseBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        mRenderer = renderer();

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplayMetrics = getDisplayMetrics(windowManager);
    }

    @NonNull
    protected abstract BaseBoardRenderer renderer();

    public final void setBoard(@NonNull Board board) {
        mBoard = board;
        invalidate();
    }

    public void allowAdjacentShips() {
        mAllowAdjacentShips = true;
    }

    private int getHorizontalPadding() {
        return getPaddingLeft() + getPaddingRight();
    }

    private int getVerticalPadding() {
        return getPaddingTop() + getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mRenderer.drawBoard(canvas, mShowTurn);
        drawCells(canvas);
        drawShips(canvas);
    }

    private void drawCells(@NonNull Canvas canvas) {
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                Cell cell = mBoard.getCell(i, j);
                if (cell == Cell.HIT) {
                    mRenderer.drawHitMark(canvas, i, j);
                } else if (cell == Cell.MISS) {
                    mRenderer.drawMissMark(canvas, i, j);
                }
            }
        }
    }

    private void drawShips(@NonNull Canvas canvas) {
        for (Board.LocatedShip locatedShip : mBoard.getLocatedShips()) {
            mRenderer.drawShip(canvas, locatedShip);
            if (locatedShip.ship.isDead() && !mAllowAdjacentShips) {
                drawMissMarks(canvas, BoardUtils.getCells(locatedShip, true));
            }
        }
    }

    private void drawMissMarks(@NonNull Canvas canvas, @NonNull List<Vector2> cells) {
        for (Vector2 cell : cells) {
            mRenderer.drawMissMark(canvas, cell);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        mRenderer.measure(w, h, getHorizontalPadding(), getVerticalPadding());
    }

    @NonNull
    private DisplayMetrics getDisplayMetrics(@NonNull WindowManager wm) {
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
        mShowTurn = false;
        invalidate();
    }

    public final void showTurnBorder() {
        mShowTurn = true;
        invalidate();
    }

    @Override
    public String toString() {
        return mRenderer.toString();
    }
}
