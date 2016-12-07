package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.Rules;
import com.ivygames.battleship.board.Vector;
import com.ivygames.common.game.Bidder;
import com.ivygames.morskoiboi.ai.DelegateOpponent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AiOpponentTest {

    private static final String ANDROID_NAME = "Android";
    private AiOpponent mAndroid;
    @Mock
    private Opponent mOpponent;

    private final DelegateOpponent mCancellableOpponent = new DelegateOpponent();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mCancellableOpponent.setOpponent(mOpponent);

        mAndroid = newAndroid(getBidder(1));
    }

    @Test
    public void WhenAndroidGoes__OpponentGetsShot() {
        mAndroid.go();

        verify(mOpponent, times(1)).onShotAt(any(Vector.class));
    }

    /**
     * This test if for when Player substituted by Ai in demos.
     */
    @Test
    public void WhenOpponentBidsWithHigherBid__OpponentGoes() {
        mAndroid.startBidding(1);

        mAndroid.onEnemyBid(2);

        verify(mOpponent, times(1)).go();
    }

    /**
     * This test if for when Player substituted by Ai in demos.
     */
    @Test
    public void WhenOpponentBidsWithLowerBid__OpponentGetsMyBid() {
        mAndroid.startBidding(2);

        mAndroid.onEnemyBid(1);

        verify(mOpponent, times(1)).onEnemyBid(2);
    }

    @Test
    public void WhenAndroidNotReady_AndOpponentBidsHigher__OpponentGoesOnlyOnce() {
        AiOpponent android = newAndroid(getBidder(1));

        android.onEnemyBid(2);

        verify(mOpponent, times(1)).go();
    }

    @Test
    public void when_android_cancelled__its_cancellable_delegate_is_called() {
        assertThat(mCancellableOpponent.cancelCalled, is(false));

        mAndroid.cancel();

        assertThat(mCancellableOpponent.cancelCalled, is(true));
    }

    private AiOpponent newAndroid(Bidder bidder) {
        Rules rules = mock(Rules.class);
        when(rules.getAllShipsSizes()).thenReturn(new int[]{1, 2, 3});
        AiOpponent aiOpponent = new AiOpponent(ANDROID_NAME, rules, mock(Bot.class), bidder, mock(Random.class));
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