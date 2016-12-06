package com.ivygames.morskoiboi.gameplay;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.common.multiplayer.MultiplayerEvent;
import com.ivygames.morskoiboi.OnlineScreen_;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameplayScreenTest extends GameplayScreen_ {

    @Test
    public void WhenScreenPausedForAndroidGame__TimerPaused() {
        setGameType(OnlineScreen_.Type.VS_ANDROID);
        showScreen();
        pause();
        verify(timeController, times(1)).pause();
    }

    @Test
    public void ForAndroidGame__ChatButtonHidden() {
        setGameType(OnlineScreen_.Type.VS_ANDROID);
        showScreen();
        checkNotDisplayed(chat());
    }

    @Test
    public void ForBluetoothGame__ChatButtonHidden() {
        setGameType(OnlineScreen_.Type.BLUETOOTH);
        showScreen();
        checkNotDisplayed(chat());
    }

    @Test
    public void ForInternetGame__ChatButtonVisible() {
        setGameType(OnlineScreen_.Type.INTERNET);
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
    public void WhenScreenDestroyed__TimerStops() {
        showScreen();
        destroy();
        verify(timeController, times(1)).stop();
    }

    @Test
    public void WhenOpponentLeft__TimerStops() {
        showScreen();
        ((GameplayScreen)screen()).onConnectionLost(MultiplayerEvent.OPPONENT_LEFT);
        verify(timeController, times(1)).stop();
        // TODO: test service stops
    }

    @Test
    public void WhenConnectionLost__TimerStops() {
        showScreen();
        ((GameplayScreen)screen()).onConnectionLost(MultiplayerEvent.CONNECTION_LOST);
        verify(timeController, times(1)).stop();
        // TODO: test service stops
    }

    private void opponentReady(boolean ready) {
        when(player.isOpponentReady()).thenReturn(ready);
    }

    @NonNull
    private Matcher<View> opponentSettingBoardNotification() {
        return withText(getString(R.string.opponent_setting_board, OPPONENT_NAME));
    }

}
