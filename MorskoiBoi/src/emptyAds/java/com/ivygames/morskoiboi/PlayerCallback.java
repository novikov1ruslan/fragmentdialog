package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

public interface PlayerCallback {

    enum Side {
        PLAYER,
        OPPONENT
    };

    void go();

    void onShotResult(@NonNull PokeResult result);

    void onWin();

    void onKill(@NonNull Side side);

    void onMiss(@NonNull Side side);

    void onHit(@NonNull Side side);

    void onShotAt(@NonNull Vector2 aim);

    void onLost();

    void opponentReady();

    void onOpponentTurn();

}
