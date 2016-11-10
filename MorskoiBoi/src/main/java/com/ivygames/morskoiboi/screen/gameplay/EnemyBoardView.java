package com.ivygames.morskoiboi.screen.gameplay;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.example.games.basegameutils.BuildConfig;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.ShotResult;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.renderer.EnemyBoardGeometryProcessor;
import com.ivygames.morskoiboi.renderer.EnemyBoardRenderer;
import com.ivygames.morskoiboi.screen.view.BaseBoardView;
import com.ivygames.morskoiboi.screen.view.TouchState;

public class EnemyBoardView extends BaseBoardView {

    private ShotResult mLastShotResult;
    private Vector2 mAim;
    @NonNull
    private final EnemyBoardPresenter mPresenter;
    @NonNull
    private final EnemyBoardRenderer mRenderer;
    @NonNull
    private final TouchState mTouchState = new TouchState();

    public EnemyBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        mPresenter = new EnemyBoardPresenter();
        mRenderer = (EnemyBoardRenderer) super.mRenderer;
    }

    @NonNull
    @Override
    protected EnemyBoardRenderer renderer() {
        EnemyBoardGeometryProcessor processor = new EnemyBoardGeometryProcessor(10, getResources().getDimension(R.dimen.ship_border));
        return new EnemyBoardRenderer(getResources(), processor);
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

        if (mTouchState.isDragging()) {
            int i = getI();
            int j = getJ();

            if (Board.contains(i, j)) {
                boolean locked = mBoard.getCell(i, j).beenShot() || mPresenter.isLocked();
                mRenderer.drawAiming(canvas, i, j, locked);
            }
        }

        if (mRenderer.isAnimationRunning()) {
            postInvalidateDelayed(mRenderer.animateExplosions(canvas, mLastShotResult.aim));
        }

        if (BuildConfig.DEBUG) {
            mRenderer.drawDebug(canvas, mTouchState.getX(), mTouchState.getY());
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        mTouchState.setEvent(event);
        mPresenter.touch(mTouchState, Vector2.get(getI(), getJ()));
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

    public void setShotResult(@NonNull ShotResult result) {
        mLastShotResult = result;
        mRenderer.startAnimation(result);
    }

    private int getI() {
        return mRenderer.xToI(mTouchState.getX());
    }

    private int getJ() {
        return mRenderer.yToJ(mTouchState.getY());
    }
}
