package com.ivygames.morskoiboi;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;

public class InternetGameScreenTest extends InternetGameScreen_ {

    @Test
    public void WhenBackPressed__SelectGameScreenOpens() {
        showScreen();
        pressBack();
        checkDisplayed(SELECT_GAME_LAYOUT);
    }
}
