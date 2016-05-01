package com.ivygames.morskoiboi;

import android.view.View;

import com.ivygames.morskoiboi.idlingresources.TaskResource;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.screen.gameplay.GameplayLayoutInterface;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Test;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GameplayScreen_PauseDialogTest extends GameplayScreenTest {

    private TaskResource task;

    @After
    public void teardown() {
        super.teardown();
        if (task != null) {
            unregisterIdlingResources(task);
        }
    }

    @Test
    public void WhenBoardIsLocked_ForAndroidGame_AfterResume__PauseDialogNotDisplayed() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        pause();
        resume();
        checkDoesNotExist(pauseDialog());
    }

    @Test
    public void WhenBoardIsNotLocked_ForBluetoothGame_AfterResume__PauseDialogNotDisplayed() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        unlockBoard();
        pause();
        resume();
        checkDoesNotExist(pauseDialog());
    }

    @Test
    public void WhenBoardIsNotLocked_ForInternetGame_AfterResume__PauseDialogNotDisplayed() {
        setGameType(Game.Type.INTERNET);
        showScreen();
        unlockBoard();
        pause();
        resume();
        checkDoesNotExist(pauseDialog());
    }

    @Test
    public void WhenBoardIsNotLocked_ForAndroidGame_AfterResume__PauseDialogDisplayed() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        unlockBoard();
        pause();
        resume();
        checkDisplayed(pauseDialog());
    }

    @Test
    public void PressingContinueOnPauseDialog__ResumesTimer__DismissesDialog() {
        WhenBoardIsNotLocked_ForAndroidGame_AfterResume__PauseDialogDisplayed();
        clickOn(continueButton());
        checkDoesNotExist(pauseDialog());
        verify(timeController, times(1)).start();
    }

    @Test
    public void PressingBackOnPauseDialog__ResumesTimer__DismissesDialog() {
        WhenBoardIsNotLocked_ForAndroidGame_AfterResume__PauseDialogDisplayed();
        pressBack();
        checkDoesNotExist(pauseDialog());
        verify(timeController, times(1)).start();
    }

//    @Test
//    public void WhenPauseDialogIsDisplayed__BoardIsLocked() {
//
//    }

    private void unlockBoard() {
        task = new TaskResource(new Runnable() {
            @Override
            public void run() {
                GameplayLayoutInterface layout = (GameplayLayoutInterface) screen().getView();
                layout.unLock();
            }
        });
        registerIdlingResources(task);
    }

    private Matcher<View> pauseDialog() {
        return withText(R.string.pause);
    }

    private Matcher<View> continueButton() {
        return withText(R.string.continue_str);
    }
}
