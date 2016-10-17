package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.PlayerCallback;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.ShotResult;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

public class DelegatePlayerCallback implements PlayerCallback {

    @NonNull
    private PlayerCallback mCallback = new DummyCallback();

    public void setCallback(@NonNull PlayerCallback callback) {
        mCallback = callback;
        Ln.v("callback set to: " + callback);
    }

    @Override
    public void onShotResult(@NonNull ShotResult result) {
        mCallback.onShotResult(result);
    }

    @Override
    public void onWin() {
        mCallback.onWin();
    }

    @Override
    public void onKill(@NonNull Side side) {
        mCallback.onKill(side);
    }

    @Override
    public void onMiss(@NonNull Side side) {
        mCallback.onMiss(side);
    }

    @Override
    public void onHit(@NonNull Side side) {
        mCallback.onHit(side);
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        mCallback.onShotAt(aim);
    }

    @Override
    public void onLost(@Nullable Board board) {
        mCallback.onLost(board);
    }

    @Override
    public void opponentReady() {
        mCallback.opponentReady();
    }

    @Override
    public void onOpponentTurn() {
        mCallback.onOpponentTurn();
    }

    @Override
    public void onPlayersTurn() {
        mCallback.onPlayersTurn();
    }

    @Override
    public void onMessage(@NonNull String message) {
        mCallback.onMessage(message);
    }

}
