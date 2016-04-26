package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.CancellableOpponent;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class DelayedOpponent extends DummyOpponent implements CancellableOpponent {
    private static int sCounter;
    private static final boolean NO_NEED_TO_THINK = false;
    private Opponent mOpponent;
    private boolean mShouldWait = true;
    private ExecutorService mExecutor;

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        mOpponent = opponent;
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        mShouldWait = true;
        mExecutor.submit(new OnShootAtCommand(mOpponent, aim, NO_NEED_TO_THINK));
    }

    @Override
    public void onShotResult(@NonNull PokeResult result) {
        mShouldWait = false;
        mExecutor.submit(new OnShotResultCommand(mOpponent, result));
    }

    @Override
    public void go() {
        mExecutor.submit(new GoCommand(mOpponent, mShouldWait));
        mShouldWait = true;
    }

    @Override
    public void onEnemyBid(int bid) {
        mShouldWait = true;
        mExecutor.submit(new OnEnemyBidCommand(mOpponent, bid));
    }

    @Override
    public void init() {
        mExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "bot" + sCounter++);
            }
        });
    }

    @Override
    public void cancel() {
        mExecutor.shutdownNow();
        try {
            mExecutor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

}
