package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.PlayerOpponent;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

/**
 * methods of this class are called in UI thread
 */
public class BackPressHandlingOpponent implements Opponent {

    @NonNull
    private final Opponent mDelegate;

    @NonNull
    private final FooBar mFoobar;
    private boolean mBackPressEnabled = true;

    public BackPressHandlingOpponent(@NonNull Opponent opponent, @NonNull FooBar fooBar) {
        mDelegate = opponent;
        mFoobar = fooBar;
    }

    @Override
    public void go() {
        mDelegate.go();
    }

    @Override
    public void onShotResult(@NonNull PokeResult result) {
        mDelegate.onShotResult(result);
        if (shipSank(result.ship)) {
            if (mFoobar.isEnemyBoardDefeated()) {
                disableBackPress();
            }
        }
    }

    private boolean shipSank(Ship ship) {
        return ship != null;
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        // If the opponent's version does not support board reveal, just switch screen in 3 seconds. In the later version of the protocol opponent
        // notifies about players defeat sending his board along.
        if (!mFoobar.versionSupportsBoardReveal()) {
            if (mFoobar.isPlayerBoardDefeated()) {
                disableBackPress();
            }
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
        disableBackPress();
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

    public boolean isBackPressEnabled() {
        return mBackPressEnabled;
    }

    private void disableBackPress() {
        Ln.d("disabling backpress");
        mBackPressEnabled = false;
    }

    @Override
    public String toString() {
        return mDelegate.toString();
    }

}
