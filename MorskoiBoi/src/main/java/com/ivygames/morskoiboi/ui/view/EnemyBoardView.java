package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.utils.UiUtils;

public class EnemyBoardView extends BaseBoardView {

    private boolean mLocked = true;

    private EnemyBoardRenderer mRenderer;

    private final Paint mAimingLockedPaint;
    private final TouchState mTouchState = new TouchState();

    public EnemyBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mRenderer = new EnemyBoardRenderer(getPresenter(), getResources());
        mAimingLockedPaint = UiUtils.newFillPaint(getResources(), R.color.aim_locked);
    }

    @Override
    protected EnemyBoardPresenter getPresenter() {
        if (mPresenter == null) {
            mPresenter = new EnemyBoardPresenter(10, getResources().getDimension(R.dimen.ship_border));
        }
        return (EnemyBoardPresenter) mPresenter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRenderer.init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRenderer.release();
    }

    public void setShotListener(ShotListener shotListener) {
        getPresenter().setShotListener(shotListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRenderer.drawNautical(canvas);
        super.onDraw(canvas);

        if (getPresenter().hasAim()) {
            mRenderer.drawAim(canvas, getPresenter().getAimRectDst());
        }

        if (getPresenter().startedDragging()) {
            drawAiming(canvas, getPresenter().getTouchedCellX(), getPresenter().getTouchedCellY(), 1, 1);
        }

        if (mRenderer.isAnimationRunning()) {
            postInvalidateDelayed(mRenderer.animateExplosions(canvas));
        }
    }

    protected final void drawAiming(Canvas canvas, int i, int j, int width, int height) {
        if (!mBoard.containsCell(i, j)) {
            return;
        }

//        Aiming aiming = mPresenter.getAiming(i, j, width, height);
//        if (aiming != null)
//            mRenderer.render(canvas, aiming, getAimingPaint(mBoard.getCell(i, j)));
//        }

        Rect verticalRect = mPresenter.getVerticalRect(i, width);
        if (verticalRect == null) {
            return;
        }
        Rect horizontalRect = mPresenter.getHorizontalRect(j, height);
        Paint paint = getAimingPaint(mBoard.getCell(i, j));
        canvas.drawRect(horizontalRect, paint);
        canvas.drawRect(verticalRect, paint);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        mTouchState.setEvent(event);
        getPresenter().onTouch(mTouchState);
        invalidate();

        return true;
    }

    @Override
    protected final Paint getAimingPaint(Cell cell) {
        return cell.beenShot() || mLocked ? mAimingLockedPaint : mAimingPaint;
    }

    public void setAim(Vector2 aim) {
        getPresenter().setAim(aim);
        invalidate();
    }

    public void removeAim() {
        getPresenter().removeAim();
    }

    public boolean isLocked() {
        return mLocked;
    }

    public void lock() {
        mLocked = true;
    }

    public void unLock() {
        mLocked = false;
        getPresenter().unlock();
    }

    public void setShotResult(PokeResult result) {
        mRenderer.startAnimation(result);
    }

}
