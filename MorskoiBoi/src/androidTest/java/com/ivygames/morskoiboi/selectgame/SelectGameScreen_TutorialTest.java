package com.ivygames.morskoiboi.selectgame;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ScreenTest;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectGameScreen_TutorialTest extends SelectGameScreen_ {

    @Test
    public void FirstTime__TutorialShown() {
        setShowTutorial(true);
        showScreen();
        checkDisplayed(tutorial());
    }

    @Test
    public void SecondTime__TutorialNotShown() {
        setShowTutorial(false);
        showScreen();
        checkDoesNotExist(tutorial());
    }

    @Test
    public void PressingHelp__ShowsTutorial() {
        showScreen();
        clickOn(help());
        checkDisplayed(tutorial());
    }

    @Test
    public void WhenTutorialShown_PressingBack__DismissesTutorial() {
        showScreen();
        clickOn(help());
        pressBack();
        checkDoesNotExist(tutorial());
        checkDisplayed(ScreenTest.SELECT_GAME_LAYOUT);
    }

    @Test
    public void WhenTutorialShown_PressingGotIt__DismissesTutorial() {
        showScreen();
        clickOn(help());
        clickOn(gotIt());
        checkDoesNotExist(tutorial());
        checkDisplayed(ScreenTest.SELECT_GAME_LAYOUT);
    }

    @Test
    public void WhenScreenIsPaused__TutorialDismissed() {
        showScreen();
        clickOn(help());
        pause();
        checkDoesNotExist(tutorial());
    }

    @Test
    public void IfTutorialDismissed__ItIsNotShownAgain() {
        setScreen(newScreen());
        clickOn(help());
        clickOn(gotIt());
        verify(settings(), times(1)).hideProgressHelp();
    }

    @NonNull
    protected Matcher<View> help() {
        return ViewMatchers.withId(R.id.help_button);
    }

    @NonNull
    protected Matcher<View> tutorial() {
        return withText(R.string.see_ranks);
    }

    protected void setShowTutorial(boolean show) {
        when(settings().showProgressHelp()).thenReturn(show);
    }

    @NonNull
    protected Matcher<View> gotIt() {
        return withId(R.id.got_it_button);
    }
}
