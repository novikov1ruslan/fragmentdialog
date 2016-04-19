package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.lost.LostScreen;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class LostScreenTest extends ScreenTest {
    private static final String OPPONENT_NAME = "Sagi";

    private Game game;

    @Before
    public void setup() {
        super.setup();
        game = mock(Game.class);
        Opponent opponent = mock(Opponent.class);
        Model.instance.game = game;
        Model.instance.opponent = opponent;
        when(opponent.getName()).thenReturn(OPPONENT_NAME);
    }

    @Override
    public BattleshipScreen newScreen() {
        return new LostScreen(activity());
    }

    @Test
    public void WhenScreenDisplayed__GamesCounterIncremented() {
        showScreen();
        verify(settings(), times(1)).incrementGamesPlayedCounter();
    }

    @Test
    public void WhenBackButtonPressedForAndroid__SelectGameScreenShown() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        pressBack();
        backToSelectGameCommand();
    }

    @Test
    public void WhenBackButtonPressedForNonAndroid__SelectGameScreenShown() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        pressBack();
        String message = getString(R.string.want_to_leave_room, OPPONENT_NAME);
        checkDisplayed(withText(message));
    }

    @Test
    public void AfterNoPressedForAndroid__SelectGameScreenShown() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        clickOn(noButton());
        backToSelectGameCommand();
    }

    @Test
    public void AfterNoPressedForNonAndroid__WantToLeaveDialogDisplayed() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        clickOn(noButton());
        String message = getString(R.string.want_to_leave_room, OPPONENT_NAME);
        checkDisplayed(withText(message));
    }

    @Test
    public void AfterYesPressed__GameStateClearedBoardSetupScreenShown() {
        showScreen();
        clickOn(yesButton());
        verify(game, times(1)).clearState();
        checkDisplayed(BOARD_SETUP_LAYOUT);
    }

    private void setGameType(Game.Type type) {
        when(game.getType()).thenReturn(type);
    }

    @NonNull
    private Matcher<View> noButton() {
        return withText(R.string.no);
    }

    @NonNull
    private Matcher<View> yesButton() {
        return withText(R.string.yes);
    }

    private void backToSelectGameCommand() {
        verify(game, times(1)).finish();
        checkDisplayed(SELECT_GAME_LAYOUT);
    }
}
