package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

public class DummyOpponent implements Opponent {
    @Override
    public void onShotAt(Vector2 aim) {

    }

    @Override
    public void onShotResult(PokeResult pokeResult) {

    }

    @Override
    public void go() {

    }

    @Override
    public void setOpponent(Opponent opponent) {

    }

    @Override
    public void onEnemyBid(int bid) {

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
}
