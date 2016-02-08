package com.ivygames.morskoiboi.ui.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.ivygames.morskoiboi.Animation;
import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.utils.UiUtils;

import org.commons.logger.Ln;

import java.util.Random;

class EnemyBoardRenderer {
    private static final int TEXTURE_SIZE = 512;

    private final Animation mSplashAnimation = new Animation(1000, 2f);
    private final Animation mExplosionAnimation = new Animation(1000, 2f);

    private Bitmap mNauticalBitmap;
    private Rect mSrcRect;

    private Bitmap mLockBitmapSrc;
    private Rect mLockSrcRect;


    private final EnemyBoardPresenter mPresenter;
    private Resources mResources;


    EnemyBoardRenderer(EnemyBoardPresenter presenter, Resources resources) {
        mPresenter = presenter;
        mResources = resources;

        fillSplashAnimation();
        fillExplosionAnimation();
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

    public long animateExplosions(Canvas canvas) {
        if (mExplosionAnimation.isRunning()) {
            return animate(mExplosionAnimation, canvas);
        } else if (mSplashAnimation.isRunning()) {
            return animate(mSplashAnimation, canvas);
        }
        return 0;
    }

    public void startAnimation(PokeResult result) {
        if (result.cell.isMiss()) {
            mSplashAnimation.setAim(result.aim);
            mSplashAnimation.start();
        } else {
            mExplosionAnimation.setAim(result.aim);
            mExplosionAnimation.start();
        }
    }

    private long animate(Animation animation, Canvas canvas) {
        canvas.drawBitmap(animation.getCurrentFrame(), animation.getBounds(), mPresenter.getAnimationDestination(animation), null);
        return animation.getFrameDuration();
    }

    public void drawNautical(Canvas canvas) {
        if (mNauticalBitmap != null) {
            canvas.drawBitmap(mNauticalBitmap, mSrcRect, getPresenter().getBoardRect(), null);
        }
    }

    public void drawAim(Canvas canvas) {
        if (getPresenter().hasAim()) {
            Rect rectDst = getPresenter().getAimRectDst();
            canvas.drawBitmap(mLockBitmapSrc, mLockSrcRect, rectDst, null);
        }
    }


    private EnemyBoardPresenter getPresenter() {
        return mPresenter;
    }

    public void init() {
        Bitmap tmp = BitmapFactory.decodeResource(mResources, R.drawable.nautical8);
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

        mLockBitmapSrc = BitmapFactory.decodeResource(mResources, R.drawable.lock);
        mLockSrcRect = new Rect(0, 0, mLockBitmapSrc.getWidth(), mLockBitmapSrc.getHeight());
    }

    public void release() {
        if (mNauticalBitmap != null) {
            mNauticalBitmap.recycle();
            mNauticalBitmap = null;
        }
        if (mLockBitmapSrc != null) {
            mLockBitmapSrc.recycle();
            mLockBitmapSrc = null;
        }
    }
}
