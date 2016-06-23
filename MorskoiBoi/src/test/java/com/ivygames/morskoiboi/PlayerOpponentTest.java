package com.ivygames.morskoiboi;

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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Rules rules = new RussianRules();
        mPlacement = new Placement(new Random(), rules);
        ChatListener listener = new ChatListener() {
            @Override
            public void showChatCrouton(ChatMessage message) {

            }
        };
        mPlayer = new PlayerOpponent(PLAYER_NAME, mPlacement, rules, listener);
        mPlayer.setOpponent(mEnemy);
        mPlayer.setCallback(callback);
    }

    @Test
    public void after_player_is_reset__enemy_is_not_ready() {
        mPlayer.reset(new Bidder().newBid());
        assertThat(mPlayer.isOpponentReady(), is(false));
    }

    @Test
    public void after_player_is_reset__his_board_is_empty() {
        mPlayer.reset(new Bidder().newBid());
        assertThat(mPlayer.getBoard().getEmptyCells().size(), is(100));
    }

    @Test
    public void after_player_is_reset__enemy_board_is_empty() {
        mPlayer.reset(new Bidder().newBid());
        assertThat(mPlayer.getEnemyBoard().getEmptyCells().size(), is(100));
    }

    @Test
    public void after_player_is_reset__it_is_not_enemy_turn() {
        mPlayer.reset(new Bidder().newBid());
        assertThat(mPlayer.isOpponentTurn(), is(false));
    }

    @Test
    public void after_player_is_reset__enemy_version_is_0() {
        mPlayer.reset(new Bidder().newBid());
        assertThat(mPlayer.getOpponentVersion(), is(0));
    }

    @Test
    public void when_enemy_bids_on_non_ready_player__enemy_does_not_go() {
        mPlayer.reset(1);
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
        PokeResult result = mPlayer.createResultForShootingAt(aim);
        mPlayer.onShotAtForResult(result);
        assertThat(result.cell.isMiss(), is(true));
        assertThat(result.ship, is(nullValue()));
    }

    @Test
    public void after_shooting_on_my_ship__result_is_hit() {
        Vector2 aim = Vector2.get(5, 5);
        Board board = new Board();
        mPlayer.setBoard(board);
        mPlacement.putShipAt(board, new Ship(2), 5, 5);
        PokeResult result = mPlayer.createResultForShootingAt(aim);
        mPlayer.onShotAtForResult(result);
        assertThat(result.cell.isHit(), is(true));
        assertThat(result.ship, is(nullValue()));
    }

    @Test
    public void after_killing_on_my_ship__result_is_kill() {
        Vector2 aim = Vector2.get(5, 5);
        Board board = new Board();
        mPlayer.setBoard(board);
        mPlacement.putShipAt(board, new Ship(2, Ship.Orientation.VERTICAL), 5, 5);
        PokeResult result = mPlayer.createResultForShootingAt(aim);
        mPlayer.onShotAtForResult(result);
        result = mPlayer.createResultForShootingAt(Vector2.get(5, 6));
        mPlayer.onShotAtForResult(result);
        assertThat(result.cell.isHit(), is(true));
        assertThat(result.ship, is(notNullValue()));
    }

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
        mPlayer.reset(0);
        mPlayer.onEnemyBid(1);
        mPlayer.startBidding();
        verify(mEnemy, times(1)).go();
    }

    @Test
    public void player_bids_second_and_higher__enemy_gets_player_bid() {
        int myBid = 2;
        mPlayer.reset(myBid);
        mPlayer.onEnemyBid(1);
        mPlayer.startBidding();
        verify(mEnemy, times(1)).onEnemyBid(myBid);
    }

    @Test
    public void enemy_bids_second_and_higher__enemy_goes() {
        mPlayer.reset(0);
        mPlayer.startBidding();
        mPlayer.onEnemyBid(1);
        verify(mEnemy, times(1)).go();
    }

    @Test
    public void enemy_bids_second_and_lower__enemy_does_not_go() {
        mPlayer.reset(2);
        mPlayer.startBidding();
        mPlayer.onEnemyBid(1);
        verify(mEnemy, never()).go();
    }

    @Test
    public void testSetOpponentVersion() throws Exception {
        mPlayer.setOpponentVersion(2);
        assertThat(mPlayer.getOpponentVersion(), is(2));
    }

    @Test
    public void WhenPlayerGoes__GoCallbackCalled() {
        mPlayer.go();

        verify(callback, times(1)).go();
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
    public void WhenEnemyBids__ItIsReady() {
        mPlayer.onEnemyBid(0);

        verify(callback, times(1)).opponentReady();
    }

    @Test
    public void WhenEnemyBidsHigher__ItIsOpponentsTurn() {
        mPlayer.reset(0);
        mPlayer.startBidding();
        mPlayer.onEnemyBid(1);

        verify(callback, times(1)).onOpponentTurn();
    }

    private Cell enemyCellAt(Vector2 aim) {
        return mPlayer.getEnemyBoard().getCellAt(aim);
    }

}