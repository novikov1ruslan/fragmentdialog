package com.ivygames.morskoiboi.ai;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

public class DelayedOpponent extends DummyOpponent implements CancellableOpponent {
    private static final int START_TIMEOUT = 3000;
    private static final int WHISTLE_SOUND_DELAY = 1300;
    private static final boolean NO_NEED_TO_THINK = false;

    private Opponent mOpponent;
    private boolean mShouldWait = true;

    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Nullable
    private Runnable mOnEnemyBidCommand;
    @Nullable
    private Runnable mGoCommand;
    @Nullable
    private Runnable mOnShotResultCommand;
    @Nullable
    private Runnable mOnShootAtCommand;

    private static long getThinkingTime(boolean needThinking) {
        int extraTime = needThinking ? 1000 : 0;
        return (long) (1000 + (int) (Math.random() * (500 + extraTime)));
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        mOpponent = opponent;
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        mShouldWait = true;
        mOnShootAtCommand = new OnShootAtCommand(mOpponent, aim);
        long thinkingTime = getThinkingTime(NO_NEED_TO_THINK);
        Ln.v("scheduling " + mOnShootAtCommand + " in " + thinkingTime);
        mHandler.postDelayed(mOnShootAtCommand, thinkingTime);
    }

    @Override
    public void onShotResult(@NonNull PokeResult result) {
        mShouldWait = false;
        mOnShotResultCommand = new OnShotResultCommand(mOpponent, result);
        Ln.v("scheduling " + mOnShotResultCommand + " in " + WHISTLE_SOUND_DELAY);
        mHandler.postDelayed(mOnShotResultCommand, WHISTLE_SOUND_DELAY);
    }

    @Override
    public void go() {
        mGoCommand = new GoCommand(mOpponent);
        int delay = mShouldWait ? START_TIMEOUT : 0;
        Ln.v("scheduling " + mGoCommand + " in " + delay);
        mHandler.postDelayed(mGoCommand, delay);
        mShouldWait = true;
    }

    @Override
    public void onEnemyBid(int bid) {
        mShouldWait = true;
        mOnEnemyBidCommand = new OnEnemyBidCommand(mOpponent, bid);
        Ln.v("scheduling " + mOnEnemyBidCommand + " in " + START_TIMEOUT);
        mHandler.postDelayed(mOnEnemyBidCommand, START_TIMEOUT);
    }

    @Override
    public void cancel() {
        Ln.v("cancelling all commands");
        mHandler.removeCallbacks(mGoCommand);
        mHandler.removeCallbacks(mOnEnemyBidCommand);
        mHandler.removeCallbacks(mOnShootAtCommand);
        mHandler.removeCallbacks(mOnShotResultCommand);
    }

}
