package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

class DummyOpponent implements Opponent {
    @Override
    public void onShotAt(@NonNull Vector2 aim) {

    }

    @Override
    public void onShotResult(@NonNull PokeResult pokeResult) {

    }

    @Override
    public void go() {

    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {

    }

    @Override
    public void onEnemyBid(int bid) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void onLost(@NonNull Board board) {

    }

    @Override
    public void setOpponentVersion(int ver) {

    }

    @Override
    public void onNewMessage(@NonNull String text) {

    }
}
