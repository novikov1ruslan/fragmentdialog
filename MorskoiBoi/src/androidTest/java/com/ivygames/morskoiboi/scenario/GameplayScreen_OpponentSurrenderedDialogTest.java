package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.GameplayScreen_;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.GameEvent;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

public class GameplayScreen_OpponentSurrenderedDialogTest extends GameplayScreen_ {

    @Test
    public void WhenOpponentLeftAndOpponentReady__OpponentSurrenderedDialogDisplayed() {
        showScreen();
        when(player.isOpponentReady()).thenReturn(true);
        ((GameplayScreen)screen()).onEventMainThread(GameEvent.OPPONENT_LEFT);
        checkDisplayed(opponentSurrenderedDialog());
    }

    @Test
    public void WhenOkPressedOnOpponentSurrenderedDialog__WinScreenDisplayedInOpponentSurrenderedMode() {
        WhenOpponentLeftAndOpponentReady__OpponentSurrenderedDialogDisplayed();
        clickOn(okButton());
        checkWinScreenDisplayedInOpponentSurrenderedMode();
    }

    @Test
    public void WhenBackPressedOnOpponentSurrenderedDialog__WinScreenDisplayedInOpponentSurrenderedMode() {
        WhenOpponentLeftAndOpponentReady__OpponentSurrenderedDialogDisplayed();
        pressBack();
        checkWinScreenDisplayedInOpponentSurrenderedMode();
    }

    private void checkWinScreenDisplayedInOpponentSurrenderedMode() {
        checkDisplayed(WIN_LAYOUT);
        checkNotDisplayed(yesButton());
        checkNotDisplayed(noButton());
        checkDisplayed(continueButton());
    }

    @NonNull
    protected Matcher<View> opponentSurrenderedDialog() {
        return withText(R.string.opponent_surrendered);
    }
}
