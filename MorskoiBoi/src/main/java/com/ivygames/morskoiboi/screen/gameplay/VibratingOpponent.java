package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.VibratorFacade;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.BattleshipScreen;

import org.commons.logger.Ln;

/**
 * methods of this class are called in UI thread
 */
public class VibratingOpponent implements Opponent {
    private static final int VIBRATION_ON_KILL = 500;

    @NonNull
    private final Opponent mPlayer;
    @NonNull
    private final VibratorFacade mVibrator;
    @NonNull
    private final FooBar mFoobar;
    @NonNull
    private final BattleshipScreen mScreen;
    private GameSettings mSettings;

    public VibratingOpponent(@NonNull Opponent opponent,
                             @NonNull VibratorFacade vibrator,
                             @NonNull FooBar fooBar,
                             @NonNull BattleshipScreen screen,
                             @NonNull GameSettings settings) {
        mPlayer = opponent;
        mVibrator = vibrator;
        mFoobar = fooBar;
        mScreen = screen;
        mSettings = settings;
    }

    @Override
    public void go() {
        mPlayer.go();
    }

    @Override
    public void onShotResult(@NonNull final PokeResult result) {
        if (shipSank(result.ship)) {
            Ln.v("enemy ship is sunk!! - shake enemy board");
            vibrate(VIBRATION_ON_KILL);
        }
        mPlayer.onShotResult(result);
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        PokeResult result = mFoobar.getLastShotResult();

        if (shipSank(result.ship)) {
            vibrate(VIBRATION_ON_KILL);
        }

        mPlayer.onShotAt(aim);
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        mPlayer.setOpponent(opponent);
    }

    @Override
    public void onEnemyBid(final int bid) {
        mPlayer.onEnemyBid(bid);
    }

    @Override
    public String getName() {
        return mPlayer.getName();
    }

    @Override
    public void onLost(@NonNull Board board) {
        mPlayer.onLost(board);
    }

    @Override
    public void setOpponentVersion(int ver) {
        mPlayer.setOpponentVersion(ver);
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        mPlayer.onNewMessage(text);
    }

    @Override
    public String toString() {
        return mPlayer.toString();
    }

    private void vibrate(int duration) {
        if (mSettings.isVibrationOn() && mScreen.isResumed()) {
            mVibrator.vibrate(duration);
        }
    }

    private boolean shipSank(Ship ship) {
        return ship != null;
    }

}
