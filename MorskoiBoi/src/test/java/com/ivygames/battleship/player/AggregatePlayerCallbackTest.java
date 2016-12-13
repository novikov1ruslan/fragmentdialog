package com.ivygames.battleship.player;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.morskoiboi.PlayerCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AggregatePlayerCallbackTest {

    private AggregatePlayerCallback mAggregateCallback = new AggregatePlayerCallback();
    @Mock
    private PlayerCallback mCallback;
    @Mock
    private Board mBoard;
    @Mock
    private ShotResult mShot;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void onWin() {
        mAggregateCallback.onWin();
        verify(mCallback, never()).onWin();

        mAggregateCallback.registerCallback(mCallback);
        mAggregateCallback.onWin();
        verify(mCallback, times(1)).onWin();

        reset(mCallback);
        mAggregateCallback.unregisterCallback(mCallback);
        mAggregateCallback.onWin();
        verify(mCallback, never()).onWin();
    }

    @Test
    public void onKillPlayer() {
        mAggregateCallback.onKillPlayer();
        verify(mCallback, never()).onKillPlayer();

        mAggregateCallback.registerCallback(mCallback);
        mAggregateCallback.onKillPlayer();
        verify(mCallback, times(1)).onKillPlayer();

        reset(mCallback);
        mAggregateCallback.unregisterCallback(mCallback);
        mAggregateCallback.onKillPlayer();
        verify(mCallback, never()).onKillPlayer();
    }

    @Test
    public void onKillEnemy() {
        mAggregateCallback.onKillEnemy();
        verify(mCallback, never()).onKillEnemy();

        mAggregateCallback.registerCallback(mCallback);
        mAggregateCallback.onKillEnemy();
        verify(mCallback, times(1)).onKillEnemy();

        reset(mCallback);
        mAggregateCallback.unregisterCallback(mCallback);
        mAggregateCallback.onKillEnemy();
        verify(mCallback, never()).onKillEnemy();
    }

    @Test
    public void onMiss() {
        mAggregateCallback.onMiss();
        verify(mCallback, never()).onMiss();

        mAggregateCallback.registerCallback(mCallback);
        mAggregateCallback.onMiss();
        verify(mCallback, times(1)).onMiss();

        reset(mCallback);
        mAggregateCallback.unregisterCallback(mCallback);
        mAggregateCallback.onMiss();
        verify(mCallback, never()).onMiss();
    }

    @Test
    public void onHit() {
        mAggregateCallback.onHit();
        verify(mCallback, never()).onHit();

        mAggregateCallback.registerCallback(mCallback);
        mAggregateCallback.onHit();
        verify(mCallback, times(1)).onHit();

        reset(mCallback);
        mAggregateCallback.unregisterCallback(mCallback);
        mAggregateCallback.onHit();
        verify(mCallback, never()).onHit();
    }

    @Test
    public void onPlayerLost() {
        mAggregateCallback.onPlayerLost(mBoard);
        verify(mCallback, never()).onPlayerLost(any(Board.class));

        mAggregateCallback.registerCallback(mCallback);
        mAggregateCallback.onPlayerLost(mBoard);
        verify(mCallback, times(1)).onPlayerLost(mBoard);

        reset(mCallback);
        mAggregateCallback.unregisterCallback(mCallback);
        mAggregateCallback.onPlayerLost(mBoard);
        verify(mCallback, never()).onPlayerLost(any(Board.class));
    }

    @Test
    public void onPlayerShotResult() {
        mAggregateCallback.onPlayerShotResult(mShot);
        verify(mCallback, never()).onPlayerShotResult(any(ShotResult.class));

        mAggregateCallback.registerCallback(mCallback);
        mAggregateCallback.onPlayerShotResult(mShot);
        verify(mCallback, times(1)).onPlayerShotResult(mShot);

        reset(mCallback);
        mAggregateCallback.unregisterCallback(mCallback);
        mAggregateCallback.onPlayerShotResult(mShot);
        verify(mCallback, never()).onPlayerShotResult(any(ShotResult.class));
    }

    @Test
    public void opponentReady() {
        mAggregateCallback.opponentReady();
        verify(mCallback, never()).opponentReady();

        mAggregateCallback.registerCallback(mCallback);
        mAggregateCallback.opponentReady();
        verify(mCallback, times(1)).opponentReady();

        reset(mCallback);
        mAggregateCallback.unregisterCallback(mCallback);
        mAggregateCallback.opponentReady();
        verify(mCallback, never()).opponentReady();
    }

    @Test
    public void onOpponentTurn() {
        mAggregateCallback.onOpponentTurn();
        verify(mCallback, never()).onOpponentTurn();

        mAggregateCallback.registerCallback(mCallback);
        mAggregateCallback.onOpponentTurn();
        verify(mCallback, times(1)).onOpponentTurn();

        reset(mCallback);
        mAggregateCallback.unregisterCallback(mCallback);
        mAggregateCallback.onOpponentTurn();
        verify(mCallback, never()).onOpponentTurn();
    }

    @Test
    public void onPlayersTurn() {
        mAggregateCallback.onPlayersTurn();
        verify(mCallback, never()).onPlayersTurn();

        mAggregateCallback.registerCallback(mCallback);
        mAggregateCallback.onPlayersTurn();
        verify(mCallback, times(1)).onPlayersTurn();

        reset(mCallback);
        mAggregateCallback.unregisterCallback(mCallback);
        mAggregateCallback.onPlayersTurn();
        verify(mCallback, never()).onPlayersTurn();
    }

    @Test
    public void onMessage() {
        String message = "";
        mAggregateCallback.onMessage(message);
        verify(mCallback, never()).onMessage(anyString());

        mAggregateCallback.registerCallback(mCallback);
        mAggregateCallback.onMessage(message);
        verify(mCallback, times(1)).onMessage(message);

        reset(mCallback);
        mAggregateCallback.unregisterCallback(mCallback);
        mAggregateCallback.onMessage(message);
        verify(mCallback, never()).onMessage(anyString());
    }

    @Test
    public void onPlayerShotAt() {
        mAggregateCallback.onPlayerShotAt();
        verify(mCallback, never()).onPlayerShotAt();

        mAggregateCallback.registerCallback(mCallback);
        mAggregateCallback.onPlayerShotAt();
        verify(mCallback, times(1)).onPlayerShotAt();

        reset(mCallback);
        mAggregateCallback.unregisterCallback(mCallback);
        mAggregateCallback.onPlayerShotAt();
        verify(mCallback, never()).onPlayerShotAt();
    }

}