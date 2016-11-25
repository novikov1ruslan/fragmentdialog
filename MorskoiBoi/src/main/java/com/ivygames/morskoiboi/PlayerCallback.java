package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.shot.ShotResult;

public interface PlayerCallback {

    void onPlayerShotResult(@NonNull ShotResult result);

    void onWin();

    void onKillPlayer();

    void onKillEnemy();

    void onMiss();

    void onHit();

    void onPlayerShotAt();

    void onPlayerLost(@Nullable Board board);

    void opponentReady();

    void onOpponentTurn();

    void onPlayersTurn();

    void onMessage(@NonNull String message);
}
