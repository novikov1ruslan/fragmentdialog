package com.ivygames.morskoiboi.gameplay;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.common.multiplayer.MultiplayerEvent;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.player.AiOpponent;
import com.ivygames.morskoiboi.player.PlayerOpponent;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameplayScreenTest extends GameplayScreen_ {

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
    public void WhenScreenDestroyed__TimerStops() {
        showScreen();
        destroy();
        verify(timeController, times(1)).stop();
    }

    @Test
    public void WhenScreenDestroyed_ForAndroidGame__AndroidOpponentIsCancelled() {
        PlayerOpponent player = mockPlayer();
        AiOpponent android = mock(AiOpponent.class);
        when(android.getName()).thenReturn(OPPONENT_NAME);
        session = new Session(player, android);
        setGameType(Game.Type.VS_ANDROID);
        showScreen();

        destroy();

        verify(android, times(1)).cancel();
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
    protected Matcher<View> opponentSettingBoardNotification() {
        return withText(getString(R.string.opponent_setting_board, OPPONENT_NAME));
    }

}
