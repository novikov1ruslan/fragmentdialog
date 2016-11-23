package com.ivygames.morskoiboi.ai;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Coord;
import com.ivygames.battleship.shot.ShotResult;

import org.commons.logger.Ln;

public class DelayedOpponent implements Opponent, Cancellable {
    private static final int WHISTLE_SOUND_DELAY = 1300;
    private static final boolean NO_NEED_TO_THINK = false;

    @NonNull
    private final Opponent mOpponent;

    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Nullable
    private GoCommand mGoCommand;
    @Nullable
    private OnShotResultCommand mOnShotResultCommand;
    @Nullable
    private OnShootAtCommand mOnShootAtCommand;

    public DelayedOpponent(@NonNull Opponent opponent) {
        mOpponent = opponent;
    }

    private static long getThinkingTime(boolean needThinking) {
        int extraTime = needThinking ? 1000 : 0;
        return (long) (1000 + (int) (Math.random() * (500 + extraTime)));
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void onShotAt(@NonNull Coord aim) {
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

    @NonNull
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
        int canceled = 0;
        if (mGoCommand != null && !mGoCommand.executed()) {
            mHandler.removeCallbacks(mGoCommand);
            canceled++;
        }
        if (mOnShootAtCommand != null && !mOnShootAtCommand.executed()) {
            mHandler.removeCallbacks(mOnShootAtCommand);
            canceled++;
        }
        if (mOnShotResultCommand != null && !mOnShotResultCommand.executed()) {
            mHandler.removeCallbacks(mOnShotResultCommand);
            canceled++;
        }
        Ln.d("canceled " + canceled + " commands");
    }

    @Override
    public String toString() {
        return "(D:" + mOpponent + ")";
    }
}
