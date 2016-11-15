package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.ShotResult;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.player.AiOpponent;
import com.ivygames.morskoiboi.player.RussianBotFactory;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AndroidOpponentTest {

    private static final String ANDROID_NAME = "Android";
    private AiOpponent mAndroid;
    @Mock
    private Opponent mOpponent;
    @Mock
    private Random mRandom;
    @Mock
    private Placement mPlacement;
    @Mock
    private Rules mRules;

    private final Board mBoard = new Board();
    private DelegateOpponent mCancellableOpponent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mCancellableOpponent = new DelegateOpponent();
        mCancellableOpponent.setOpponent(mOpponent);

        Bidder bidder = mock(Bidder.class);
        when(bidder.newBid()).thenReturn(1);
        mAndroid = newAndroid(bidder);
    }

    private AiOpponent newAndroid(Bidder bidder) {
        AiOpponent aiOpponent = new AiOpponent(ANDROID_NAME, mPlacement, mRules,
                new RussianBotFactory().createBot(), bidder, mRandom);
        aiOpponent.setBoard(mBoard);
        aiOpponent.setOpponent(mCancellableOpponent);

        return aiOpponent;
    }

    @NonNull
    private Placement placement() {
        return new Placement(new Random(), new RussianRules());
    }

    @Test
    public void when_asking_for_name__actual_name_returned() {
        assertThat(mAndroid.getName(), equalTo(ANDROID_NAME));
    }

    @Test
    public void when_android_says_go_to_opponent__opponent_receives_aim() {
        when(mRules.getAllShipsSizes()).thenReturn(new int[]{});
        mAndroid.go();
        verify(mOpponent, times(1)).onShotAt(any(Vector2.class));
    }

    @Test
    public void after_android_is_shot_at__opponent_receives_shot_result() {
        Vector2 aim = Vector2.get(5, 5);
        ArgumentCaptor<ShotResult> argument = ArgumentCaptor.forClass(ShotResult.class);
        mAndroid.onShotAt(aim);
        verify(mOpponent, times(1)).onShotResult(argument.capture());
        assertThat(argument.getValue().aim, equalTo(aim));
    }

    @Test
    public void if_android_is_hit_but_NOT_lost__opponent_goes() {
        placement().putShipAt(mBoard, new Ship(2), 5, 5);
        when(mRules.isItDefeatedBoard(any(Board.class))).thenReturn(false);

        mAndroid.onShotAt(Vector2.get(5, 5));

        verify(mOpponent, times(1)).go();
    }

    @Test
    public void if_android_is_lost__opponent_does_NOT_go() {
        when(mRules.isItDefeatedBoard(any(Board.class))).thenReturn(true);
        mAndroid.onShotAt(Vector2.get(5, 5));
        verify(mOpponent, never()).go();
    }

    @Test
    public void when_result_of_a_shot_is_miss__opponent_goes() {
        ShotResult result = new ShotResult(Vector2.get(5, 5), Cell.MISS);
        mAndroid.onShotResult(result);
        verify(mOpponent, times(1)).go();
    }

    @Test
    public void WhenOpponentBidsWithHigherBid__OpponentGoes() {
        when(mRules.getAllShipsSizes()).thenReturn(new int[]{});
        mAndroid.startBidding(1);

        mAndroid.onEnemyBid(2);

        verify(mOpponent, times(1)).go();
    }

    @Test
    public void WhenAndroidNotReady_AndOpponentBidsHigher__OpponentGoesOnlyOnce() {
        when(mRules.getAllShipsSizes()).thenReturn(new int[]{});

        Bidder bidder = mock(Bidder.class);
        when(bidder.newBid()).thenReturn(1);
        mAndroid = newAndroid(bidder);

        mAndroid.onEnemyBid(2);

        verify(mOpponent, times(1)).go();
    }

    @Test
    public void WhenOpponentBidsWithLowerBid__OpponentGetsMyBid() {
        mAndroid.startBidding(2);
        when(mRules.getAllShipsSizes()).thenReturn(new int[]{});

        mAndroid.onEnemyBid(1);

        verify(mOpponent, times(1)).onEnemyBid(2);
    }

    @Test
    public void when_android_cancelled__its_cancellable_delegate_is_called() {
        assertThat(mCancellableOpponent.cancelCalled, is(false));
        mAndroid.cancel();
        assertThat(mCancellableOpponent.cancelCalled, is(true));
    }

}