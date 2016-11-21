package com.ivygames.morskoiboi.boardsetup;

import com.ivygames.morskoiboi.OnlineScreen_;
import com.ivygames.battleship.ship.Ship.Orientation;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static com.ivygames.morskoiboi.ScreenUtils.autoSetup;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static com.ivygames.morskoiboi.ScreenUtils.done;


public class BoardSetupScreenTest extends BoardSetupScreen_ {

    public void WhenAutoPressed__BoardIsSet() {
        showScreen();
        clickOn(autoSetup());
        // TODO:
    }

    @Test
    public void WhenBoardIsSet_PressingDone__OpensGameplayScreen() {
        showScreen();
        mOrientationBuilder.setOrientation(Orientation.VERTICAL);
        clickOn(autoSetup());
        setBoardSet();
        clickOn(done());
        checkDisplayed(GAMEPLAY_LAYOUT);
    }

    private void setBoardSet() {
//        when(rules.getAllShipsSizes()).thenReturn(new int[0]);
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
        setGameType(OnlineScreen_.Type.VS_ANDROID);
        showScreen();
        pressBack();
        backToSelectGame();
    }
}
