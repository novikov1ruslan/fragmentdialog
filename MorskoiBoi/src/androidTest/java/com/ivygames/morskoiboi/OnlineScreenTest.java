package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.model.Board;
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
    protected PlayerOpponent player;
    protected Opponent opponent;

    @Override
    public void setup() {
        super.setup();
        opponent = mock(Opponent.class);
        Model.instance.opponent = opponent;
        when(opponent.getName()).thenReturn(OPPONENT_NAME);
        game = mock(Game.class);
        Model.instance.game = game;
        player = mock(PlayerOpponent.class);
        Model.instance.player = player;

        when(player.getBoard()).thenReturn(new Board());
        when(player.getEnemyBoard()).thenReturn(new Board());
    }

    @NonNull
    protected Matcher<View> wantToLeaveDialog() {
        return withText(getString(R.string.want_to_leave_room, OPPONENT_NAME));
    }

    protected final void setGameType(Game.Type type) {
        when(game.getType()).thenReturn(type);
    }
    // TODO: inline
    protected final void backToSelectGameCommand() {
        verifyGameFinished();
        checkDisplayed(SELECT_GAME_LAYOUT);
    }

    private void verifyGameFinished() {
        verify(game, times(1)).finish();
    }

}
