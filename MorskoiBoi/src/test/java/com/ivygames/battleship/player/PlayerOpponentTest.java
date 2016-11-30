package com.ivygames.battleship.player;

import android.support.annotation.NonNull;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.ChatMessage;
import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.Rules;
import com.ivygames.battleship.ai.AiOpponent;
import com.ivygames.battleship.ai.RussianBot;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.common.analytics.ExceptionHandler;
import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.PlayerCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class PlayerOpponentTest {

    private static final String PLAYER_NAME = "Sagi";
    private PlayerOpponent mPlayer;
    @Mock
    private Opponent mEnemy;
    @Mock
    private PlayerCallback callback;
    @Mock
    private Rules rules;
    private ChatListener listener = new ChatListener() {
        @Override
        public void showChatCrouton(ChatMessage message) {

        }
    };
    @Mock
    private Random mRandom;

    @Before
    public void setUp() {
        initMocks(this);
        ExceptionHandler.setDryRun(true);

        when(rules.getAllShipsSizes()).thenReturn(new int[]{1, 2, 3});
        mPlayer = newPlayer(rules);
    }

    @NonNull
    private PlayerOpponent newPlayer(Rules rules) {
        PlayerOpponent player = new PlayerOpponent(PLAYER_NAME, rules.getAllShipsSizes().length);
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
    public void WhenCallbackIsRegisteredWhenOpponentIsReady__CallbackNotifiedOfThat() {
        mPlayer.go();

        PlayerCallback callback = mock(PlayerCallback.class);
        mPlayer.registerCallback(callback);

        verify(callback, times(1)).opponentReady();
    }

    @Test
    public void WhenCallbackIsRegisteredWhenOpponentReady_AndPlayerReady__CallbackNotifiedAboutPlayerTurn() {
        mPlayer.onEnemyBid(1);
        mPlayer.startBidding(2);

        PlayerCallback callback = mock(PlayerCallback.class);
        mPlayer.registerCallback(callback);

        verify(callback, times(1)).onPlayersTurn();
    }

    @Test
    public void WhenCallbackIsRegisteredWhenOpponentReady_AndPlayerReady__CallbackNotifiedAboutOpponentTurn() {
        mPlayer.onEnemyBid(2);
        mPlayer.startBidding(1);

        PlayerCallback callback = mock(PlayerCallback.class);
        mPlayer.registerCallback(callback);

        verify(callback, times(1)).onOpponentTurn();
    }

    @Test
    public void when_enemy_bids_on_non_ready_player__enemy_does_not_go() {
        mPlayer.onEnemyBid(2);

        verify(mEnemy, never()).go();
    }

    @Test
    public void after_shot_result_is_miss__enemy_board_shows_miss() {
        Vector aim = Vector.get(5, 5);
        ShotResult result = new ShotResult(aim, Cell.MISS);

        mPlayer.onShotResult(result);

        assertThat(enemyCellAt(aim), is(Cell.MISS));
    }

    @Test
    public void when_result_of_a_shot_is_miss__opponent_goes() {
        ShotResult result = new ShotResult(Vector.get(5, 5), Cell.MISS);

        mPlayer.onShotResult(result);

        verify(mEnemy, times(1)).go();
    }

    @Test
    public void EnemyMisses__WhenShotOnEmptyCell() {
        mPlayer.onShotAt(Vector.get(5, 5));

        ArgumentCaptor<ShotResult> argument = ArgumentCaptor.forClass(ShotResult.class);

        verify(mEnemy, times(1)).onShotResult(argument.capture());
        assertThat(argument.getValue().cell, is(Cell.MISS));
    }

    @Test
    public void after_shot_result_is_hit__enemy_board_shows_hit() {
        Vector aim = Vector.get(5, 5);
        Cell cell = Cell.HIT;
        ShotResult result = new ShotResult(aim, cell);
        mPlayer.onShotResult(result);
        assertThat(enemyCellAt(aim), equalTo(cell));
    }

    @Test
    public void after_shot_result_is_kill__enemy_board_shows_killed_ship() {
        Vector aim = Vector.get(5, 5);
        Ship ship = new Ship(2);
        ShotResult result = new ShotResult(aim, Cell.HIT, new LocatedShip(ship));

        mPlayer.onShotResult(result);

        LocatedShip actual = mPlayer.getEnemyBoard().getShipAt(aim);
        assertThat(actual.ship, equalTo(ship));
        assertThat(enemyCellAt(aim), equalTo(Cell.HIT));
    }

    @Test
    public void after_shooting_on_my_empty_cell__result_is_miss() {
        Board board = new Board();
        mPlayer.setBoard(board);
        mPlayer.onShotAt(Vector.get(5, 5));

        assertThat(board.getCell(5, 5) == Cell.MISS, is(true));
    }

    @Test
    public void after_shooting_on_my_ship__result_is_hit() {
        Vector aim = Vector.get(5, 5);
        Board board = new Board();
        mPlayer.setBoard(board);
        Ship ship = new Ship(2);
        board.addShip(new LocatedShip(ship, aim));

        mPlayer.onShotAt(aim);

        assertThat(board.getCell(aim) == Cell.HIT, is(true));
    }

    @Test
    public void after_killing_on_my_ship__result_is_kill() {
        Vector aim = Vector.get(5, 5);
        Board board = new Board();
        mPlayer.setBoard(board);
        Ship ship = new Ship(1);
        board.addShip(new LocatedShip(ship, aim));

        mPlayer.onShotAt(aim);

        assertThat(board.getCell(5, 5) == Cell.HIT, is(true));
    }

    @Test
    public void when_asking_for_name__actual_name_returned() {
        assertThat(mPlayer.getName(), equalTo(PLAYER_NAME));
    }

    @Test
    public void when_asking_for_board__actual_board_returned() {
        Board board = new Board();
        board.setCell(Cell.MISS, Vector.get(4, 4))
        ;
        mPlayer.setBoard(board);

        assertThat(mPlayer.getBoard(), is(board));
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
    public void WhenPlayerGoesForNonReadyOpponent__ItIsPlayersTurn() {
        mPlayer.go();

        verify(callback, times(1)).onPlayersTurn();
    }

    @Test
    public void WhenPlayerGoesForReadyOpponent__ItIsPlayersTurn() {
        mPlayer.onEnemyBid(1);
        mPlayer.startBidding(2);
        mPlayer.go();

        verify(callback, times(1)).onPlayersTurn();
    }

    @Test
    public void AfterCallbackUnregistered__ItIsNotCalled() {
        mPlayer.unregisterCallback(callback);
        mPlayer.go();

        verify(callback, never()).onPlayersTurn();
    }

    @Test
    public void WhenPlayerGetsShotResult__CallbackCalled() {
        ShotResult result = new ShotResult(Vector.get(1, 1), Cell.HIT);
        mPlayer.onShotResult(result);

        verify(callback, times(1)).onPlayerShotResult(result);
    }

    @Test
    public void WhenPlayerKillsShip__CallbackCalled() {
        ShotResult result = new ShotResult(Vector.get(1, 1), Cell.HIT, new LocatedShip(new Ship(1)));
        mPlayer.onShotResult(result);

        verify(callback, times(1)).onKillEnemy();
    }

    @Test
    public void WhenPlayerHitsShip__CallbackCalled() {
        ShotResult result = new ShotResult(Vector.get(1, 1), Cell.HIT);
        mPlayer.onShotResult(result);

        verify(callback, times(1)).onHit();
    }

    @Test
    public void WhenPlayerMissesShip__CallbackCalled() {
        ShotResult result = new ShotResult(Vector.get(1, 1), Cell.MISS);
        mPlayer.onShotResult(result);

        verify(callback, times(1)).onMiss();
        verify(callback, times(1)).onOpponentTurn();
    }

    @Test
    public void WhenPlayerIsShotAt__CallbackCalled() {
        Vector aim = Vector.get(1, 1);
        mPlayer.onShotAt(aim);

        verify(callback, times(1)).onPlayerShotAt();
    }

    @Test
    public void WhenPlayerIsHit__CallbackCalled() {
        Board board = new Board();
        Ship ship = new Ship(2, Ship.Orientation.VERTICAL);
        board.addShip(new LocatedShip(ship, 5, 5));
        mPlayer.setBoard(board);
        Vector aim = Vector.get(5, 5);

        mPlayer.onShotAt(aim);

        verify(callback, times(1)).onHit();
    }

    @Test
    public void WhenPlayerIsMissed__CallbackCalled() {
        Board board = new Board();
        Ship ship = new Ship(2, Ship.Orientation.VERTICAL);
        board.addShip(new LocatedShip(ship, 5, 5));
        mPlayer.setBoard(board);
        Vector aim = Vector.get(1, 1);

        mPlayer.onShotAt(aim);

        verify(callback, times(1)).onMiss();
    }

    @Test
    public void WhenPlayerIsKilled__CallbackCalled() {
        Board board = new Board();
        Ship ship = new Ship(1, Ship.Orientation.VERTICAL);
        board.addShip(new LocatedShip(ship, 5, 5));
        mPlayer.setBoard(board);
        Vector aim = Vector.get(5, 5);

        mPlayer.onShotAt(aim);

        verify(callback, times(1)).onKillPlayer();
    }

    @Test
    public void WhenPlayerLoses__CallbackNotCalled() {
        mPlayer = newPlayer(PlayerTestUtils.defeatedBoardRules(1));
        mPlayer.setOpponentVersion(Opponent.PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL - 1);

        Board board = new Board();
        Ship ship = new Ship(1);
        board.addShip(new LocatedShip(ship, 5, 5));
        mPlayer.setBoard(board);

        mPlayer.onShotAt(Vector.get(5, 5));

        verify(callback, never()).onPlayerLost(any(Board.class));
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
        mPlayer = newPlayer(PlayerTestUtils.defeatedBoardRules(1));
        mPlayer.setOpponentVersion(Opponent.PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL);

        Ship ship = newDeadShip();
        ShotResult result = new ShotResult(Vector.get(5, 5), Cell.HIT, new LocatedShip(ship));
        mPlayer.onShotResult(result);

        verify(mEnemy, times(1)).onLost(any(Board.class));
    }

    @Test
    public void WhenEnemyLoses_AndEnemyDoesNotSupportBoardReveal__CallbackNotCalled() {
        PlayerOpponent player = new PlayerOpponent(PLAYER_NAME, 1);
        player.setOpponentVersion(Opponent.PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL - 1);

        ShotResult result = new ShotResult(Vector.get(5, 5), Cell.HIT, new LocatedShip(new Ship(1)));
        player.onShotResult(result);

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

        verify(callback, times(1)).onPlayerLost(board);
    }

    // TODO: repeat the 3 for win as well
    @Test
    public void WhenGameEnds__PlayersBoardIsEmpty() {
        mPlayer.onLost(new Board());

        assertThat(BoardUtils.getCoordinatesFreeFromShips(mPlayer.getBoard(), false).size(), is(100));
    }

    @Test
    public void WhenOpponentLooses__enemy_board_is_empty() {
        mPlayer.onLost(new Board());

        assertThat(BoardUtils.getCoordinatesFreeFromShips(mPlayer.getEnemyBoard(), false).size(), is(100));
    }

    @Test
    public void WhenMessageArrives__CallbackIsCalled() {
        String message = "message";
        mPlayer.onNewMessage(message);

        verify(callback, times(1)).onMessage(message);
    }

    @Test
    public void WhenPlayerWinsOverAiOpponent__OpponentLost() {
        PlayerOpponent player = newPlayer(PlayerTestUtils.defeatedBoardRules(1));
        MyAiOpponent aiOpponent = new MyAiOpponent("Ai", rules);
        player.setOpponent(aiOpponent);
        aiOpponent.setOpponent(player);

        Ship ship = newDeadShip();
        ShotResult result = new ShotResult(Vector.get(5, 5), Cell.HIT, new LocatedShip(ship));
        player.onShotResult(result);

        assertThat(aiOpponent.lostCalled(), is(true));
    }

    @Test
    public void AfterPlayerIsShotAt__OpponentReceivesShotResult() {
        Vector aim = Vector.get(5, 5);
        ArgumentCaptor<ShotResult> argument = ArgumentCaptor.forClass(ShotResult.class);

        mPlayer.onShotAt(aim);

        verify(mEnemy, times(1)).onShotResult(argument.capture());
        assertThat(argument.getValue().aim, is(aim));
    }

    @Test
    public void WhenPlayerIsHitButNotLost__OpponentGoes() {
        Board board = new Board();
        board.addShip(new LocatedShip(new Ship(2), 5, 5));
        mPlayer.setBoard(board);

        mPlayer.onShotAt(Vector.get(5, 5));

        verify(mEnemy, times(1)).go();
    }

    @NonNull
    private Ship newDeadShip() {
        Ship ship = new Ship(1);
        ship.shoot();
        return ship;
    }

    private Cell enemyCellAt(Vector aim) {
        return mPlayer.getEnemyBoard().getCell(aim);
    }

    private class MyAiOpponent extends AiOpponent {

        private boolean lostCalled;

        MyAiOpponent(@NonNull String name, @NonNull Rules rules) {
            super(name, rules, new RussianBot(mRandom), new Bidder(mRandom), mRandom);
        }

        @Override
        public void onLost(@NonNull Board board) {
            super.onLost(board);
            lostCalled = true;
        }

        boolean lostCalled() {
            return lostCalled;
        }
    }
}