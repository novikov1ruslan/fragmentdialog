package com.ivygames.morskoiboi.gameplay;

import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.screen.gameplay.ChatDialogLayout;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GameplayScreen_ChatDialogTest extends GameplayScreen_ {

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
        closeSoftKeyboard();

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
    public void WhenSendPressed_AndNoTextEntered__NoMessageSentToOpponent_DialogDismissed() {
        WhenChatButtonClicked__ChatDialogDisplayed();
        clickOn(send());
        checkDoesNotExist(chatDialog());
        verify(opponent, never()).onNewMessage(anyString());
    }

    @Test
    public void AfterTextEntered_SendPressed__MessageSentToOpponent_DialogDismissed() {
        WhenChatButtonClicked__ChatDialogDisplayed();
        onView(ViewMatchers.withId(R.id.message_text)).perform(typeText("test message"));
        clickOn(send());
        checkDoesNotExist(chatDialog());
        verify(opponent, times(1)).onNewMessage("test message");
    }

    private Matcher<View> chatDialog() {
        return instanceOf(ChatDialogLayout.class);
    }

    private Matcher<View> send() {
        return withText(R.string.send);
    }
}
