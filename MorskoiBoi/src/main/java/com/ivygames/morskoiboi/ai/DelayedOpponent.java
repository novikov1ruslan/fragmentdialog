package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.Cancellable;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

final class DelayedOpponent implements Opponent, Cancellable {

    private Thread mThread;
    private final Opponent mOpponent;
    private final Board mMyBoard;

    DelayedOpponent(Opponent opponent, Board myBoard) {
        mOpponent = opponent;
        mMyBoard = myBoard;
    }

    @Override
    public void onShotAt(Vector2 aim) {
        join();
        mThread = new Thread(new ShootAtOpponentCommand(mOpponent, aim, false), "bot");
        mThread.start();
    }

    @Override
    public void onShotResult(PokeResult result) {
        join();
        mThread = new Thread(new PassShotResultToOpponentCommand(mOpponent, result, mMyBoard), "bot");
        mThread.start();
    }

    @Override
    public void go() {
        join();
        mThread = new Thread(new GoCommand(mOpponent), "go_bot");
        mThread.start();
    }

    @Override
    public void setOpponent(Opponent opponent) {

    }

    @Override
    public void onEnemyBid(int bid) {
        join();
        mThread = new Thread(new OnEnemyBidCommand(mOpponent, bid), "bidding_bot");
        mThread.start();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void onLost(Board board) {

    }

    @Override
    public void setOpponentVersion(int ver) {

    }

    @Override
    public void onNewMessage(String text) {

    }

    @Override
    public void cancel() {
        if (mThread == null) {
            Ln.v("AI not running");
        } else {
            Ln.d("stopping AI");
            mThread.interrupt();
            join();
        }
    }

    void join() {
        if (mThread != null && mThread.isAlive()) {
            try {
                Ln.w("need to join");
                mThread.interrupt();
                mThread.join();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
