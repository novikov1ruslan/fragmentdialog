package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

public class DelegateOpponent implements CancellableOpponent {

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
    public void onShotResult(@NonNull PokeResult pokeResult) {
        mOpponent.onShotResult(pokeResult);
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
