package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Coordinate;
import com.ivygames.battleship.shot.ShotResult;

public interface PlayerCallback {

    void onShotResult(@NonNull ShotResult result);

    void onWin();

    void onKillPlayer();

    void onKillEnemy();

    void onMiss();

    void onHit();

    void onShotAt(@NonNull Coordinate aim);

    void onLost(@Nullable Board board);

    void opponentReady();

    void onOpponentTurn();

    void onPlayersTurn();

    void onMessage(@NonNull String message);
}
