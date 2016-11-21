package com.ivygames.morskoiboi.lost;

import com.ivygames.morskoiboi.OnlineScreen_;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static com.ivygames.morskoiboi.ScreenUtils.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class LostScreenTest extends LostScreen_ {

    @Test
    public void WhenScreenDisplayed__GamesCounterIncremented() {
        showScreen();
        verify(settings(), times(1)).incrementGamesPlayedCounter();
    }

    @Test
    public void WhenBackButtonPressedForAndroid__SelectGameScreenShown() {
        setGameType(OnlineScreen_.Type.VS_ANDROID);
        showScreen();
        pressBack();
        backToSelectGame();
    }

    @Test
    public void AfterNoPressedForAndroid__SelectGameScreenShown() {
        setGameType(OnlineScreen_.Type.VS_ANDROID);
        showScreen();
        clickOn(noButton());
        backToSelectGame();
    }

    @Test
    public void AfterYesPressed__BoardSetupScreenShown() {
        when(rules.getAllShipsSizes()).thenReturn(new int[]{});
        showScreen();
        clickOn(yesButton());
        checkDisplayed(BOARD_SETUP_LAYOUT);
    }

}
