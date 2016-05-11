package com.ivygames.morskoiboi.screen.gameplay;

import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.Bidder;
import com.ivygames.morskoiboi.GameHandler;
import com.ivygames.morskoiboi.PlayerOpponent;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.VibratorFacade;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.utils.GameUtils;

import org.commons.logger.Ln;
import org.json.JSONException;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

/**
 * methods of this class are called in UI thread
 */
public abstract class UiProxyOpponent implements Opponent {
//    private static final long LOST_GAME_WO_REVEAL_DELAY = 3000; // milliseconds
//    private static final long LOST_GAME_WITH_REVEAL_DELAY = 5000; // milliseconds
//    private static final int VIBRATION_ON_KILL = 500;
//    private static final int WON_GAME_DELAY = 3000; // milliseconds
//
//    @NonNull
//    private final PlayerOpponent mPlayer;
//    private GameplayScreen mScreen;
//
//    @NonNull
//    private final VibratorFacade mVibrator;
//
//    @NonNull
//    private final Runnable mShowLostScreenCommand = new Runnable() {
//
//        @Override
//        public void run() {
//            setScreen(GameHandler.newLostScreen(mGame));
//        }
//    };
//
//    public UiProxyOpponent(@NonNull PlayerOpponent opponent, @NonNull GameplayScreen screen) {
//        mPlayer = opponent;
//        mScreen = screen;
//        mVibrator = new VibratorFacade(screen.parent());
//    }
//
//    @Override
//    public void go() {
//        mPlayer.go();
//        mParent.startService(getServiceIntent(getString(R.string.your_turn)));
//        mLayout.playerTurn();
//        mGameIsOn = true;
//        mStartTime = SystemClock.elapsedRealtime();
//        mMyTurn = true;
//
//        if (mGame.getType() != Game.Type.VS_ANDROID || isResumed()) {
//            Ln.d("player's turn - starting timer");
//            mTimerController.start(); // for all practical scenarios - start will only be called from here
//        } else {
//            Ln.d("player's turn, but screen is paused - DO NOT START TIMER");
//        }
//        hideOpponentSettingBoardNotification();
//    }
//
//    private void hideOpponentSettingBoardNotification() {
//        Ln.d("hiding \"opponent setting board\" notification");
//        mLayout.hideOpponentSettingBoardNotification();
//    }
//
//    @Override
//    public void onShotResult(@NonNull final PokeResult result) {
//        stopDetectingShotTimeout();
//
//        Ln.v(result);
//        mPlayer.onShotResult(result);
//        mStatistics.updateWithNewShot(result.ship, result.cell);
//
//        mLayout.removeAim();
//        mLayout.setShotResult(result);
//
//        mLayout.invalidateEnemyBoard();
//
//        // TODO: call this only if ship sank
//        updateEnemyStatus();
//
//        if (shipSank(result.ship)) {
//            Ln.v("enemy ship is sunk!! - shake enemy board");
//            mLayout.shakeEnemyBoard();
//            mGameplaySounds.playKillSound();
//            vibrate(VIBRATION_ON_KILL);
//
//            if (mRules.isItDefeatedBoard(mEnemyPublicBoard)) {
//                Ln.d("enemy has lost!!!");
//                disableBackPress();
//
//                mStatistics.setTimeSpent(mUnlockedTime);
//                mEnemy.onLost(mPlayerPrivateBoard);
//                resetPlayer();
//
//                mLayout.win();
//                showWinScreenDelayed();
//            }
//        } else if (result.cell.isMiss()) {
//            Ln.d(mPlayer + ": I missed - passing the turn to " + mEnemy);
//            mGameplaySounds.playSplash();
//            showOpponentTurn();
//            startDetectingTurnTimeout();
//            mEnemy.go();
//        } else {
//            Ln.v("it's a hit! - player continues");
//            mGameplaySounds.playHitSound();
//        }
//    }
//
//    private boolean shipSank(Ship ship) {
//        return ship != null;
//    }
//
//    @Override
//    public void onShotAt(@NonNull Vector2 aim) {
//        stopDetectingTurnTimeout();
//        PokeResult result = mPlayer.createResultForShootingAt(aim);
//        mPlayer.onShotAtForResult(result);
//        Ln.v(this + ": hitting my board at " + aim + " yields result: " + result);
//
//        updateMyStatus();
//
//        if (shipSank(result.ship)) {
//            // Ln.v("player's ship is sunk: " + result);
//            mLayout.shakePlayerBoard();
//            mGameplaySounds.playKillSound();
//            vibrate(VIBRATION_ON_KILL);
//        } else if (result.cell.isMiss()) {
//            // Ln.v("opponent misses: " + result);
//            mGameplaySounds.playSplash();
//        } else {
//            // Ln.v("player's ship is hit: " + result);
//            mLayout.invalidatePlayerBoard();
//            mGameplaySounds.playHitSound();
//        }
//
//        // If the opponent's version does not support board reveal, just switch screen in 3 seconds. In the later version of the protocol opponent
//        // notifies about players defeat sending his board along.
//        if (!versionSupportsBoardReveal()) {
//            if (mRules.isItDefeatedBoard(mPlayerPrivateBoard)) {
//                Ln.v("opponent version doesn't support board reveal = " + mPlayer.getOpponentVersion());
//                AnalyticsEvent.send("reveal_not_supported");
//                resetPlayer();
//                disableBackPress();
//                mLayout.lost();
//                showLostScreenDelayed(LOST_GAME_WO_REVEAL_DELAY);
//            }
//        }
//    }
//
//    private boolean versionSupportsBoardReveal() {
//        return mPlayer.getOpponentVersion() >= GameUtils.PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL;
//    }
//
//    @Override
//    public void setOpponent(@NonNull Opponent opponent) {
//        // do nothing
//    }
//
//    @Override
//    public void onEnemyBid(final int bid) {
//        Ln.d("opponent's bid received: " + bid);
//        hideOpponentSettingBoardNotification();
//        mPlayer.onEnemyBid(bid);
//        if (mPlayer.isOpponentTurn()) {
//            showOpponentTurn();
//            startDetectingTurnTimeout();
//        }
//    }
//
//    @Override
//    public String getName() {
//        return mPlayer.getName();
//    }
//
//    @Override
//    public void onLost(@NonNull Board board) {
//        if (!mRules.isItDefeatedBoard(mPlayerPrivateBoard)) {
//            Ln.v("player private board: " + mPlayerPrivateBoard);
//            reportException("lost while not defeated");
//        }
//        // revealing the enemy board
//        mEnemyPublicBoard = board;
//        updateEnemyStatus();
//        mLayout.setEnemyBoard(board);
//        resetPlayer();
//        disableBackPress();
//        mLayout.lost();
//        showLostScreenDelayed(LOST_GAME_WITH_REVEAL_DELAY);
//    }
//
//    private void resetPlayer() {
//        Ln.d("match is over - blocking the player for further messages until start of the next round");
//        copyPlayerBoard();
//        copyEnemyBoard();
//        mPlayer.reset(new Bidder().newBid());
//        // need to de-associate UI from the enemy opponent
//        mEnemy.setOpponent(mPlayer);
//    }
//
//    @Override
//    public void setOpponentVersion(int ver) {
//        mPlayer.setOpponentVersion(ver);
//    }
//
//    @Override
//    public void onNewMessage(@NonNull String text) {
//        mChatAdapter.add(ChatMessage.newEnemyMessage(text));
//        mPlayer.onNewMessage(text);
//    }
//
//    private void showLostScreenDelayed(long mseconds) {
//        mUiThreadHandler.postDelayed(mShowLostScreenCommand, mseconds);
//    }
//
//    private void disableBackPress() {
//        Ln.d("disabling backpress");
//        mBackPressEnabled = false;
//    }
//
//    private void vibrate(int duration) {
//        if (mSettings.isVibrationOn() && isResumed()) {
//            mVibrator.vibrate(duration);
//        }
//    }
//
//    private void showWinScreenDelayed() {
//        mUiThreadHandler.postDelayed(new ShowWinCommand(false), WON_GAME_DELAY);
//    }
//
//    private void copyPlayerBoard() {
//        try {
//            mPlayerPrivateBoard = Board.fromJson(mPlayerPrivateBoard.toJson());
//        } catch (JSONException je) {
//            Ln.e(je, "could not copy player's board");
//        }
//    }
//
//    private void copyEnemyBoard() {
//        try {
//            mEnemyPublicBoard = Board.fromJson(mEnemyPublicBoard.toJson());
//        } catch (JSONException je) {
//            Ln.e(je, "could not copy enemy's board");
//        }
//    }
//
//    @Override
//    public String toString() {
//        return mPlayer.toString();
//    }

}
