package com.ivygames.morskoiboi.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.SoundBar;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.achievement.AchievementsUtils;
import com.ivygames.morskoiboi.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Game.Type;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.rt.InternetGame;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.ui.BattleshipActivity.SignInListener;
import com.ivygames.morskoiboi.ui.view.WinLayoutSmall;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import java.util.Collection;

public class WinScreen extends OnlineGameScreen implements BackPressListener, SignInListener {
    private static final String TAG = "WIN";
    private static final String DIALOG = FragmentAlertDialog.TAG;

    public static final String EXTRA_OPPONENT_SURRENDERED = "EXTRA_OPPONENT_SURRENDERED";
    public static final String EXTRA_BOARD = "EXTRA_BOARD";

    private Game mGame;
    private WinLayoutSmall mLayout;
    private long mTime;

    private Collection<Ship> mShips;

    private int mScores;

    private SoundBar mSoundBar;
    private Bundle mArgs;

    @Override
    public void onCreate() {
        super.onCreate();

        Board board = Board.fromJson(getArguments().getString(EXTRA_BOARD));

        mGame = Model.instance.game;

        mSoundBar = new SoundBar(getActivity().getAssets(), "win.ogg");

        mTime = mGame.getTimeSpent();
        mShips = board.getShips();
        mScores = mGame.calcTotalScores(mShips);
        Ln.d("time spent in the game = " + mTime + "; scores = " + mScores + " incrementing played games counter");

        GameSettings.get().incrementGamesPlayedCounter();
        Ln.v("fleet: " + mShips);
    }

    private Bundle getArguments() {
        return mArgs;
    }

    @Override
    public View getView() {
        return mLayout;
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (WinLayoutSmall) getLayoutInflater().inflate(R.layout.win, container, false);
        if (mGame.getType() != Type.VS_ANDROID) {
            Ln.d("online game - hiding scores", mGame.getType());
            mLayout.hideScorables();
        }

        mLayout.setYesClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mGaTracker.send(new UiEvent("continue", "win").build());
                backToBoardSetup();
            }
        });

        mLayout.setNoClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mGaTracker.send(new UiEvent("dont_continue", "win").build());
                doNotContinue();
            }
        });

        mLayout.setTime(mTime);
        mLayout.setShips(mShips);
        mLayout.setTotalScore(mScores);

        mLayout.setSignInClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mGaTracker.send(new UiEvent(GameConstants.GA_ACTION_SIGN_IN, "win").build());
                mApiClient.connect();
            }
        });

        if (hasOpponentSurrendered()) {
            Ln.d("opponent has surrendered - hiding continue option");
            mLayout.opponentSurrendered();
        }

        Ln.d(this + " screen created");
        updateProgress();
        return mLayout;
    }

    private boolean hasOpponentSurrendered() {
        return getArguments().getBoolean(EXTRA_OPPONENT_SURRENDERED);
    }

    @Override
    public void onResume() {
        super.onResume();
        Ln.d(this + " is fully visible - resume sounds");
        mSoundBar.autoResume();

        if (mGame.getType() == Type.VS_ANDROID && !mApiClient.isConnected()) {
            Ln.d("game vs Android, but client is not connected - show sign button");
            mLayout.showSignInBar();
        } else {
            mLayout.hideSignInBar();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Ln.d(this + " is partially obscured - pause sounds");
        mSoundBar.autoPause();
    }

    @Override
    public void onSignInSucceeded() {
        Ln.d("sign in succeeded - hiding sign in button");
        mLayout.hideSignInBar();
    }

    private void updateProgress() {
        int progress;
        if (mGame.getType() == Type.VS_ANDROID) {
            if (GameConstants.IS_TEST_MODE) {
                Ln.i("game is in test mode - achievements not updated");
            } else {
                new AchievementsManager(mApiClient, mGaTracker).processAchievements(mGame, mShips);
            }
            progress = mScores * AchievementsManager.NORMAL_DIFFICULTY_PROGRESS_FACTOR;
        } else if (mGame.getType() == Type.INTERNET) {
            progress = InternetGame.WIN_PROGRESS_POINTS;
        } else {
            progress = BluetoothGame.WIN_PROGRESS_POINTS;
        }

        if (hasOpponentSurrendered()) {
            progress = progress / 2;
        }

        int penalty = GameSettings.get().getProgressPenalty();
        Ln.d("updating player's progress [" + progress + "] for game type: " + mGame.getType() + "; penalty=" + penalty);
        int progressIncrement = progress - penalty;
        if (progressIncrement > 0) {
            AchievementsUtils.incrementProgress(progressIncrement, mApiClient, mGaTracker);
            GameSettings.get().setProgressPenalty(0);
        } else {
            GameSettings.get().setProgressPenalty(-progressIncrement);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGame.getType() == Type.VS_ANDROID) {
            if (GameConstants.IS_TEST_MODE) {
                Ln.i("game is in the test mode - scores are not submitted");
            } else {
                if (mApiClient.isConnected()) {
                    submitScore(mScores);
                } else {
                    Ln.d("### client is not connected - could not submit scores!");
                }
            }
        }

        mSoundBar.release();
        Ln.d(this + " destroyed - sound pool released");
    }

    @Override
    public void onBackPressed() {
        mGaTracker.send(new UiEvent(GameConstants.GA_ACTION_BACK, "win").build());
        doNotContinue();
    }

    private void backToBoardSetup() {
        Ln.d("getting back to " + BoardSetupScreen.TAG);
        Model.instance.game.clearState();
        mParent.setScreen(new BoardSetupScreen());
    }

    private void showWantToLeaveRoomDialog() {
        String displayName = Model.instance.opponent.getName();
        String message = getString(R.string.want_to_leave_room, displayName);
        DialogUtils.newOkCancelDialog(message, new BackToSelectGameCommand(mParent)).show(mFm, DIALOG);
    }

    private void submitScore(int totalScores) {
        Ln.d("submitting scores: " + totalScores);
        Games.Leaderboards.submitScore(mApiClient, getString(R.string.leaderboard_normal), totalScores);

        Player currentPlayer = Games.Players.getCurrentPlayer(mApiClient);
        if (currentPlayer != null) {
            String playerName = currentPlayer.getDisplayName();
            String player = String.valueOf(playerName.hashCode());
            mGaTracker.send(new AnalyticsEvent("scores", player, totalScores).build());
        }
    }

    private void doNotContinue() {
        if (shouldNotifyOpponent() && !hasOpponentSurrendered()) {
            showWantToLeaveRoomDialog();
        } else {
            new BackToSelectGameCommand(mParent).run();
        }
    }

    public void setArguments(Bundle args) {
        mArgs = args;
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }

}
