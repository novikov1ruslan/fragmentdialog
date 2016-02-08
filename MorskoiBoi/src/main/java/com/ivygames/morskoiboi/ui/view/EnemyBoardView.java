package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    private ShotListener mShotListener;

    private final TouchState mTouchState = new TouchState();
    private int mTouchAction = mTouchState.getTouchAction();
    private EnemyBoardRenderer mRenderer;

    private final Paint mAimingLockedPaint;

    public interface ShotListener {
        void onShot(int i, int j);

        void onAimingStarted();

        void onAimingFinished();
    }

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
        mShotListener = shotListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRenderer.drawNautical(canvas);
        super.onDraw(canvas);

        if (getPresenter().hasAim()) {
            mRenderer.drawAim(canvas, getPresenter().getAimRectDst());
        }

        drawAiming(canvas);

        if (mRenderer.isAnimationRunning()) {
            postInvalidateDelayed(mRenderer.animateExplosions(canvas));
        }
    }

    private void drawAiming(Canvas canvas) {
        if (mTouchState.getDragStatus() == TouchState.START_DRAGGING) {
            drawAiming(canvas, getPresenter().getTouchedCellX(), getPresenter().getTouchedCellY(), 1, 1);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        mTouchState.setEvent(event);
        getPresenter().setTouch(mTouchState.getTouchX(), mTouchState.getTouchY());
        mTouchAction = mTouchState.getTouchAction();
        // TODO: create universal procedure to map x,y to cell
        if (mTouchAction == MotionEvent.ACTION_DOWN && !mLocked) {
            mShotListener.onAimingStarted();
        }

        if (mTouchAction == MotionEvent.ACTION_UP && !mLocked) {
            // TODO: unify these 2 callbacks
            mShotListener.onAimingFinished();
            mShotListener.onShot(getPresenter().getTouchedCellX(), getPresenter().getTouchedCellY());
        }
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
        if (mTouchAction == MotionEvent.ACTION_DOWN || mTouchAction == MotionEvent.ACTION_MOVE) {
            mShotListener.onAimingStarted();
        }
    }

    public void setShotResult(PokeResult result) {
        mRenderer.startAnimation(result);
    }

}
