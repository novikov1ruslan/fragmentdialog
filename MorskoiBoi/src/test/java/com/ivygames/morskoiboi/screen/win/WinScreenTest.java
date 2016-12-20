package com.ivygames.morskoiboi.screen.win;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.common.multiplayer.RealTimeMultiplayer;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.Game;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.ScoreStatistics;
import com.ivygames.morskoiboi.ScoresCalculator;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.ai.AndroidGame;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame;
import com.ivygames.morskoiboi.progress.ProgressManager;

import com.ivygames.morskoiboi.bluetooth.BluetoothConnection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;
import org.robolectric.RobolectricTestRunner;

import java.util.Collection;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class WinScreenTest {

    private BattleshipActivity activity;
    private Session session;
    private Collection fleet;
    private ScoreStatistics statistics;

    @Mock
    private RealTimeMultiplayer mMultiplayer;
    @Mock
    private AchievementsManager mAchievementsManager;
    @Mock
    private ProgressManager mProgressManager;

    @Mock
    private GameSettings mSettings;
    @Mock
    private ScoresCalculator scoresCalculator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        activity = mock(BattleshipActivity.class);
        session = new Session(mock(PlayerOpponent.class), mock(Opponent.class));
        fleet = mock(Collection.class);
        statistics = mock(ScoreStatistics.class);

        Dependencies.inject(mSettings);
        Dependencies.inject(scoresCalculator);

        Dependencies.inject(mMultiplayer);
        Dependencies.inject(mAchievementsManager);
        Dependencies.inject(mProgressManager);
    }

    @Test
    public void WhenScreenDisplayedWithPositiveScoreBalance__ProgressUpdated() {
        setScores(100);
        setPenalty(0);

        showScreen(new AndroidGame());

        verify(mSettings, times(1)).incrementProgress(100);
        verify(mSettings, times(1)).setProgressPenalty(0);
    }

    @Test
    public void IfScoreBalancePositive_AndConnected__ProgressSynchronized() {
        when(mProgressManager.isConnected()).thenReturn(true);
        setScores(100);
        setPenalty(0);

        showScreen(new AndroidGame());

        expectSynchronizeBeCalled(times(1));
    }

    private void showScreen(Game game) {
        WinScreen screen = new WinScreen(activity, game, session, fleet, statistics, false);
    }

    @Test
    public void IfNotConnected__ProgressNotSynchronized() {
        when(mProgressManager.isConnected()).thenReturn(false);
        setScores(100);
        setPenalty(0);

        showScreen(new AndroidGame());

        expectSynchronizeBeCalled(never());
    }

    @Test
    public void WhenScreenDisplayedWithNegativeScoreBalance__PenaltyUpdated() {
        setScores(100);
        setPenalty(200);

        showScreen(new AndroidGame());

        expectSynchronizeBeCalled(never());
        verify(mSettings, never()).incrementProgress(anyInt());
        verify(mSettings, times(1)).setProgressPenalty(100);
    }

    @Test
    public void WhenScreenDisplayedForAndroidGame__AchievementsProcessed() {
        showScreen(new AndroidGame());

        expectProcessAchievementsBeCalled(times(1));
    }

    @Test
    public void WhenScreenDisplayedForNonAndroidGame__AchievementsNotProcessed() {
        BluetoothConnection connection = mock(BluetoothConnection.class);
        showScreen(new BluetoothGame(connection));

        expectProcessAchievementsBeCalled(never());
    }

    private void expectProcessAchievementsBeCalled(VerificationMode times) {
        verify(mAchievementsManager, times).processScores(anyInt());
    }

    private void expectSynchronizeBeCalled(VerificationMode times) {
        verify(mProgressManager, times).synchronize();
    }

    private void setScores(int scores) {
        when(scoresCalculator.calcScoresForAndroidGame(anyCollection(), any(ScoreStatistics.class))).thenReturn(scores);
    }

    private void setPenalty(int penalty) {
        when(mSettings.getProgressPenalty()).thenReturn(penalty);
    }

}