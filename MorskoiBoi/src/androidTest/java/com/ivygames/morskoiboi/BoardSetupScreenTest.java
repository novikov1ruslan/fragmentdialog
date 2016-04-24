package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class BoardSetupScreenTest extends OnlineScreenTest {

    private PlayerOpponent player;

    @Before
    public void setup() {
        super.setup();
        player = mock(PlayerOpponent.class);
        Model.instance.player = player;
    }

    @Override
    public BattleshipScreen newScreen() {
        return new BoardSetupScreen(activity());
    }

    public void WhenAutoPressed__BoardIsSet() {
        showScreen();
        clickOn(autoSetup());
        // TODO:
    }

    @Test
    public void WhenBoardIsSet__PressingDone_OpensGameplayScreen() {
        when(player.getBoard()).thenReturn(new Board());
        when(player.getEnemyBoard()).thenReturn(new Board());
        showScreen();
        clickOn(autoSetup());
        clickOn(done());
        checkDisplayed(GAMEPLAY_LAYOUT);
    }

    @Test
    public void WhenBoardNotSet__PressingDone_DisplaysMustSetShipsMessage() {
        showScreen();
        clickOn(done());
        checkDisplayed(mustSetShipsMessage());
    }

    public void ForInternetGame_IfPlayerSetsShipsForMoreThan60Seconds__SessionTimeoutDialogShown() {
        // TODO:
    }

    public void ForNonInternetGames_SetupTimeIsUnlimited() {
    // TODO:
    }

    public void WhenScreenDestroyed__AllCroutonsCanceled() {
    // TODO:
    }

    @Test
    public void WhenPressedBackForAndroidGame__GameFinishes_OpensSelectGameScreen() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        pressBack();
        backToSelectGameCommand();
    }

    @NonNull
    protected final Matcher<View> placeInstructions() {
        return withText(R.string.place_instruction);
    }

    @NonNull
    protected final Matcher<View> rotateInstructions() {
        return withText(R.string.rotate_instruction);
    }

    @NonNull
    protected final Matcher<View> mustSetShipsMessage() {
        return withText(R.string.ships_setup_validation);
    }

    protected final Matcher<View> autoSetup() {
        return withId(R.id.auto_setup);
    }

    protected final Matcher<View> done() {
        return withId(R.id.done);
    }

    protected final Matcher<View> boardView() {
        return withId(R.id.board_view);
    }

    protected final Matcher<View> help() {
        return withId(R.id.help_button);
    }

    protected final Matcher<View> gotIt() {
        return withId(R.id.got_it_button);
    }

}
