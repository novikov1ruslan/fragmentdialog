package com.ivygames.morskoiboi.boardsetup;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Ship;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ivygames.morskoiboi.ScreenUtils.BOARD_SETUP_LAYOUT;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static com.ivygames.morskoiboi.ScreenUtils.done;
import static org.mockito.Mockito.when;

public class BoardSetupScreen_AllHorizontalDialogTest extends BoardSetupScreen_ {
    @Test
    public void WhenBoardIsSet_AndAllShipsAreHorizontal_AndDonePressed__DialogDisplayed() {
        showScreen();
        mOrientationBuilder.setOrientation(Ship.Orientation.HORIZONTAL);
        isBoardSet(true);

        clickOn(done());

        checkDisplayed(onlyHorizontalDialog());
    }

    private void isBoardSet(boolean b) {
        when(rules.getAllShipsSizes()).thenReturn(new int[0]);
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
    private Matcher<View> onlyHorizontalDialog() {
        String message = getString(R.string.only_horizontal_ships) + " " +
                getString(R.string.rotate_instruction);
        return withText(message);
    }

    private Matcher<View> rearrange() {
        return positiveButton();
    }

    private Matcher<View> _continue() {
        return negativeButton();
    }
}
