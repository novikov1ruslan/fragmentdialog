package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlayerUtils {
    @NonNull
    public static Rules defeatedBoardRules() {
        Rules rules = mock(Rules.class);
        when(rules.isItDefeatedBoard(any(Board.class))).thenReturn(true);
        return rules;
    }
}
