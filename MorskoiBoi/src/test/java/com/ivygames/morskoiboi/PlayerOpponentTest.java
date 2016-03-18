package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.variant.RussianPlacement;
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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Rules rules = new RussianRules();
        RulesFactory.setRules(rules);
        PlacementFactory.setPlacementAlgorithm(new RussianPlacement(new Random(), rules.getTotalShips()));
        mPlayer = new PlayerOpponent(PLAYER_NAME, PlacementFactory.getAlgorithm());
        mPlayer.setOpponent(mEnemy);
    }

    @Test
    public void after_player_is_reset__enemy_is_not_ready() {
        mPlayer.reset(new Random());
        assertThat(mPlayer.isOpponentReady(), is(false));
    }

    @Test
    public void after_player_is_reset__his_board_is_empty() {
        mPlayer.reset(new Random());
        assertThat(mPlayer.getBoard().getEmptyCells().size(), is(100));
    }

    @Test
    public void after_player_is_reset__enemy_board_is_empty() {
        mPlayer.reset(new Random());
        assertThat(mPlayer.getEnemyBoard().getEmptyCells().size(), is(100));
    }

    @Test
    public void after_player_is_reset__it_is_not_enemy_turn() {
        mPlayer.reset(new Random());
        assertThat(mPlayer.isOpponentTurn(), is(false));
    }

    @Test
    public void after_player_is_reset__enemy_version_is_0() {
        mPlayer.reset(new Random());
        assertThat(mPlayer.getOpponentVersion(), is(0));
    }

    @Test
    public void when_enemy_bids_on_non_ready_player__enemy_does_not_go() {
        when(mRandom.nextInt(anyInt())).thenReturn(1);
        mPlayer.reset(mRandom);
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
        PokeResult result = mPlayer.onShotAtForResult(aim);
        assertThat(result.cell.isMiss(), is(true));
        assertThat(result.ship, is(nullValue()));
    }

    @Test
    public void after_shooting_on_my_ship__result_is_hit() {
        Vector2 aim = Vector2.get(5, 5);
        Board board = new Board();
        mPlayer.setBoard(board);
        PlacementFactory.getAlgorithm().putShipAt(board, new Ship(2), 5, 5);
        PokeResult result = mPlayer.onShotAtForResult(aim);
        assertThat(result.cell.isHit(), is(true));
        assertThat(result.ship, is(nullValue()));
    }

    @Test
    public void after_killing_on_my_ship__result_is_kill() {
        Vector2 aim = Vector2.get(5, 5);
        Board board = new Board();
        mPlayer.setBoard(board);
        PlacementFactory.getAlgorithm().putShipAt(board, new Ship(2, Ship.Orientation.VERTICAL), 5, 5);
        mPlayer.onShotAtForResult(aim);
        PokeResult result = mPlayer.onShotAtForResult(Vector2.get(5, 6));
        assertThat(result.cell.isHit(), is(true));
        assertThat(result.ship, is(notNullValue()));
    }

    @Test
    public void when_asking_for_name__actual_name_returned() {
        assertThat(mPlayer.getName(), equalTo(PLAYER_NAME));
    }

    @Test
    public void when_shooting__enemy_is_shot_at() throws Exception {
        mPlayer.shoot(5, 5);
        verify(mEnemy, times(1)).onShotAt(Vector2.get(5, 5));
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
        setMyBidTo(0);
        mPlayer.onEnemyBid(1);
        mPlayer.startBidding();
        verify(mEnemy, times(1)).go();
    }

    @Test
    public void player_bids_second_and_higher__enemy_gets_player_bid() {
        int myBid = 2;
        setMyBidTo(myBid);
        mPlayer.onEnemyBid(1);
        mPlayer.startBidding();
        verify(mEnemy, times(1)).onEnemyBid(myBid);
    }

    @Test
    public void enemy_bids_second_and_higher__enemy_goes() {
        setMyBidTo(0);
        mPlayer.startBidding();
        mPlayer.onEnemyBid(1);
        verify(mEnemy, times(1)).go();
    }

    @Test
    public void enemy_bids_second_and_lower__enemy_does_not_go() {
        setMyBidTo(2);
        mPlayer.startBidding();
        mPlayer.onEnemyBid(1);
        verify(mEnemy, never()).go();
    }

    @Test
    public void testSetOpponentVersion() throws Exception {
        mPlayer.setOpponentVersion(2);
        assertThat(mPlayer.getOpponentVersion(), is(2));
    }

    private Cell enemyCellAt(Vector2 aim) {
        return mPlayer.getEnemyBoard().getCellAt(aim);
    }

    private void setMyBidTo(int myBid) {
        when(mRandom.nextInt(anyInt())).thenReturn(myBid);
        mPlayer.reset(mRandom);
    }
}