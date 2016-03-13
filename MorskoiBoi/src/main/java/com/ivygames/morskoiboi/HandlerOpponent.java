package com.ivygames.morskoiboi;

import android.os.Handler;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

public class HandlerOpponent implements Opponent {

    private final Handler mHandler;
    private final Opponent mOpponent;
    private boolean mStopped;

    public HandlerOpponent(Handler handler, Opponent opponent) {
        mHandler = handler;
        mOpponent = opponent;
    }

    @Override
    public void onShotResult(final PokeResult pokeResult) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mStopped) {
                    Ln.w("already stopped");
                    return;
                }
                mOpponent.onShotResult(pokeResult);
            }
        });
    }

    @Override
    public void onShotAt(final Vector2 aim) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mStopped) {
                    Ln.w("already stopped");
                    return;
                }
                mOpponent.onShotAt(aim);
            }
        });
    }

    @Override
    public void go() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mStopped) {
                    Ln.w("already stopped");
                    return;
                }
                mOpponent.go();
            }
        });
    }

    @Override
    public void setOpponent(final Opponent opponent) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mStopped) {
                    Ln.w("already stopped");
                    return;
                }
                mOpponent.setOpponent(opponent);
            }
        });
    }

    @Override
    public void onEnemyBid(final int bid) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mStopped) {
                    Ln.w("already stopped");
                    return;
                }
                mOpponent.onEnemyBid(bid);
            }
        });
    }

    @Override
    public String getName() {
        return mOpponent.getName();
    }

    @Override
    public void onLost(final Board board) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mStopped) {
                    Ln.w("already stopped");
                    return;
                }
                mOpponent.onLost(board);
            }
        });
    }

    @Override
    public void setOpponentVersion(final int ver) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mStopped) {
                    Ln.w("already stopped");
                    return;
                }
                mOpponent.setOpponentVersion(ver);
            }
        });
    }

    @Override
    public void onNewMessage(final String text) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mStopped) {
                    Ln.w("already stopped");
                    return;
                }
                mOpponent.onNewMessage(text);
            }
        });
    }

    @Override
    public String toString() {
        return mOpponent.toString();
    }

    public void stop() {
        Ln.d("stopped");
        mStopped = true;
    }
}
