package com.ivygames.morskoiboi.screen.gameplay;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.view.Aiming;
import com.ivygames.morskoiboi.screen.view.BaseBoardView;

public class EnemyBoardView extends BaseBoardView {

    private PokeResult mLastShotResult;
    private Vector2 mAim;
    private EnemyBoardPresenter mPresenter;
    @NonNull
    private final EnemyBoardRenderer mRenderer;
    @NonNull
    private final Aiming mAiming = new Aiming();

    public EnemyBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        mRenderer = (EnemyBoardRenderer) super.mRenderer;
    }

    @NonNull
    @Override
    protected EnemyBoardRenderer renderer() {
        mPresenter = new EnemyBoardPresenter(10, getResources().getDimension(R.dimen.ship_border));
        return new EnemyBoardRenderer(getResources(), mPresenter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRenderer.init(availableMemory());
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
        mRenderer.release();
    }

    public void setShotListener(@NonNull ShotListener shotListener) {
        mPresenter.setShotListener(shotListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRenderer.drawNautical(canvas);
        super.onDraw(canvas);

        if (mAim != null) {
            mRenderer.drawAim(canvas, mAim);
        }

        if (mPresenter.startedDragging()) {
            int i = mPresenter.getTouchedI();
            int j = mPresenter.getTouchedJ();

            if (Board.containsCell(i, j)) {
                mAiming.set(i, j, 1, 1);
                boolean locked = isLocked(mBoard.getCell(i, j).beenShot());
                mRenderer.drawAiming(canvas, mAiming, locked);
            }
        }

        if (mRenderer.isAnimationRunning()) {
            postInvalidateDelayed(mRenderer.animateExplosions(canvas, mLastShotResult.aim));
        }

//        if (GameConstants.IS_TEST_MODE) {
//            getRenderer().render(canvas, mTouchState.getX(), mTouchState.getY());
//        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        mPresenter.touch(event);
        invalidate();

        return true;
    }

    private boolean isLocked(boolean isShot) {
        return isShot || mPresenter.isLocked();
    }

    public void setAim(@NonNull Vector2 aim) {
        mAim = aim;
        invalidate();
    }

    public void removeAim() {
        mAim = null;
        invalidate();
    }

    public boolean isLocked() {
        return mPresenter.isLocked();
    }

    public void lock() {
        mPresenter.lock();
    }

    public void unLock() {
        mPresenter.unlock();
    }

    public void setShotResult(@NonNull PokeResult result) {
        mLastShotResult = result;
        mRenderer.startAnimation(result);
    }
}
