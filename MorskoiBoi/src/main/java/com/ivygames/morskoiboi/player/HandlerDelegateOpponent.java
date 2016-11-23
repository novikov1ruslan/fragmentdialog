package com.ivygames.morskoiboi.player;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Coordinate;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.morskoiboi.ai.Cancellable;

class HandlerDelegateOpponent implements Opponent, Cancellable {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @NonNull
    private final Opponent mDelegate;

    HandlerDelegateOpponent(@NonNull Opponent opponent) {
        mDelegate = opponent;
    }

    @Override
    public void onShotAt(@NonNull final Coordinate aim) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onShotAt(aim);
            }
        });
    }

    @Override
    public void onShotResult(@NonNull final ShotResult shotResult) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onShotResult(shotResult);
            }
        });
    }

    @Override
    public void go() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.go();
            }
        });
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void onEnemyBid(final int bid) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onEnemyBid(bid);
            }
        });
    }

    @NonNull
    @Override
    public String getName() {
        return mDelegate.getName();
    }

    @Override
    public void onLost(@NonNull final Board board) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onLost(board);
            }
        });
    }

    @Override
    public void setOpponentVersion(int ver) {
        mDelegate.setOpponentVersion(ver);
    }

    @Override
    public void onNewMessage(@NonNull final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegate.onNewMessage(text);
            }
        });
    }

    @Override
    public void cancel() {
        if (mDelegate instanceof Cancellable) {
            ((Cancellable) mDelegate).cancel();
        }
    }

    @Override
    public String toString() {
        return "(H:" + mDelegate + ")";
    }

}
