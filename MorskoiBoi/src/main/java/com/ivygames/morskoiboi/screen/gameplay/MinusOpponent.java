package com.ivygames.morskoiboi.screen.gameplay;

import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.Bidder;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;
import org.json.JSONException;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

/**
 * methods of this class are called in UI thread
 */
public abstract class MinusOpponent implements Opponent {
//    private static final long LOST_GAME_WO_REVEAL_DELAY = 3000; // milliseconds
//    private static final long LOST_GAME_WITH_REVEAL_DELAY = 5000; // milliseconds
//    private static final int WON_GAME_DELAY = 3000; // milliseconds
//
//    @NonNull
//    private final Opponent mDelegate;
//
//    @NonNull
//    private final FooBar mFooBar;
//
//
//    public MinusOpponent(@NonNull Opponent opponent, @NonNull FooBar fooBar) {
//        mDelegate = opponent;
//        mFooBar = fooBar;
//    }
//
//    @Override
//    public void go() {
//        mDelegate.go();
//        mParent.startService(getServiceIntent(getString(R.string.your_turn)));
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
//    }
//
//    @Override
//    public void onShotResult(@NonNull final PokeResult result) {
//        stopDetectingShotTimeout();
//
//        Ln.v(result);
//        mDelegate.onShotResult(result);
//        mStatistics.updateWithNewShot(result.ship, result.cell);
//
//        if (shipSank(result.ship)) {
//            if (mFooBar.isEnemyBoardDefeated()) {
//                Ln.d("enemy has lost!!!");
//                mStatistics.setTimeSpent(mUnlockedTime);
//                mEnemy.onLost(mPlayerPrivateBoard);
//                resetPlayer();
//
//                showWinScreenDelayed();
//            }
//        } else if (result.cell.isMiss()) {
//            Ln.d(mDelegate + ": I missed - passing the turn to " + mEnemy);
//            mParent.startService(getServiceIntent(getString(R.string.opponent_s_turn)));
//            updateUnlockedTime();
//            mMyTurn = false;
//
//            startDetectingTurnTimeout();
//            mEnemy.go();
//        }
//    }
//
//    @Override
//    public void onShotAt(@NonNull Vector2 aim) {
//        stopDetectingTurnTimeout();
//
//        // If the opponent's version does not support board reveal, just switch screen in 3 seconds. In the later version of the protocol opponent
//        // notifies about players defeat sending his board along.
//        if (!mFooBar.versionSupportsBoardReveal()) {
//            if (mFooBar.isPlayerBoardDefeated()) {
//                Ln.v("opponent version doesn't support board reveal = " + mDelegate.getOpponentVersion());
//                AnalyticsEvent.send("reveal_not_supported");
//                resetPlayer();
//                showLostScreenDelayed(LOST_GAME_WO_REVEAL_DELAY);
//            }
//        }
//    }
//
//    @Override
//    public void setOpponent(@NonNull Opponent opponent) {
//        mDelegate.setOpponent(opponent);
//    }
//
//    @Override
//    public void onEnemyBid(final int bid) {
//        Ln.d("opponent's bid received: " + bid);
//        hideOpponentSettingBoardNotification();
//        mDelegate.onEnemyBid(bid);
//        if (mFooBar.isOpponentTurn()) {
//            mParent.startService(getServiceIntent(getString(R.string.opponent_s_turn)));
//            updateUnlockedTime();
//            mMyTurn = false;
//
//            startDetectingTurnTimeout();
//        }
//    }
//
//    @Override
//    public String getName() {
//        return mDelegate.getName();
//    }
//
//    @Override
//    public void onLost(@NonNull Board board) {
//        if (!mFooBar.isPlayerBoardDefeated()) {
//            Ln.v("player private board: " + mPlayerPrivateBoard);
//            reportException("lost while not defeated");
//        }
//        // revealing the enemy board
//        mEnemyPublicBoard = board;
//        updateEnemyStatus();
//        resetPlayer();
//        showLostScreenDelayed(LOST_GAME_WITH_REVEAL_DELAY);
//    }
//
//    private void resetPlayer() {
//        Ln.d("match is over - blocking the player for further messages until start of the next round");
//        copyPlayerBoard();
//        copyEnemyBoard();
//        mDelegate.reset(new Bidder().newBid());
//        // need to de-associate UI from the enemy opponent
//        mEnemy.setOpponent(mDelegate);
//    }
//
//    @Override
//    public void setOpponentVersion(int ver) {
//        mDelegate.setOpponentVersion(ver);
//    }
//
//    @Override
//    public void onNewMessage(@NonNull String text) {
//        mDelegate.onNewMessage(text);
//    }
//
//    private void showLostScreenDelayed(long mseconds) {
//        mUiThreadHandler.postDelayed(mShowLostScreenCommand, mseconds);
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
//        return mDelegate.toString();
//    }
//
//    private boolean shipSank(Ship ship) {
//        return ship != null;
//    }

}
