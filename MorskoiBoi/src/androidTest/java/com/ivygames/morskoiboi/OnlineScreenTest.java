package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.model.Opponent;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class OnlineScreenTest extends ScreenTest {

    protected static final String OPPONENT_NAME = "Sagi";
    protected Game game;

    @Override
    public void setup() {
        super.setup();
        game = mock(Game.class);
        Model.instance.game = game;
        Opponent opponent = mock(Opponent.class);
        Model.instance.opponent = opponent;
        when(opponent.getName()).thenReturn(OPPONENT_NAME);
    }

    @NonNull
    protected Matcher<View> cancelButton() {
        return withText(R.string.cancel);
    }

    @NonNull
    protected Matcher<View> okButton() {
        return withText(R.string.ok);
    }

    protected void backToSelectGameCommand() {
        verifyGameFinished();
        checkDisplayed(SELECT_GAME_LAYOUT);
    }

    private void verifyGameFinished() {
        verify(game, times(1)).finish();
    }

    protected void setGameType(Game.Type type) {
        when(game.getType()).thenReturn(type);
    }
}
