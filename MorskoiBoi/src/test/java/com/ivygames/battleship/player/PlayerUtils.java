package com.ivygames.battleship.player;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Rules;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlayerUtils {
    @NonNull
    public static Rules defeatedBoardRules(int numberOfShips) {
        Rules rules = mock(Rules.class);
        when(rules.getAllShipsSizes()).thenReturn(new int[numberOfShips]);
        return rules;
    }
}
