package com.ivygames.morskoiboi.screen.gameplay;

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
import com.ivygames.morskoiboi.screen.view.Aiming;
import com.ivygames.morskoiboi.screen.view.BaseBoardView;
import com.ivygames.morskoiboi.screen.view.TouchState;
import com.ivygames.morskoiboi.utils.UiUtils;

public class EnemyBoardView extends BaseBoardView {

    private boolean mLocked = true;

    private final Paint mAimingLockedPaint;
    private final TouchState mTouchState = new TouchState();
    private PokeResult mLastShotResult;

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
    protected EnemyBoardRenderer getRenderer() {
        if (mRenderer == null) {
            mRenderer = new EnemyBoardRenderer(getPresenter(), getResources());
        }
        return (EnemyBoardRenderer) mRenderer;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getRenderer().init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getRenderer().release();
    }

    public void setShotListener(ShotListener shotListener) {
        getPresenter().setShotListener(shotListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        getRenderer().drawNautical(canvas);
        super.onDraw(canvas);

        if (getPresenter().hasAim()) {
            getRenderer().drawAim(canvas, getPresenter().getAimRectDst());
        }

        if (getPresenter().startedDragging()) {
            int i = getPresenter().getTouchedI();
            int j = getPresenter().getTouchedJ();

            if (mBoard.containsCell(i, j)) {
                Aiming aiming = getPresenter().getAiming(i, j, 1, 1);
                mRenderer.render(canvas, aiming, getAimingPaint(mBoard.getCell(i, j)));
            }
        }

        if (getRenderer().isAnimationRunning()) {
            postInvalidateDelayed(getRenderer().animateExplosions(canvas, mLastShotResult.aim));
        }

        getRenderer().render(canvas, mTouchState.getTouchX(), mTouchState.getTouchY());
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        mTouchState.setEvent(event);
        getPresenter().onTouch(mTouchState);
        invalidate();

        return true;
    }

    private Paint getAimingPaint(Cell cell) {
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
        mLastShotResult = result;
        getRenderer().startAnimation(result);
    }
}
