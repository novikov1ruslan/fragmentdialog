package com.ivygames.morskoiboi.gameplay;

import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Game;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class GameplayScreen_AbandonGameDialog extends GameplayScreen_ {

    @Test
    public void WhenBackPressedForAndroidGame__DialogDisplayed() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        pressBack();
        checkDisplayed(abandonGameDialog());
    }

    @Test
    public void WhenBackPressed__DialogDismissed_ScreenRemains() {
        WhenBackPressedForAndroidGame__DialogDisplayed();
        pressBack();
        checkDoesNotExist(abandonGameDialog());
        checkDisplayed(GAMEPLAY_LAYOUT);
    }

    @Test
    public void WhenNoPressed__DialogDismissed_ScreenRemains() {
        WhenBackPressedForAndroidGame__DialogDisplayed();
        clickOn(cancelButton());
        checkDoesNotExist(abandonGameDialog());
        checkDisplayed(GAMEPLAY_LAYOUT);
    }

    @Test
    public void WhenYesPressed__DialogDismissed_GameFinished_SelectGameDisplayed() {
        WhenBackPressedForAndroidGame__DialogDisplayed();
        clickOn(okButton());
        checkDoesNotExist(abandonGameDialog());
        FinishGame_BackToSelectGame();
    }

    private Matcher<View> abandonGameDialog() {
        return withText(R.string.abandon_game_question);
    }
}
