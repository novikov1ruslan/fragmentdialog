package com.ivygames.morskoiboi.player;

import com.ivygames.common.analytics.ExceptionHandler;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerCallback;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class PlayerOpponentTest {

    private static final String PLAYER_NAME = "Sagi";
    private PlayerOpponent mPlayer;
    @Mock
    private Opponent mEnemy;
    @Mock
    private Random mRandom;
    private Placement mPlacement;
    @Mock
    private PlayerCallback callback;

    private Rules rules = new RussianRules();
    private ChatListener listener = new ChatListener() {
        @Override
        public void showChatCrouton(ChatMessage message) {

        }
    };

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ExceptionHandler.setDryRun(true);

        mPlacement = new Placement(new Random(), rules);

        mPlayer = new PlayerOpponent(PLAYER_NAME, mPlacement, rules, listener);
        mPlayer.setOpponent(mEnemy);
        mPlayer.setCallback(callback);
    }

    @Test
    public void AfterPlayerLoses__EnemyIsNotReady() {
        mPlayer.onLost(new Board());

        assertThat(mPlayer.isOpponentReady(), is(false));
    }

    @Test
    public void when_enemy_bids_on_non_ready_player__enemy_does_not_go() {
        mPlayer.onEnemyBid(2);
        verify(mEnemy, never()).go();
    }

    @Test
    public void if_player_can_go__enemy_is_ready() {
        mPlayer.go();
        assertThat(mPlayer.isOpponentReady(), is(true));
    }

    @Test
    public void after_shot_result_is_miss__enemy_board_shows_miss() {
        Vector2 aim = Vector2.get(5, 5);
        Cell cell = Cell.newMiss();
        PokeResult result = new PokeResult(aim, cell);
        mPlayer.onShotResult(result);
        assertThat(enemyCellAt(aim), equalTo(cell));
    }

    @Test
    public void after_shot_result_is_hit__enemy_board_shows_hit() {
        Vector2 aim = Vector2.get(5, 5);
        Cell cell = Cell.newHit();
        PokeResult result = new PokeResult(aim, cell);
        mPlayer.onShotResult(result);
        assertThat(enemyCellAt(aim), equalTo(cell));
    }

    @Test
    public void after_shot_result_is_kill__enemy_board_shows_killed_ship() {
        Vector2 aim = Vector2.get(5, 5);
        Cell cell = Cell.newHit();
        Ship ship = new Ship(2);
        ship.setCoordinates(5, 5);
        PokeResult result = new PokeResult(aim, cell, ship);
        mPlayer.onShotResult(result);
        Ship actual = mPlayer.getEnemyBoard().getFirstShipAt(aim);
        assertThat(actual, equalTo(ship));
    }

    @Test
    public void after_shooting_on_my_empty_cell__result_is_miss() {
        Vector2 aim = Vector2.get(5, 5);
        Board board = new Board();
        mPlayer.setBoard(board);
        mPlayer.onShotAt(aim);

        assertThat(board.getCell(5, 5).isMiss(), is(true));
    }

//    @Test
//    public void after_shooting_on_my_ship__result_is_hit() {
//        Vector2 aim = Vector2.get(5, 5);
//        Board board = new Board();
//        mPlayer.setBoard(board);
//        mPlacement.putShipAt(board, new Ship(2), 5, 5);
//        PokeResult result = mPlayer.createResultForShootingAt(aim);
//        mPlayer.onShotAtForResult(result);
//        assertThat(result.cell.isHit(), is(true));
//        assertThat(result.ship, is(nullValue()));
//    }

//    @Test
//    public void after_killing_on_my_ship__result_is_kill() {
//        Vector2 aim = Vector2.get(5, 5);
//        Board board = new Board();
//        mPlayer.setBoard(board);
//        mPlacement.putShipAt(board, new Ship(2, Ship.Orientation.VERTICAL), 5, 5);
//        PokeResult result = mPlayer.createResultForShootingAt(aim);
//        mPlayer.onShotAtForResult(result);
//        result = mPlayer.createResultForShootingAt(Vector2.get(5, 6));
//
//        mPlayer.onShotAtForResult(result);
//
//        assertThat(result.cell.isHit(), is(true));
//        assertThat(result.ship, is(notNullValue()));
//    }

    @Test
    public void when_asking_for_name__actual_name_returned() {
        assertThat(mPlayer.getName(), equalTo(PLAYER_NAME));
    }

    @Test
    public void when_asking_for_board__actual_board_returned() {
        Board board = new Board();
        board.setCell(Cell.newMiss(), Vector2.get(4, 4));
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
    public void WhenPlayerGoes__GoCallbackCalled() {
        mPlayer.go();

        verify(callback, times(1)).onPlayerGoes();
    }

    @Test
    public void WhenPlayerGoes__ItIsPlayersTurn() {
        reset(callback);
        mPlayer.go();

        verify(callback, times(1)).onPlayersTurn();
    }

    @Test
    public void WhenPlayerGetsShotResult__CallbackCalled() {
        PokeResult result = new PokeResult(Vector2.get(1, 1), Cell.newHit());
        mPlayer.onShotResult(result);

        verify(callback, times(1)).onShotResult(result);
    }

    @Test
    public void WhenPlayerKillsShip__CallbackCalled() {
        PokeResult result = new PokeResult(Vector2.get(1, 1), Cell.newHit(), new Ship(1));
        mPlayer.onShotResult(result);

        verify(callback, times(1)).onKill(PlayerCallback.Side.OPPONENT);
    }

    @Test
    public void WhenPlayerHitsShip__CallbackCalled() {
        PokeResult result = new PokeResult(Vector2.get(1, 1), Cell.newHit());
        mPlayer.onShotResult(result);

        verify(callback, times(1)).onHit(PlayerCallback.Side.OPPONENT);
    }

    @Test
    public void WhenPlayerMissesShip__CallbackCalled() {
        PokeResult result = new PokeResult(Vector2.get(1, 1), Cell.newMiss());
        mPlayer.onShotResult(result);

        verify(callback, times(1)).onMiss(PlayerCallback.Side.OPPONENT);
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

        verify(callback, times(1)).onHit(PlayerCallback.Side.PLAYER);
    }

    @Test
    public void WhenPlayerIsMissed__CallbackCalled() {
        Board board = new Board();
        mPlacement.putShipAt(board, new Ship(2, Ship.Orientation.VERTICAL), 5, 5);
        mPlayer.setBoard(board);
        Vector2 aim = Vector2.get(1, 1);

        mPlayer.onShotAt(aim);

        verify(callback, times(1)).onMiss(PlayerCallback.Side.PLAYER);
    }

    @Test
    public void WhenPlayerIsKilled__CallbackCalled() {
        Board board = new Board();
        mPlacement.putShipAt(board, new Ship(1, Ship.Orientation.VERTICAL), 5, 5);
        mPlayer.setBoard(board);
        Vector2 aim = Vector2.get(5, 5);

        mPlayer.onShotAt(aim);

        verify(callback, times(1)).onKill(PlayerCallback.Side.PLAYER);
    }

    @Test
    public void WhenOpponentLoses_AndOpponentDoesNotSupportBoardReveal__CallbackIsCalled() {
        rules = mock(Rules.class);
        when(rules.isItDefeatedBoard(any(Board.class))).thenReturn(true);
        mPlayer = new PlayerOpponent(PLAYER_NAME, mPlacement, rules, listener);
        mPlayer.setOpponent(mEnemy);
        mPlayer.setOpponentVersion(Opponent.PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL - 1);
        mPlayer.setCallback(callback);

        Board board = new Board();
        mPlacement.putShipAt(board, new Ship(1, Ship.Orientation.VERTICAL), 5, 5);
        mPlayer.setBoard(board);

        Vector2 aim = Vector2.get(5, 5);
        mPlayer.onShotAt(aim);

        verify(callback, times(1)).onLost(null);
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

        assertThat(mPlayer.getBoard().getEmptyCells().size(), is(100));
    }

    @Test
    public void WhenOpponentLooses__enemy_board_is_empty() {
        mPlayer.onLost(new Board());

        assertThat(mPlayer.getEnemyBoard().getEmptyCells().size(), is(100));
    }

    @Test
    public void WhenMessageArrives__CallbackIsCalled() {
        String message = "message";
        mPlayer.onNewMessage(message);

        verify(callback, times(1)).onMessage(message);
    }


    // TODO: when callback is set - sticky

    private Cell enemyCellAt(Vector2 aim) {
        return mPlayer.getEnemyBoard().getCellAt(aim);
    }

}