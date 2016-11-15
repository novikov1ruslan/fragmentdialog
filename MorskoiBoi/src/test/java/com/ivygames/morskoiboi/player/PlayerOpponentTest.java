package com.ivygames.morskoiboi.player;

import android.support.annotation.NonNull;

import com.ivygames.common.analytics.ExceptionHandler;
import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerCallback;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.ShotResult;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupUtils;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class PlayerOpponentTest {

    private static final String PLAYER_NAME = "Sagi";
    private PlayerOpponent mPlayer;
    @Mock
    private Opponent mEnemy;
    private Placement mPlacement;
    @Mock
    private PlayerCallback callback;

    private Rules russianRules = new RussianRules();
    private ChatListener listener = new ChatListener() {
        @Override
        public void showChatCrouton(ChatMessage message) {

        }
    };

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ExceptionHandler.setDryRun(true);

        mPlacement = new Placement(new Random(), russianRules);
        mPlayer = newPlayer(russianRules);
    }

    @NonNull
    private PlayerOpponent newPlayer(Rules rules) {
        Placement placement = new Placement(new Random(), new RussianRules());
        PlayerOpponent player = new PlayerOpponent(PLAYER_NAME, placement, rules);
        player.setChatListener(listener);
        player.setOpponent(mEnemy);
        player.registerCallback(callback);
        return player;
    }

    @Test
    public void Initially__EnemyIsNotReady() {
        assertThat(mPlayer.isOpponentReady(), is(false));
    }

    @Test
    public void if_player_can_go__enemy_is_ready() {
        mPlayer.go();

        assertThat(mPlayer.isOpponentReady(), is(true));
    }

    @Test
    public void when_enemy_bids_on_non_ready_player__enemy_does_not_go() {
        mPlayer.onEnemyBid(2);

        verify(mEnemy, never()).go();
    }

    @Test
    public void after_shot_result_is_miss__enemy_board_shows_miss() {
        Vector2 aim = Vector2.get(5, 5);
        Cell cell = Cell.MISS;
        ShotResult result = new ShotResult(aim, cell);
        mPlayer.onShotResult(result);
        assertThat(enemyCellAt(aim), equalTo(cell));
    }

    @Test
    public void after_shot_result_is_hit__enemy_board_shows_hit() {
        Vector2 aim = Vector2.get(5, 5);
        Cell cell = Cell.HIT;
        ShotResult result = new ShotResult(aim, cell);
        mPlayer.onShotResult(result);
        assertThat(enemyCellAt(aim), equalTo(cell));
    }

    @Test
    public void after_shot_result_is_kill__enemy_board_shows_killed_ship() {
        Vector2 aim = Vector2.get(5, 5);
        Ship ship = new Ship(2);
        ship.setCoordinates(5, 5);
        ShotResult result = new ShotResult(aim, Cell.HIT, ship);

        mPlayer.onShotResult(result);

        Ship actual = mPlayer.getEnemyBoard().getFirstShipAt(aim);
        assertThat(actual, equalTo(ship));
        assertThat(enemyCellAt(aim), equalTo(Cell.HIT));
    }

    @Test
    public void after_shooting_on_my_empty_cell__result_is_miss() {
        Board board = new Board();
        mPlayer.setBoard(board);
        mPlayer.onShotAt(Vector2.get(5, 5));

        assertThat(board.getCell(5, 5) == Cell.MISS, is(true));
    }

    @Test
    public void after_shooting_on_my_ship__result_is_hit() {
        Vector2 aim = Vector2.get(5, 5);
        Board board = new Board();
        mPlayer.setBoard(board);
        mPlacement.putShipAt(board, new Ship(2), aim);

        mPlayer.onShotAt(aim);

        assertThat(board.getCell(aim) == Cell.HIT, is(true));
    }

    @Test
    public void after_killing_on_my_ship__result_is_kill() {
        Vector2 aim = Vector2.get(5, 5);
        Board board = new Board();
        mPlayer.setBoard(board);
        mPlacement.putShipAt(board, new Ship(1), aim);

        mPlayer.onShotAt(aim);

        assertThat(board.getCell(4, 4) == Cell.MISS, is(true));
        assertThat(board.getCell(4, 5) == Cell.MISS, is(true));
        assertThat(board.getCell(4, 6) == Cell.MISS, is(true));
        assertThat(board.getCell(5, 4) == Cell.MISS, is(true));
        assertThat(board.getCell(5, 5) == Cell.HIT, is(true));
        assertThat(board.getCell(5, 6) == Cell.MISS, is(true));
        assertThat(board.getCell(6, 4) == Cell.MISS, is(true));
        assertThat(board.getCell(6, 5) == Cell.MISS, is(true));
        assertThat(board.getCell(6, 6) == Cell.MISS, is(true));
    }

    @Test
    public void when_asking_for_name__actual_name_returned() {
        assertThat(mPlayer.getName(), equalTo(PLAYER_NAME));
    }

    @Test
    public void when_asking_for_board__actual_board_returned() {
        Board board = new Board();
        board.setCell(Cell.MISS, Vector2.get(4, 4));
        mPlayer.setBoard(board);
        assertThat(mPlayer.getBoard(), equalTo(board));
    }

    @Test
    public void after_enemy_bids__enemy_is_ready() {
        mPlayer.onEnemyBid(1);
        assertThat(mPlayer.isOpponentReady(), is(true));
    }

    @Test
    public void player_bids_second_and_lower__enemy_goes() {
        mPlayer.onEnemyBid(1);
        mPlayer.startBidding(0);
        verify(mEnemy, times(1)).go();
    }

    @Test
    public void player_bids_second_and_higher__enemy_gets_player_bid() {
        int myBid = 2;
        mPlayer.onEnemyBid(1);
        mPlayer.startBidding(myBid);
        verify(mEnemy, times(1)).onEnemyBid(myBid);
    }

    @Test
    public void enemy_bids_second_and_higher__enemy_goes() {
        mPlayer.startBidding(0);
        mPlayer.onEnemyBid(1);
        verify(mEnemy, times(1)).go();
    }

    @Test
    public void enemy_bids_second_and_lower__enemy_does_not_go() {
        mPlayer.startBidding(2);
        mPlayer.onEnemyBid(1);
        verify(mEnemy, never()).go();
    }

    @Test
    public void WhenPlayerGoes__ItIsPlayersTurn() {
        reset(callback);
        mPlayer.go();

        verify(callback, times(1)).onPlayersTurn();
    }

    @Test
    public void WhenPlayerGetsShotResult__CallbackCalled() {
        ShotResult result = new ShotResult(Vector2.get(1, 1), Cell.HIT);
        mPlayer.onShotResult(result);

        verify(callback, times(1)).onShotResult(result);
    }

    @Test
    public void WhenPlayerKillsShip__CallbackCalled() {
        ShotResult result = new ShotResult(Vector2.get(1, 1), Cell.HIT, new Ship(1));
        mPlayer.onShotResult(result);

        verify(callback, times(1)).onKillEnemy();
    }

    @Test
    public void WhenPlayerHitsShip__CallbackCalled() {
        ShotResult result = new ShotResult(Vector2.get(1, 1), Cell.HIT);
        mPlayer.onShotResult(result);

        verify(callback, times(1)).onHit();
    }

    @Test
    public void WhenPlayerMissesShip__CallbackCalled() {
        ShotResult result = new ShotResult(Vector2.get(1, 1), Cell.MISS);
        mPlayer.onShotResult(result);

        verify(callback, times(1)).onMiss();
        verify(callback, times(1)).onOpponentTurn();
    }

    @Test
    public void WhenPlayerIsShotAt__CallbackCalled() {
        Vector2 aim = Vector2.get(1, 1);
        mPlayer.onShotAt(aim);

        verify(callback, times(1)).onShotAt(aim);
    }

    @Test
    public void WhenPlayerIsHit__CallbackCalled() {
        Board board = new Board();
        mPlacement.putShipAt(board, new Ship(2, Ship.Orientation.VERTICAL), 5, 5);
        mPlayer.setBoard(board);
        Vector2 aim = Vector2.get(5, 5);

        mPlayer.onShotAt(aim);

        verify(callback, times(1)).onHit();
    }

    @Test
    public void WhenPlayerIsMissed__CallbackCalled() {
        Board board = new Board();
        mPlacement.putShipAt(board, new Ship(2, Ship.Orientation.VERTICAL), 5, 5);
        mPlayer.setBoard(board);
        Vector2 aim = Vector2.get(1, 1);

        mPlayer.onShotAt(aim);

        verify(callback, times(1)).onMiss();
    }

    @Test
    public void WhenPlayerIsKilled__CallbackCalled() {
        Board board = new Board();
        mPlacement.putShipAt(board, new Ship(1, Ship.Orientation.VERTICAL), 5, 5);
        mPlayer.setBoard(board);
        Vector2 aim = Vector2.get(5, 5);

        mPlayer.onShotAt(aim);

        verify(callback, times(1)).onKillPlayer();
    }

    @Test
    public void WhenPlayerLoses__CallbackNotCalled() {
        mPlayer = newPlayer(PlayerUtils.defeatedBoardRules());
        mPlayer.setOpponentVersion(Opponent.PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL - 1);

        Board board = new Board();
        mPlacement.putShipAt(board, new Ship(1), 5, 5);
        mPlayer.setBoard(board);

        mPlayer.onShotAt(Vector2.get(5, 5));

        verify(callback, never()).onLost(any(Board.class));
    }

    @Test
    public void AfterPlayerLoses__EnemyIsNotReady() {
        if_player_can_go__enemy_is_ready();

        WhenPlayerLoses__CallbackNotCalled();

        assertThat(mPlayer.isOpponentReady(), is(false));
    }

    @Test
    public void AfterPlayerWins__EnemyIsNotReady() {
        if_player_can_go__enemy_is_ready();

        WhenPlayerWins__OpponentLost();

        assertThat(mPlayer.isOpponentReady(), is(false));
    }

    @Test
    public void WhenPlayerWins__OpponentLost() {
        mPlayer = newPlayer(PlayerUtils.defeatedBoardRules());
        mPlayer.setOpponentVersion(Opponent.PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL);

        ShotResult result = new ShotResult(Vector2.get(5, 5), Cell.HIT, new Ship(1));
        mPlayer.onShotResult(result);

        verify(mEnemy, times(1)).onLost(any(Board.class));
    }

    @Test
    public void WhenEnemyLoses_AndEnemyDoesNotSupportBoardReveal__CallbackNotCalled() {
        mPlayer = newPlayer(PlayerUtils.defeatedBoardRules());
        mPlayer.setOpponentVersion(Opponent.PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL - 1);

        ShotResult result = new ShotResult(Vector2.get(5, 5), Cell.HIT, new Ship(1));
        mPlayer.onShotResult(result);

        verify(mEnemy, never()).onLost(any(Board.class));
    }

    @Test
    public void WhenOpponentBids__ItIsReady() {
        mPlayer.onEnemyBid(0);

        verify(callback, times(1)).opponentReady();
    }

    @Test
    public void IfPlayerCanGo__OpponentIsReady() {
        mPlayer.go();

        verify(callback, times(1)).opponentReady();
    }

    @Test
    public void WhenOpponentBidsHigher__ItIsOpponentsTurn() {
        mPlayer.startBidding(0);
        mPlayer.onEnemyBid(1);

        verify(callback, times(1)).onOpponentTurn();
    }

    @Test
    public void WhenOpponentLooses__CallbackIsCalled() {
        Board board = new Board();
        mPlayer.onLost(board);

        verify(callback, times(1)).onLost(board);
    }

    // TODO: repeat the 3 for win as well
    @Test
    public void WhenGameEnds__PlayersBoardIsEmpty() {
        mPlayer.onLost(new Board());

        assertThat(BoardSetupUtils.getCellsFreeFromShips(mPlayer.getBoard()).size(), is(100));
    }

    @Test
    public void WhenOpponentLooses__enemy_board_is_empty() {
        mPlayer.onLost(new Board());

        assertThat(BoardSetupUtils.getCellsFreeFromShips(mPlayer.getEnemyBoard()).size(), is(100));
    }

    @Test
    public void WhenMessageArrives__CallbackIsCalled() {
        String message = "message";
        mPlayer.onNewMessage(message);

        verify(callback, times(1)).onMessage(message);
    }

    @Test
    public void WhenPlayerWinsOverAiOpponent__OpponentLost() {
        PlayerOpponent player = newPlayer(PlayerUtils.defeatedBoardRules());
        MyAiOpponent aiOpponent = new MyAiOpponent("Ai", mPlacement, new RussianRules());
        player.setOpponent(aiOpponent);
        aiOpponent.setOpponent(player);

        ShotResult result = new ShotResult(Vector2.get(5, 5), Cell.HIT, new Ship(1));
        player.onShotResult(result);

        assertThat(aiOpponent.lostCalled(), is(true));
    }

    // TODO: when callback is set - sticky

    private Cell enemyCellAt(Vector2 aim) {
        return mPlayer.getEnemyBoard().getCellAt(aim);
    }

    private static class MyAiOpponent extends AiOpponent {

        private boolean lostCalled;

        public MyAiOpponent(@NonNull String name, @NonNull Placement placement, @NonNull Rules rules) {
            super(name, placement, rules, new RussianBotFactory().createBot(), new Bidder(new Random()), new Random());
        }

        @Override
        public void onLost(@NonNull Board board) {
            super.onLost(board);
            lostCalled = true;
        }

        public boolean lostCalled() {
            return lostCalled;
        }
    }
}