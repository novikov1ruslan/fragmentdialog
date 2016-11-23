package com.ivygames.morskoiboi.renderer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Coord;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.common.gfx.Animation;
import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;

import org.commons.logger.Ln;

import java.util.Random;

public class EnemyBoardRenderer extends BaseBoardRenderer {
    private static final int TEXTURE_SIZE = 512;
    private static final float CELL_RATIO = 2f;

    @NonNull
    private final Animation mSplashAnimation = new Animation(1000);
    @NonNull
    private final Animation mExplosionAnimation = new Animation(1000);

    private Bitmap mNauticalBitmap;
    private Rect mSrcRect;

    private Bitmap mLockBitmapSrc;
    private Rect mLockSrcRect;

    private Animation mCurrentAnimation;
    @NonNull
    private final EnemyBoardGeometryProcessor mProcessor;
    @NonNull
    private final Resources mResources;

    public EnemyBoardRenderer(@NonNull Resources resources, @NonNull EnemyBoardGeometryProcessor processor) {
        super(resources, processor);
        mProcessor = processor;
        mResources = resources;

        fillSplashAnimation(resources);
        fillExplosionAnimation(resources);
    }

    private void fillSplashAnimation(@NonNull Resources resources) {
        mSplashAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.splash_01));
        mSplashAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.splash_02));
        mSplashAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.splash_03));
        mSplashAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.splash_04));
        mSplashAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.splash_05));
        mSplashAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.splash_06));
        mSplashAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.splash_07));
        mSplashAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.splash_08));
        mSplashAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.splash_09));
        mSplashAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.splash_10));
    }

    private void fillExplosionAnimation(@NonNull Resources resources) {
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_01));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_03));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_05));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_06));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_07));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_08));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_09));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_10));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_12));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_13));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_14));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_15));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_17));
        mExplosionAnimation.adFrame(Bitmaps.getBitmap(resources, R.drawable.explosion_18));
    }

    public boolean isAnimationRunning() {
        return mCurrentAnimation != null && mCurrentAnimation.isRunning();
    }

    public long animateExplosions(@NonNull Canvas canvas, @NonNull Coord aim) {
        canvas.drawBitmap(mCurrentAnimation.getCurrentFrame(), mCurrentAnimation.getBounds(),
                mProcessor.getAnimationDestination(aim, CELL_RATIO), null);
        return mCurrentAnimation.getFrameDuration();
    }

    public void startAnimation(@NonNull ShotResult result) {
        mCurrentAnimation = result.cell == Cell.MISS ? mSplashAnimation : mExplosionAnimation;
        mCurrentAnimation.start();
    }

    public void drawNautical(@NonNull Canvas canvas) {
        if (mNauticalBitmap != null) {
            canvas.drawBitmap(mNauticalBitmap, mSrcRect, mProcessor.getBoardRect(), null);
        }
    }

    public void drawAiming(@NonNull Canvas canvas, @NonNull Coord v, boolean locked) {
        drawAiming(canvas, v.i, v.j, locked);
    }

    public void drawAiming(@NonNull Canvas canvas, int i, int j, boolean locked) {
        drawAiming(canvas, mProcessor.getAimingG(i, j), locked);
    }

    public void drawAim(@NonNull Canvas canvas, @NonNull Coord aim) {
        drawAim(canvas, mProcessor.getAimRectDst(aim));
    }

    public void drawAim(@NonNull Canvas canvas, @NonNull Rect rectDst) {
        canvas.drawBitmap(mLockBitmapSrc, mLockSrcRect, rectDst, null);
    }

    public void init(long availableMemory) {
        mNauticalBitmap = BitmapFactory.decodeResource(mResources, R.drawable.nautical8);
        if (mNauticalBitmap == null) {
            Ln.e("could not decode nautical");
        } else {
            final Random random = new Random();
            int x = random.nextInt(mNauticalBitmap.getWidth() - TEXTURE_SIZE);
            int y = random.nextInt(mNauticalBitmap.getHeight() - TEXTURE_SIZE);
            mSrcRect = new Rect(x, y, x + TEXTURE_SIZE, y + TEXTURE_SIZE);
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
