package com.ivygames.morskoiboi.ai;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.ShotResult;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

public class DelayedOpponent implements Opponent, Cancellable {
    private static final int WHISTLE_SOUND_DELAY = 1300;
    private static final boolean NO_NEED_TO_THINK = false;

    private Opponent mOpponent;

    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Nullable
    private GoCommand mGoCommand;
    @Nullable
    private OnShotResultCommand mOnShotResultCommand;
    @Nullable
    private OnShootAtCommand mOnShootAtCommand;

    private static long getThinkingTime(boolean needThinking) {
        int extraTime = needThinking ? 1000 : 0;
        return (long) (1000 + (int) (Math.random() * (500 + extraTime)));
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        mOpponent = opponent;
        Ln.v("opponent set to " + opponent);
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        mOnShootAtCommand = new OnShootAtCommand(mOpponent, aim);
        long thinkingTime = getThinkingTime(NO_NEED_TO_THINK);
        Ln.v("scheduling " + mOnShootAtCommand + " in " + thinkingTime);
        mHandler.postDelayed(mOnShootAtCommand, thinkingTime);
    }

    @Override
    public void onShotResult(@NonNull ShotResult result) {
        mOnShotResultCommand = new OnShotResultCommand(mOpponent, result);
        Ln.v("scheduling " + mOnShotResultCommand + " in " + WHISTLE_SOUND_DELAY);
        mHandler.postDelayed(mOnShotResultCommand, WHISTLE_SOUND_DELAY);
    }

    @Override
    public void go() {
        if (mOnShotResultCommand == null || mOnShotResultCommand.executed()) {
            mOpponent.go();
        } else {
            mGoCommand = new GoCommand(mOpponent);
            Ln.v("scheduling " + mGoCommand + " after " + mOnShotResultCommand);
            assert mOnShotResultCommand != null;
            mOnShotResultCommand.setNextCommand(mGoCommand);
        }
    }

    @Override
    public void onEnemyBid(int bid) {
        mOpponent.onEnemyBid(bid);
    }

    @Override
    public String getName() {
        return mOpponent.getName();
    }

    @Override
    public void onLost(@NonNull Board board) {
        mOpponent.onLost(board);
    }

    @Override
    public void setOpponentVersion(int ver) {
        mOpponent.setOpponentVersion(ver);
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        mOpponent.onNewMessage(text);
    }

    @Override
    public void cancel() {
        Ln.v("cancelling all commands");
        mHandler.removeCallbacks(mGoCommand);
        mHandler.removeCallbacks(mOnShootAtCommand);
        mHandler.removeCallbacks(mOnShotResultCommand);
    }

    @Override
    public String toString() {
        return mOpponent.toString() + "(D)";
    }
}
