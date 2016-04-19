package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


public class BoardSetupScreenTest extends OnlineScreenTest {

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new BoardSetupScreen(activity());
    }

    @Test
    public void WhenPressedBackForAndroidGame__GameFinishesOpensSelectGameScreen() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        pressBack();
        backToSelectGameCommand();
    }

    @Test
    public void WhenBackPressedForNonAndroidGame__WantToLeaveDialogDisplayed() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        pressBack();
        String message = getString(R.string.want_to_leave_room, OPPONENT_NAME);
        checkDisplayed(withText(message));
    }

    @Test
    public void PressingCancelOnWantToLeaveDialog__RemovesDialogScreenDoesNotChange() {
        WhenBackPressedForNonAndroidGame__WantToLeaveDialogDisplayed();
        clickOn(cancelButton());
        checkDisplayed(BOARD_SETUP_LAYOUT);
        checkDoesNotExist(cancelButton());
    }

    @Test
    public void PressingOkOnWantToLeaveDialog__SelectGameScreenDisplayed() {
        WhenBackPressedForNonAndroidGame__WantToLeaveDialogDisplayed();
        clickOn(okButton());
        backToSelectGameCommand();
    }

}
