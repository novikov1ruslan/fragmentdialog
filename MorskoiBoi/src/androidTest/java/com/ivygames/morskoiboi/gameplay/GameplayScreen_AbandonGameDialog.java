package com.ivygames.morskoiboi.gameplay;

import android.view.View;

import com.ivygames.morskoiboi.OnlineScreen_;
import com.ivygames.morskoiboi.R;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;

public class GameplayScreen_AbandonGameDialog extends GameplayScreen_ {

    @Test
    public void WhenBackPressedForAndroidGame__DialogDisplayed() {
        setGameType(OnlineScreen_.Type.VS_ANDROID);
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
        backToSelectGame();
    }

    private Matcher<View> abandonGameDialog() {
        return withText(R.string.abandon_game_question);
    }
}
