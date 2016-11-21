package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.battleship.board.Vector2;

public class DelegateOpponent implements Opponent, Cancellable {

    private Opponent mOpponent;
    public boolean cancelCalled;

    @Override
    public void cancel() {
        cancelCalled = true;
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        mOpponent.onShotAt(aim);
    }

    @Override
    public void onShotResult(@NonNull ShotResult shotResult) {
        mOpponent.onShotResult(shotResult);
    }

    @Override
    public void go() {
        mOpponent.go();
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        mOpponent = opponent;
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
}
