package com.ivygames.battleship;

import android.support.annotation.NonNull;

import com.ivygames.battleship.player.PlayerOpponent;

public interface PlayerFactory {
    PlayerOpponent createPlayer(@NonNull String name, int numberOfShips);
}
