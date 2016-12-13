package com.ivygames.battleship.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.morskoiboi.PlayerCallback;

import org.commons.logger.Ln;

import java.util.HashSet;
import java.util.Set;

class AggregatePlayerCallback implements PlayerCallback {
    @NonNull
    private final Set<PlayerCallback> mCallbacks = new HashSet<>();

    void registerCallback(PlayerCallback callback) {
        mCallbacks.add(callback);
    }

    void unregisterCallback(@NonNull PlayerCallback callback) {
        mCallbacks.remove(callback);
        Ln.v("callback removed: " + callback);
    }

    @Override
    public void onWin() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onWin();
        }
    }

    @Override
    public void onKillPlayer() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onKillPlayer();
        }
    }

    @Override
    public void onKillEnemy() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onKillEnemy();
        }
    }

    @Override
    public void onMiss() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onMiss();
        }
    }

    @Override
    public void onHit() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onHit();
        }
    }

    @Override
    public void onPlayerLost(@Nullable Board board) {
        for (PlayerCallback callback : mCallbacks) {
            callback.onPlayerLost(board);
        }
    }

    @Override
    public void onPlayerShotResult(@NonNull ShotResult result) {
        for (PlayerCallback callback : mCallbacks) {
            callback.onPlayerShotResult(result);
        }
    }

    @Override
    public void opponentReady() {
        for (PlayerCallback callback : mCallbacks) {
            callback.opponentReady();
        }
    }

    @Override
    public void onOpponentTurn() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onOpponentTurn();
        }
    }

    @Override
    public void onPlayersTurn() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onPlayersTurn();
        }
    }

    @Override
    public void onMessage(@NonNull String message) {
        for (PlayerCallback callback : mCallbacks) {
            callback.onMessage(message);
        }
    }

    @Override
    public void onPlayerShotAt() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onPlayerShotAt();
        }
    }
}
