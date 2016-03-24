package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.Bidder;
import com.ivygames.morskoiboi.Rules;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AndroidOpponentTest {

    private static final String ANDROID_NAME = "Android";
    private AndroidOpponent mAndroid;
    @Mock
    private Opponent mOpponent;
    @Mock
    private Random mRandom;
    @Mock
    private PlacementAlgorithm mPlacement;
    @Mock
    private Rules mRules;

    private final Board mBoard = new Board();
    private RussianPlacement sPlacement;
    private DelegateOpponent mCancellableOpponent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Rules rules = new RussianRules();
        sPlacement = new RussianPlacement(new Random(), rules.getTotalShips());
        when(mPlacement.generateBoard()).thenReturn(mBoard);

        mCancellableOpponent = new DelegateOpponent();

        mAndroid = new AndroidOpponent(ANDROID_NAME, mPlacement, mRules, mCancellableOpponent);
        mAndroid.setOpponent(mOpponent);
    }

    @Test
    public void after_android_is_reset__it_is_not_opponents_turn() {
        mAndroid.reset(new Bidder().newBid());
        assertThat(mAndroid.isOpponentTurn(), is(false));
    }

    @Test
    public void when_asking_for_name__actual_name_returned() {
        assertThat(mAndroid.getName(), equalTo(ANDROID_NAME));
    }

    @Test
    public void when_android_says_go_to_opponent__opponent_receives_aim() {
        mAndroid.go();
        verify(mOpponent, times(1)).onShotAt(any(Vector2.class));
    }

    @Test
    public void after_android_is_shot_at__opponent_receives_shot_result() {
        Vector2 aim = Vector2.get(5, 5);
        ArgumentCaptor<PokeResult> argument = ArgumentCaptor.forClass(PokeResult.class);
        mAndroid.onShotAt(aim);
        verify(mOpponent, times(1)).onShotResult(argument.capture());
        assertThat(argument.getValue().aim, equalTo(aim));
    }

    @Test
    public void if_android_is_hit_but_NOT_lost__opponent_goes() {
        sPlacement.putShipAt(mBoard, new Ship(2), 5, 5);
        when(mRules.isItDefeatedBoard(any(Board.class))).thenReturn(false);
        mAndroid.onShotAt(Vector2.get(5, 5));
        verify(mOpponent, times(1)).go();
    }

    @Test
    public void if_android_is_hit_and_lost__opponent_does_NOT_go() {
        sPlacement.putShipAt(mBoard, new Ship(1), 5, 5);
        when(mRules.isItDefeatedBoard(any(Board.class))).thenReturn(true);
        mAndroid.onShotAt(Vector2.get(5, 5));
        verify(mOpponent, never()).go();
    }

    @Test
    public void when_result_of_a_shot_is_miss__opponent_goes() {
        PokeResult result = new PokeResult(Vector2.get(5, 5), Cell.newMiss());
        mAndroid.onShotResult(result);
        verify(mOpponent, times(1)).go();
    }

    @Test
    public void when_result_of_a_shot_is_defeat__opponent_lost() {
        PokeResult result = new PokeResult(Vector2.get(5, 5), Cell.newHit(), new Ship(1));
        when(mRules.isItDefeatedBoard(any(Board.class))).thenReturn(true);
        mAndroid.onShotResult(result);
        verify(mOpponent, times(1)).onLost(any(Board.class));
    }

    @Test
    public void when_opponent_bid_with_higher_bid__opponent_goes() {
        mAndroid.reset(1);
        mAndroid.onEnemyBid(2);
        assertThat(mAndroid.isOpponentTurn(), is(true));
        verify(mOpponent, times(1)).go();
    }

    @Test
    public void when_opponent_bid_with_lower_bid__opponent_gets_my_bid() {
        int myBid = 2;
        mAndroid.reset(myBid);
        mAndroid.onEnemyBid(1);
        assertThat(mAndroid.isOpponentTurn(), is(false));
        verify(mOpponent, times(1)).onEnemyBid(myBid);
    }

    @Test
    public void when_android_looses__its_cancellable_delegate_is_called() {
        assertThat(mCancellableOpponent.cancelCalled, is(false));
        mAndroid.onLost(mBoard);
        assertThat(mCancellableOpponent.cancelCalled, is(true));
    }

    @Test
    public void when_android_cancelled__its_cancellable_delegate_is_called() {
        assertThat(mCancellableOpponent.cancelCalled, is(false));
        mAndroid.cancel();
        assertThat(mCancellableOpponent.cancelCalled, is(true));
    }
}