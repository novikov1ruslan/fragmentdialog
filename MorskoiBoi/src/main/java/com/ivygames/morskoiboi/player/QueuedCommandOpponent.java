package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Coord;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.morskoiboi.ai.Cancellable;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class has to be {@link Cancellable}, because whatever it wrap can be cancellable.
 * So this class should be able to propagate call to the delegate.
 */
public class QueuedCommandOpponent implements Opponent, Cancellable {

    @NonNull
    private final Opponent mDelegate;

    public QueuedCommandOpponent(@NonNull Opponent opponent) {
        mDelegate = opponent;
    }

    private Queue<Runnable> mQ = new LinkedList<>();

    public void executePendingCommands() {
        while (mQ.size() > 0) {
            mQ.poll().run();
        }
    }

    @Override
    public void onShotAt(@NonNull final Coord aim) {
        mQ.offer(new Runnable() {
            @Override
            public void run() {
                mDelegate.onShotAt(aim);
            }
        });
    }

    @Override
    public void onShotResult(@NonNull final ShotResult shotResult) {
        mQ.offer(new Runnable() {
            @Override
            public void run() {
                mDelegate.onShotResult(shotResult);
            }
        });
    }

    @Override
    public void go() {
        mQ.offer(new Runnable() {
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
        mQ.offer(new Runnable() {
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
        mQ.offer(new Runnable() {
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
        // Message can be sent directly, without command
        mQ.offer(new Runnable() {
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
        return "(C:" + mDelegate + ")";
    }
}
