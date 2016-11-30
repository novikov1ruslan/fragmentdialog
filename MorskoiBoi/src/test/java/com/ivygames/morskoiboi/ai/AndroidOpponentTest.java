package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.Placement;
import com.ivygames.battleship.Rules;
import com.ivygames.battleship.RussianRules;
import com.ivygames.battleship.ai.AiOpponent;
import com.ivygames.battleship.ai.Bot;
import com.ivygames.battleship.ai.RussianBot;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.common.game.Bidder;

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
    private Rules mRules = new RussianRules();

    private final Board mBoard = new Board();
    private DelegateOpponent mCancellableOpponent = new DelegateOpponent();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mCancellableOpponent.setOpponent(mOpponent);

        Bidder bidder = getBidder(1);
        mAndroid = newAndroid(bidder, new RussianBot(mRandom));
    }

    @Test
    public void when_asking_for_name__actual_name_returned() {
        assertThat(mAndroid.getName(), equalTo(ANDROID_NAME));
    }

    @Test
    public void when_android_says_go_to_opponent__opponent_receives_aim() {
        mAndroid.go();

        verify(mOpponent, times(1)).onShotAt(any(Vector.class));
    }

    @Test
    public void after_android_is_shot_at__opponent_receives_shot_result() {
        Vector aim = Vector.get(5, 5);
        ArgumentCaptor<ShotResult> argument = ArgumentCaptor.forClass(ShotResult.class);

        mAndroid.onShotAt(aim);

        verify(mOpponent, times(1)).onShotResult(argument.capture());
        assertThat(argument.getValue().aim, is(aim));
    }

    @Test
    public void if_android_is_hit_but_NOT_lost__opponent_goes() {
        mBoard.addShip(new LocatedShip(new Ship(2), 5, 5));

        mAndroid.onShotAt(Vector.get(5, 5));

        verify(mOpponent, times(1)).go();
    }

    @Test
    public void if_android_is_lost__opponent_does_NOT_go() {

        mAndroid.onShotAt(Vector.get(5, 5));

        verify(mOpponent, never()).go();
    }

    @Test
    public void when_result_of_a_shot_is_miss__opponent_goes() {
        ShotResult result = new ShotResult(Vector.get(5, 5), Cell.MISS);
        mAndroid.onShotResult(result);
        verify(mOpponent, times(1)).go();
    }

    @Test
    public void WhenOpponentBidsWithHigherBid__OpponentGoes() {
        mAndroid.startBidding(1);

        mAndroid.onEnemyBid(2);

        verify(mOpponent, times(1)).go();
    }

    @Test
    public void WhenAndroidNotReady_AndOpponentBidsHigher__OpponentGoesOnlyOnce() {
        mAndroid = newAndroid(getBidder(1), new RussianBot(mRandom));

        mAndroid.onEnemyBid(2);

        verify(mOpponent, times(1)).go();
    }

    @Test
    public void WhenOpponentBidsWithLowerBid__OpponentGetsMyBid() {
        mAndroid.startBidding(2);

        mAndroid.onEnemyBid(1);

        verify(mOpponent, times(1)).onEnemyBid(2);
    }

    @Test
    public void when_android_cancelled__its_cancellable_delegate_is_called() {
        assertThat(mCancellableOpponent.cancelCalled, is(false));

        mAndroid.cancel();

        assertThat(mCancellableOpponent.cancelCalled, is(true));
    }

    private AiOpponent newAndroid(Bidder bidder, Bot bot) {
        AiOpponent aiOpponent = new AiOpponent(ANDROID_NAME, mRules, bot, bidder, mRandom);
        aiOpponent.setBoard(mBoard);
        aiOpponent.setOpponent(mCancellableOpponent);

        return aiOpponent;
    }

    @NonNull
    private Bidder getBidder(int value) {
        Bidder bidder = mock(Bidder.class);
        when(bidder.newBid()).thenReturn(value);
        return bidder;
    }
}