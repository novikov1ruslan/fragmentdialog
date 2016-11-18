package com.ivygames.morskoiboi.boardsetup;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Ship;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Random;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ivygames.morskoiboi.ScreenUtils.BOARD_SETUP_LAYOUT;
import static com.ivygames.morskoiboi.ScreenUtils.autoSetup;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static com.ivygames.morskoiboi.ScreenUtils.done;
import static org.mockito.Mockito.*;

public class BoardSetupScreen_AllHorizontalDialogTest extends BoardSetupScreen_ {

    @Before
    public void foo() {
        Random random = mock(Random.class);
        when(random.nextInt()).thenReturn(1);
        when(random.nextInt(Matchers.anyInt())).thenReturn(1);
        Dependencies.inject(random);
    }

    @Test
    public void WhenBoardIsSet_AndAllShipsAreHorizontal_AndDonePressed__DialogDisplayed() {
        showScreen();
        mOrientationBuilder.setOrientation(Ship.Orientation.HORIZONTAL);

        clickOn(autoSetup());
        clickOn(done());

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
