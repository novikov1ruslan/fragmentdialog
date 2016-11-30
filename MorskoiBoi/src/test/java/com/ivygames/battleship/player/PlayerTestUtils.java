package com.ivygames.battleship.player;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Rules;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerTestUtils {
    @NonNull
    static Rules defeatedBoardRules(int numberOfShips) {
        Rules rules = mock(Rules.class);
        when(rules.getAllShipsSizes()).thenReturn(new int[numberOfShips]);
        return rules;
    }
}
