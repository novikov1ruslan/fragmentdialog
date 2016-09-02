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
import com.ivygames.morskoiboi.screen.view.TouchState;

public class EnemyBoardView extends BaseBoardView {

    private PokeResult mLastShotResult;
    private Vector2 mAim;
    private EnemyBoardPresenter mPresenter;
    @NonNull
    private final EnemyBoardRenderer mRenderer;
    @NonNull
    private final TouchState mTouchState = new TouchState();
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

        if (startedDragging()) {
            int i = mPresenter.xToI(mTouchState.getX());
            int j = mPresenter.yToJ(mTouchState.getY());

            if (Board.containsCell(i, j)) {
                boolean locked = mBoard.getCell(i, j).beenShot() || mPresenter.isLocked();
                mRenderer.drawAiming(canvas, getAiming(1, 1), locked);
            }
        }

        if (mRenderer.isAnimationRunning()) {
            postInvalidateDelayed(mRenderer.animateExplosions(canvas, mLastShotResult.aim));
        }

//        if (GameConstants.IS_TEST_MODE) {
//            getRenderer().render(canvas, mTouchState.getX(), mTouchState.getY());
//        }
    }

    private boolean startedDragging() {
        return mTouchState.getDragStatus() == TouchState.START_DRAGGING;
    }

    private Aiming getAiming(int width, int height) {
        int i = mPresenter.xToI(mTouchState.getX());
        int j = mPresenter.yToJ(mTouchState.getY());
        mAiming.set(i, j, width, height);
        return mAiming;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        mTouchState.setEvent(event);
        mPresenter.touch(mTouchState);
        invalidate();

        return true;
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
