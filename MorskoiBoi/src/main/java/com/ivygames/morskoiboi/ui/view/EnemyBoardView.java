package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.Animation;
import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.utils.UiUtils;

import org.commons.logger.Ln;

import java.util.Random;

public class EnemyBoardView extends BaseBoardView {

    private static final int TEXTURE_SIZE = 512;

    private ShotListener mShotListener;
    private boolean mLocked;
    private final Paint mAimingLockedPaint;
    private Bitmap mNauticalBitmap;
    private Rect mSrcRect;

    private Bitmap mLockBitmapSrc;
    private Rect mLockSrcRect;

    private final Animation mSplashAnimation = new Animation(1000, 2f);
    private final Animation mExplosionAnimation = new Animation(1000, 2f);

    private final TouchState mTouchState = new TouchState();
    private int mTouchAction = mTouchState.getTouchAction();

    public interface ShotListener {
        void onShot(int i, int j);

        void onAimingStarted();

        void onAimingFinished();
    }

    public EnemyBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        mAimingLockedPaint = UiUtils.newFillPaint(getResources(), R.color.aim_locked);
        mLocked = true;

        fillSplashAnimation();
        fillExplosionAnimation();
    }

    @Override
    protected EnemyBoardPresenter getPresenter() {
        if (mPresenter == null) {
            mPresenter = new EnemyBoardPresenter(10, getResources().getDimension(R.dimen.ship_border));
        }
        return (EnemyBoardPresenter) mPresenter;
    }

    private void fillSplashAnimation() {
        Bitmaps bitmaps = Bitmaps.getInstance();
        mSplashAnimation.adFrame(bitmaps.getBitmap(R.drawable.splash_01));
        mSplashAnimation.adFrame(bitmaps.getBitmap(R.drawable.splash_02));
        mSplashAnimation.adFrame(bitmaps.getBitmap(R.drawable.splash_03));
        mSplashAnimation.adFrame(bitmaps.getBitmap(R.drawable.splash_04));
        mSplashAnimation.adFrame(bitmaps.getBitmap(R.drawable.splash_05));
        mSplashAnimation.adFrame(bitmaps.getBitmap(R.drawable.splash_06));
        mSplashAnimation.adFrame(bitmaps.getBitmap(R.drawable.splash_07));
        mSplashAnimation.adFrame(bitmaps.getBitmap(R.drawable.splash_08));
        mSplashAnimation.adFrame(bitmaps.getBitmap(R.drawable.splash_09));
        mSplashAnimation.adFrame(bitmaps.getBitmap(R.drawable.splash_10));
    }

    private void fillExplosionAnimation() {
        Bitmaps bitmaps = Bitmaps.getInstance();
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_01));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_03));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_05));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_06));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_07));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_08));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_09));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_10));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_12));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_13));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_14));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_15));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_17));
        mExplosionAnimation.adFrame(bitmaps.getBitmap(R.drawable.explosion_18));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Bitmap tmp = BitmapFactory.decodeResource(getResources(), R.drawable.nautical8);
        if (tmp == null) {
            Ln.e("could not decode nautical");
        } else {
            final Random mRandom = new Random(System.currentTimeMillis());
            int x = mRandom.nextInt(tmp.getWidth() - TEXTURE_SIZE);
            int y = mRandom.nextInt(tmp.getHeight() - TEXTURE_SIZE);
            mNauticalBitmap = Bitmap.createBitmap(tmp, x, y, TEXTURE_SIZE, TEXTURE_SIZE);
            mSrcRect = new Rect(0, 0, mNauticalBitmap.getWidth(), mNauticalBitmap.getHeight());
            if (mNauticalBitmap.getWidth() != tmp.getWidth() || mNauticalBitmap.getHeight() != tmp.getHeight()) {
                tmp.recycle();
            }
        }

        mLockBitmapSrc = BitmapFactory.decodeResource(getResources(), R.drawable.lock);
        mLockSrcRect = new Rect(0, 0, mLockBitmapSrc.getWidth(), mLockBitmapSrc.getHeight());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mNauticalBitmap != null) {
            mNauticalBitmap.recycle();
            mNauticalBitmap = null;
        }
        if (mLockBitmapSrc != null) {
            mLockBitmapSrc.recycle();
            mLockBitmapSrc = null;
        }
    }

    public void setShotListener(ShotListener shotListener) {
        mShotListener = shotListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mNauticalBitmap != null) {
            canvas.drawBitmap(mNauticalBitmap, mSrcRect, getPresenter().getBoardRect(), null);
        }

        super.onDraw(canvas);

        if (getPresenter().hasAim()) {
            Rect rectDst = getPresenter().getAimRectDst();
            canvas.drawBitmap(mLockBitmapSrc, mLockSrcRect, rectDst, null);
        }

        if (mTouchState.getDragStatus() == TouchState.START_DRAGGING) {
            drawAiming(canvas, getPresenter().getTouchedCellX(), getPresenter().getTouchedCellY(), 1, 1);
        }

        if (mExplosionAnimation.isRunning()) {
            animate(mExplosionAnimation, canvas);
        } else if (mSplashAnimation.isRunning()) {
            animate(mSplashAnimation, canvas);
        }
    }

    private void animate(Animation animation, Canvas canvas) {
        canvas.drawBitmap(animation.getCurrentFrame(), animation.getBounds(), getPresenter().getAnimationDestination(animation), null);
        postInvalidateDelayed(animation.getFrameDuration());
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
        // Ln.v(result);
        if (result.cell.isMiss()) {
            mSplashAnimation.setAim(result.aim);
            mSplashAnimation.start();
        } else {
            mExplosionAnimation.setAim(result.aim);
            mExplosionAnimation.start();
        }
    }

}
