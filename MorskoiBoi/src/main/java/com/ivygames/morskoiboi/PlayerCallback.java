package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.ShotResult;
import com.ivygames.morskoiboi.model.Vector2;

public interface PlayerCallback {

    void onShotResult(@NonNull ShotResult result);

    void onWin();

    void onKillPlayer();

    void onKillEnemy();

    void onMiss();

    void onHit();

    void onShotAt(@NonNull Vector2 aim);

    void onLost(@Nullable Board board);

    void opponentReady();

    void onOpponentTurn();

    void onPlayersTurn();

    void onMessage(@NonNull String message);
}
