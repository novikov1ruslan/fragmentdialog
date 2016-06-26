package com.ivygames.morskoiboi.screen.gameplay;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.view.Aiming;
import com.ivygames.morskoiboi.screen.view.BaseBoardView;
import com.ivygames.morskoiboi.utils.UiUtils;

public class EnemyBoardView extends BaseBoardView {

    private final Paint mAimingLockedPaint;
    private PokeResult mLastShotResult;
    private Vector2 mAim;

    public EnemyBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mAimingLockedPaint = UiUtils.newFillPaint(getResources(), R.color.aim_locked);
    }

    @Override
    protected EnemyBoardPresenter presenter() {
        if (mPresenter == null) {
            mPresenter = new EnemyBoardPresenter(10, getResources().getDimension(R.dimen.ship_border));
        }
        return (EnemyBoardPresenter) mPresenter;
    }

    @Override
    protected EnemyBoardRenderer getRenderer() {
        if (mRenderer == null) {
            mRenderer = new EnemyBoardRenderer(presenter(), getResources());
        }
        return (EnemyBoardRenderer) mRenderer;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getRenderer().init(availableMemory());
    }

    private long availableMemory() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getRenderer().release();
    }

    public void setShotListener(ShotListener shotListener) {
        presenter().setShotListener(shotListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        getRenderer().drawNautical(canvas);
        super.onDraw(canvas);

        if (mAim != null) {
            getRenderer().drawAim(canvas, presenter().getAimRectDst(mAim));
        }

        if (presenter().startedDragging()) {
            int i = presenter().getTouchedI();
            int j = presenter().getTouchedJ();

            if (Board.containsCell(i, j)) {
                Aiming aiming = presenter().getAiming(i, j, 1, 1);
                mRenderer.render(canvas, aiming, getAimingPaint(mBoard.getCell(i, j).beenShot()));
            }
        }

        if (getRenderer().isAnimationRunning()) {
            postInvalidateDelayed(getRenderer().animateExplosions(canvas, mLastShotResult.aim));
        }

//        if (GameConstants.IS_TEST_MODE) {
//            getRenderer().render(canvas, mTouchState.getX(), mTouchState.getY());
//        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        presenter().touch(event);
        invalidate();

        return true;
    }

    private Paint getAimingPaint(boolean isShot) {
        return isShot || presenter().isLocked() ? mAimingLockedPaint : mAimingPaint;
    }

    public void setAim(Vector2 aim) {
        mAim = aim;
        invalidate();
    }

    public void removeAim() {
        mAim = null;
        invalidate();
    }

    public boolean isLocked() {
        return presenter().isLocked();
    }

    public void lock() {
        presenter().lock();
    }

    public void unLock() {
        presenter().unlock();
    }

    public void setShotResult(PokeResult result) {
        mLastShotResult = result;
        getRenderer().startAnimation(result);
    }
}
