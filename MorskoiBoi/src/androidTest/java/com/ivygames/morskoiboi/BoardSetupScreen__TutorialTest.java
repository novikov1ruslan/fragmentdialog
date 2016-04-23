package com.ivygames.morskoiboi;

import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BoardSetupScreen__TutorialTest extends BoardSetupScreenTest {

    @Test
    public void WhenScreenIsShownFirstTime__TutorialIsShown() {
        setShowTutorial(true);
        showScreen();
        checkTutorialShown();
    }

    @Test
    public void WhenScreenIsShownSecondTime__TutorialIsNotShown() {
        setShowTutorial(false);
        showScreen();
        checkTutorialNotShown();
    }

    @Test
    public void WhenHelpPressed__TutorialShown() {
        showScreen();
        clickOn(help());
        checkTutorialShown();
    }

    @Test
    public void WhenTutorialShown_PressingBack__RemovesTutorial() {
        showScreen();
        clickOn(help());
        pressBack();
        checkTutorialNotShown();
        checkDisplayed(BOARD_SETUP_LAYOUT);
    }

    @Test
    public void WhenGotItPressed__TutorialDismissed() {
        showScreen();
        clickOn(help());
        clickOn(gotIt());
        checkTutorialDismissed();
    }

    @Test
    public void WhenScreenIsPaused__TutorialDismissed() {
        showScreen();
        clickOn(help());
        pause();
        checkTutorialDismissed();
    }

    @Test
    public void IfTutorialDismissed__ItIsNotShownAgain() {
        showScreen();
        clickOn(help());
        clickOn(gotIt());
        verify(settings(), times(1)).hideBoardSetupHelp();
    }

    protected void checkTutorialDismissed() {
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

    protected void setShowTutorial(boolean show) {
        when(settings().showSetupHelp()).thenReturn(show);
    }
}
