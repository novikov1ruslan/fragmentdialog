package com.ivygames.morskoiboi.screen.gameplay;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.example.games.basegameutils.BuildConfig;
import com.ivygames.battleship.board.Vector2;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.renderer.EnemyBoardGeometryProcessor;
import com.ivygames.morskoiboi.renderer.EnemyBoardRenderer;
import com.ivygames.morskoiboi.screen.boardsetup.BoardUtils;
import com.ivygames.morskoiboi.screen.view.BaseBoardView;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.List;

public class EnemyBoardView extends BaseBoardView {

    private ShotResult mLastShotResult;
    @Nullable
    private Vector2 mLockAim;
    @NonNull
    private final EnemyBoardPresenter mPresenter;
    @NonNull
    private final EnemyBoardRenderer mRenderer;
    private MotionEvent debug_last_event;
    @NonNull
    private Vector2 mAiming = Vector2.INVALID_VECTOR;
    @NonNull
    private List<Vector2> mPossibleShots = new ArrayList<>();

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

        if (mLockAim != null) {
            mRenderer.drawAim(canvas, mLockAim);
        }

        if (mPresenter.isDragging()) {
            if (mAiming != Vector2.INVALID_VECTOR) {
                mRenderer.drawAiming(canvas, mAiming, isLocked(mAiming));
            }
        }

        if (mRenderer.isAnimationRunning()) {
            postInvalidateDelayed(mRenderer.animateExplosions(canvas, mLastShotResult.aim));
        }

        if (BuildConfig.DEBUG) {
            mRenderer.drawDebug(canvas, debug_last_event.getX(), debug_last_event.getY());
        }
    }

    private boolean isLocked(@NonNull Vector2 v) {
        return !isEmpty(v) || mPresenter.isLocked();
    }

    private boolean isEmpty(@NonNull Vector2 v) {
        return mPossibleShots.contains(v);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        debug_last_event = event;
        mAiming = Vector2.get(getI(event.getX()), getJ(event.getY()));
        logAction(event);
        mPresenter.touch(event.getAction(), mAiming);
        invalidate();

        return true;
    }

    private void logAction(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Ln.v("DOWN: x=" + event.getX() + "; y=" + event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Ln.v("UP: x=" + event.getX() + "; y=" + event.getY());
        }
    }

    public void setLockAim(@NonNull Vector2 aim) {
        mLockAim = aim;
        invalidate();
    }

    public void removeLockAim() {
        mLockAim = null;
        invalidate();
    }

    public boolean isLocked() {
        return mPresenter.isLocked();
    }

    public void lock() {
        mPresenter.lock();
    }

    public void unLock() {
        mPossibleShots = BoardUtils.getPossibleShots(mBoard, Dependencies.getRules().allowAdjacentShips());
        mPresenter.unlock();
    }

    public void setShotResult(@NonNull ShotResult result) {
        mLastShotResult = result;
        mRenderer.startAnimation(result);
    }

    private int getI(float x) {
        return mRenderer.xToI((int) x);
    }

    private int getJ(float y) {
        return mRenderer.yToJ((int) y);
    }
}
