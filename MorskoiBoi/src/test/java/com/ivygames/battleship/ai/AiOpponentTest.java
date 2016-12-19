package com.ivygames.battleship.ai;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.Rules;
import com.ivygames.battleship.board.Board;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AiOpponentTest {

    private static final String ANDROID_NAME = "Android";
    private AiOpponent mAiOpponent;
    @Mock
    private Opponent mOpponent;

    private final DelegateOpponent mCancellableOpponent = new DelegateOpponent();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mCancellableOpponent.setOpponent(mOpponent);

        mAiOpponent = newBot(getBidder(1));
    }

    @Test
    public void WhenAndroidGoes__OpponentGetsShot() {
        mAiOpponent.onEnemyBid(1);

        mAiOpponent.go();

        verify(mOpponent, times(1)).onShotAt(any(Vector.class));
    }

    /**
     * This test if for when Player substituted by Ai in demos.
     */
    @Test
    public void WhenOpponentBidsWithHigherBid__OpponentGoes() {
        mAiOpponent.startBidding(1);

        mAiOpponent.onEnemyBid(2);

        verify(mOpponent, times(1)).go();
    }

    /**
     * This test if for when Player substituted by Ai in demos.
     */
    @Test
    public void WhenOpponentBidsWithLowerBid__OpponentGetsMyBid() {
        mAiOpponent.startBidding(2);

        mAiOpponent.onEnemyBid(1);

        verify(mOpponent, times(1)).onEnemyBid(2);
    }

    @Test
    public void WhenAndroidNotReady_AndOpponentBidsHigher__OpponentGoesOnlyOnce() {
        AiOpponent android = newBot(getBidder(1));

        android.onEnemyBid(2);

        verify(mOpponent, times(1)).go();
    }

    @Test
    public void WhenAndroidGoes1stTime_ItSetsTheBoard() {
        AiOpponent bot = newBot(getBidder(1));
        AiOpponent spy = spy(bot);
        spy.startBidding(1);

        spy.go();

        verify(spy, times(1)).setBoard(any(Board.class));
    }

    @Test
    public void WhenAndroidGoes2ndTime_TheBoardNotSet() {
        AiOpponent bot = newBot(getBidder(1));
        AiOpponent spy = spy(bot);
        spy.startBidding(1);

        spy.go();
        reset(spy);
        spy.go();

        verify(spy, never()).setBoard(any(Board.class));
    }

    @Test
    public void when_android_cancelled__its_cancellable_delegate_is_called() {
        assertThat(mCancellableOpponent.cancelCalled, is(false));

        mAiOpponent.cancel();

        assertThat(mCancellableOpponent.cancelCalled, is(true));
    }

    private AiOpponent newBot(Bidder bidder) {
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