package com.ivygames.morskoiboi.scenario;

import com.ivygames.common.timer.TurnTimerController;
import com.ivygames.morskoiboi.OnlineScreen_;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.BoardSerialization;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.gameplay.GameplayScreen;

import org.junit.Before;
import org.mockito.Mockito;

public class ScenarioTest extends OnlineScreen_ {
    private static final String BOARD_WITH_SHIP = "{\"ships\":[{\"size\":1,\"is_horizontal\":true,\"x\":5,\"y\":5,\"health\":1}],\"cells\":\"                                            000       000       000                                 \"}";

    private TurnTimerController timerController;

    @Override
    public BattleshipScreen newScreen() {
        return new GameplayScreen(activity, game, session, timerController);
    }

    @Before
    public void setup() {
        super.setup();
        timerController = Mockito.mock(TurnTimerController.class);
    }

//    @Test
    public void WhenPlayerLooses__EnemyBoardShown() {
        // TODO:
        showScreen();
        Board enemyBoard = BoardSerialization.fromJson(BOARD_WITH_SHIP);
        player.onLost(enemyBoard);
    }

}
