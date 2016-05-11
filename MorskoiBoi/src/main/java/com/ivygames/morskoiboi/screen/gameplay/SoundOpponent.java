package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

/**
 * methods of this class are called in UI thread
 */
public class SoundOpponent implements Opponent {

    @NonNull
    private final Opponent mDelegate;
    @NonNull
    private final GameplaySoundManager mGameplaySounds;
    @NonNull
    private final FooBar mFoobar;

    public SoundOpponent(@NonNull Opponent opponent, @NonNull GameplaySoundManager sounds, @NonNull FooBar fooBar) {
        mDelegate = opponent;
        mGameplaySounds = sounds;
        mFoobar = fooBar;
    }

    @Override
    public void go() {
        mDelegate.go();
    }

    @Override
    public void onShotResult(@NonNull final PokeResult result) {
        if (shipSank(result.ship)) {
            mGameplaySounds.playKillSound();
        } else if (result.cell.isMiss()) {
            mGameplaySounds.playSplash();
        } else {
            mGameplaySounds.playHitSound();
        }

        mDelegate.onShotResult(result);
    }

    private boolean shipSank(Ship ship) {
        return ship != null;
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        PokeResult result = mFoobar.getLastShotResult();

        if (shipSank(result.ship)) {
            mGameplaySounds.playKillSound();
        } else if (result.cell.isMiss()) {
            mGameplaySounds.playSplash();
        } else {
            mGameplaySounds.playHitSound();
        }

        mDelegate.onShotAt(aim);
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        mDelegate.setOpponent(opponent);
    }

    @Override
    public void onEnemyBid(final int bid) {
        mDelegate.onEnemyBid(bid);
    }

    @Override
    public String getName() {
        return mDelegate.getName();
    }

    @Override
    public void onLost(@NonNull Board board) {
        mDelegate.onLost(board);
    }

    @Override
    public void setOpponentVersion(int ver) {
        mDelegate.setOpponentVersion(ver);
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        mDelegate.onNewMessage(text);
    }

    @Override
    public String toString() {
        return mDelegate.toString();
    }

}
