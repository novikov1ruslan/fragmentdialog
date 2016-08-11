package com.ivygames.morskoiboi.boardsetup;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Board;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class BoardSetupScreen_AllHorizontalDialogTest extends BoardSetupScreen_ {
    @Test
    public void WhenBoardIsSet_AndAllShipsAreHorizontal_AndDonePressed__DialogDisplayed() {
        showScreen();
        when(rules.isBoardSet(any(Board.class))).thenReturn(true);

        done();

        checkDisplayed(onlyHorizontalDialog());
    }

    @Test
    public void PressingBack__DialogDismissed() {
        WhenBoardIsSet_AndAllShipsAreHorizontal_AndDonePressed__DialogDisplayed();

        pressBack();

        checkDoesNotExist(onlyHorizontalDialog());
        checkDisplayed(BOARD_SETUP_LAYOUT);
    }

    @Test
    public void PressingRearrange__DialogDismissed() {
        WhenBoardIsSet_AndAllShipsAreHorizontal_AndDonePressed__DialogDisplayed();

        clickOn(rearrange());

        checkDoesNotExist(onlyHorizontalDialog());
        checkDisplayed(BOARD_SETUP_LAYOUT);
    }

    @Test
    public void PressingContinue__GameplayScreenDisplayed() {
        WhenBoardIsSet_AndAllShipsAreHorizontal_AndDonePressed__DialogDisplayed();

        clickOn(_continue());

        checkDisplayed(GAMEPLAY_LAYOUT);
    }

    @NonNull
    protected Matcher<View> onlyHorizontalDialog() {
        return withText(getString(R.string.only_horizontal_ships));
    }

    private Matcher<View> rearrange() {
        return positiveButton();
    }

    private Matcher<View> _continue() {
        return negativeButton();
    }
}
