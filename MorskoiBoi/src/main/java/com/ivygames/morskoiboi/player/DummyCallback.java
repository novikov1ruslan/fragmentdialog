package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Coordinate;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.morskoiboi.PlayerCallback;

public class DummyCallback implements PlayerCallback {

    @Override
    public void onShotResult(@NonNull ShotResult result) {

    }

    @Override
    public void onWin() {

    }

    @Override
    public void onKillPlayer() {

    }

    @Override
    public void onKillEnemy() {

    }

    @Override
    public void onMiss() {

    }

    @Override
    public void onHit() {

    }

    @Override
    public void onShotAt(@NonNull Coordinate aim) {

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
