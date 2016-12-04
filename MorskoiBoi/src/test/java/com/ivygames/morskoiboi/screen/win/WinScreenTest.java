package com.ivygames.morskoiboi.screen.win;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.Game;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.ScoreStatistics;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.ai.AndroidGame;
import com.ivygames.morskoiboi.config.ScoresUtilsTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WinScreenTest {

    private WinScreen screen;
    private BattleshipActivity activity;
    private AndroidGame game;
    private PlayerOpponent player;
    private Opponent opponent;
    private Session session;
    private Collection fleet;
    private ScoreStatistics statistics;

    @Mock
    private GameSettings settings;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        activity = mock(BattleshipActivity.class);
        game = new AndroidGame();
        player = mock(PlayerOpponent.class);
        opponent = mock(Opponent.class);
        session = new Session(player, opponent);
        fleet = mock(Collection.class);
        statistics = mock(ScoreStatistics.class);

        Dependencies.inject(settings);
    }

    @Test
    public void WhenScreenDisplayedWithPositiveScoreBalance__ProgressUpdated() {
        // TODO: this is component test
//        setScores(100);
//        setPenalty(0);
//        showScreen();
        Collection<Ship> ships = ScoresUtilsTest.ships_1_4();
        ScoreStatistics statistics = ScoresUtilsTest.game_2xCombo_30xShells_150seconds();
        screen = new WinScreen(activity, game, session, ships, statistics, false);

//        screen.onCreateView();

        verify(settings, times(1)).incrementProgress(100);
        verify(settings, times(1)).setProgressPenalty(0);
    }

}