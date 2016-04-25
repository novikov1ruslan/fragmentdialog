package com.ivygames.morskoiboi;

import android.view.View;

import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.screen.gameplay.ChatDialogLayout;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.Mockito;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class GameplayScreen_ChatDialogTest extends GameplayScreenTest {

    @Test
    public void WhenChatButtonClicked__ChatDialogDisplayed() {
        setGameType(Game.Type.INTERNET);
        showScreen();
        clickOn(chat());
        checkDisplayed(chatDialog());
    }

    @Test
    public void WhenBackPressed__DialogDismissed() {
        WhenChatButtonClicked__ChatDialogDisplayed();
        pressBack();
        checkDoesNotExist(chatDialog());
    }

    @Test
    public void WhenCancelPressed__DialogDismissed() {
        WhenChatButtonClicked__ChatDialogDisplayed();
        clickOn(cancelButton());
        checkDoesNotExist(chatDialog());
    }

    @Test
    public void WhenSendPressed_AndNoTextEntered__DialogDismissed() {
        WhenChatButtonClicked__ChatDialogDisplayed();
        clickOn(send());
        checkDoesNotExist(chatDialog());
        verify(opponent, never()).onNewMessage(anyString());
    }

    @Test
    public void AfterTextEnteredSendPressed__MessageSentToOpponent() {
        WhenChatButtonClicked__ChatDialogDisplayed();
        String message = "test message";
        onView(withId(R.id.message_text)).perform(typeText(message));
        clickOn(send());
        checkDoesNotExist(chatDialog());
        verify(opponent, Mockito.times(1)).onNewMessage(message);
    }

    private Matcher<View> chatDialog() {
        return instanceOf(ChatDialogLayout.class);
    }

    private Matcher<View> send() {
        return withText(R.string.send);
    }
}
