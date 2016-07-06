package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.PlayerCallback;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

public class DummyCallback implements PlayerCallback {

    @Override
    public void onShotResult(@NonNull PokeResult result) {

    }

    @Override
    public void onWin() {

    }

    @Override
    public void onKill(@NonNull Side side) {

    }

    @Override
    public void onMiss(@NonNull Side side) {

    }

    @Override
    public void onHit(@NonNull Side side) {

    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {

    }

    @Override
    public void onLost(@Nullable Board board) {

    }

    @Override
    public void opponentReady() {

    }

    @Override
    public void onOpponentTurn() {

    }

    @Override
    public void onPlayersTurn() {

    }

    @Override
    public void onMessage(@NonNull String message) {

    }
}
