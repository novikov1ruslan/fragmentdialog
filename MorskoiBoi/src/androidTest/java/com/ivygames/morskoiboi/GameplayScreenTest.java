package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.model.Game;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameplayScreenTest extends GameplayScreen_ {

    @Test
    public void WhenBackPressed__DialogDisplayed() {
//        showScreen();
//        pressBack();
//        checkDisplayed(MAIN_LAYOUT);
    }

    @Test
    public void WhenScreenPausedForAndroidGame__TimerPaused() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        pause();
        verify(timeController, times(1)).pause();
    }

    @Test
    public void ForAndroidGame__ChatButtonHidden() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        checkNotDisplayed(chat());
    }

    @Test
    public void ForBluetoothGame__ChatButtonHidden() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        checkNotDisplayed(chat());
    }

    @Test
    public void ForInternetGame__ChatButtonVisible() {
        setGameType(Game.Type.INTERNET);
        showScreen();
        checkDisplayed(chat());
    }

    @Test
    public void IfEnemyReady__OpponentSettingBoardNotificationNotShown() {
        opponentReady(true);
        showScreen();
        checkDoesNotExist(opponentSettingBoardNotification());
    }

    @Test
    public void IfEnemyNotReady__OpponentSettingBoardNotificationShown() {
        opponentReady(false);
        showScreen();
        checkDisplayed(opponentSettingBoardNotification());
    }

    @Test
    public void WhenScreenDestroyed__TimerStopped() {
        showScreen();
        destroy();
        verify(timeController, times(1)).stop();
    }

    @Test
    public void WhenScreenDestroyed_ForAndroidGame__AndroidOpponentIsCancelled() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        destroy();
        verify((Cancellable) opponent, times(1)).cancel();
    }

    protected void opponentReady(boolean ready) {
        when(player.isOpponentReady()).thenReturn(ready);
    }

    @NonNull
    protected Matcher<View> opponentSettingBoardNotification() {
        return withText(getString(R.string.opponent_setting_board, OPPONENT_NAME));
    }

    @NonNull
    protected final Matcher<View> chat() {
        return withId(R.id.chat_button);
    }

}
