package com.ivygames.morskoiboi;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BoardSetupScreen__TutorialTest extends BoardSetupScreenTest {

    @Test
    public void WhenScreenIsShownFirstTime__TutorialIsShown() {
        when(settings().showSetupHelp()).thenReturn(true);
        showScreen();
        checkTutorialShown();
    }

    @Test
    public void WhenScreenIsShownSecondTime__TutorialIsNotShown() {
        when(settings().showSetupHelp()).thenReturn(false);
        showScreen();
        checkTutorialNotShown();
    }

    @Test
    public void WhenTutorialShown_PressingBack__RemovesTutorial() {
        when(settings().showSetupHelp()).thenReturn(true);
        showScreen();
        pressBack();
        checkTutorialNotShown();
        checkDisplayed(BOARD_SETUP_LAYOUT);
    }

    @Test
    public void WhenGotItPressed__TutorialDismissed() {
        when(settings().showSetupHelp()).thenReturn(true);
        showScreen();
        clickOn(gotIt());
        checkTutorialDismissed();
    }

    @Test
    public void WhenScreenIsPaused__TutorialDismissed() {
//        when(settings().showSetupHelp()).thenReturn(true);
//        showScreen();
//        screen().onPause();
//        checkTutorialDismissed();
        // TODO:
    }

    @Test
    public void WhenHelpPressed__TutorialShown() {
        showScreen();
        clickOn(help());
        checkTutorialShown();
    }

    protected void checkTutorialDismissed() {
        verify(settings(), times(1)).hideBoardSetupHelp();
        checkDoesNotExist(placeInstructions());
        checkDoesNotExist(rotateInstructions());
    }

    protected void checkTutorialShown() {
        checkDisplayed(placeInstructions());
        checkDisplayed(rotateInstructions());
    }

    protected void checkTutorialNotShown() {
        checkDoesNotExist(placeInstructions());
        checkDoesNotExist(rotateInstructions());
    }
}
