package com.ivygames.morskoiboi.screen.win;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.android.gms.games.Player;
import com.ivygames.common.SignInListener;
import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.music.NullSoundBar;
import com.ivygames.common.music.SoundBar;
import com.ivygames.common.music.SoundBarFactory;
import com.ivygames.common.ui.BackPressListener;
import com.ivygames.morskoiboi.AnalyticsUtils;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Game.Type;
import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.model.ScoreStatistics;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;

import org.commons.logger.Ln;

import java.util.Collection;

public class WinScreen extends OnlineGameScreen implements BackPressListener, SignInListener {
    private static final String TAG = "WIN";

    private WinLayoutSmall mLayout;
    private final long mTime;

    private Session mSession;
    @NonNull
    private final Collection<Ship> mShips;
    @NonNull
    private final ScoreStatistics mStatistics;

    private final int mScores;

    @NonNull
    private final SoundBar mSoundBar;
    @NonNull
    private final ApiClient mApiClient = Dependencies.getApiClient();
    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();
    @NonNull
    private final AchievementsManager mAchievementsManager = Dependencies.getAchievementsManager();
    @NonNull
    private final ProgressManager mProgressManager = Dependencies.getProgressManager();
    @NonNull
    private final Rules mRules = Dependencies.getRules();
    private final boolean mOpponentSurrendered;

    public WinScreen(@NonNull BattleshipActivity parent,
                     @NonNull Game game,
                     @NonNull Session session,
                     @NonNull Collection<Ship> fleet,
                     @NonNull ScoreStatistics statistics,
                     boolean opponentSurrendered) {
        super(parent, game, session.opponent.getName());
        mSession = session;
        mShips = fleet;
        mStatistics = statistics;
        mOpponentSurrendered = opponentSurrendered;

        mSoundBar = createSoundBar(parent, "win.ogg");
        mSoundBar.play();

        mTime = statistics.getTimeSpent();
        mScores = mRules.calcTotalScores(fleet, mGame.getType(), mStatistics, opponentSurrendered);
        Ln.d("time spent in the game = " + mTime + "; scores = " + mScores + " incrementing played games counter");

        mSettings.incrementGamesPlayedCounter();
        Ln.v("fleet: " + mShips);

        if (mGame.getType() == Type.VS_ANDROID) {
            mAchievementsManager.processCombo(mStatistics.getCombo());
            mAchievementsManager.processShellsLeft(mStatistics.getShells());
            mAchievementsManager.processTimeSpent(mStatistics.getTimeSpent());
            mAchievementsManager.processShipsLeft(mShips);
            mAchievementsManager.processScores(mScores);
        }
        processScores();
    }

    private SoundBar createSoundBar(@NonNull Context context, @NonNull String soundName) {
        if (mSettings.isSoundOn()) {
            return SoundBarFactory.create(context, soundName);
        } else {
            return new NullSoundBar();
        }
    }

    @NonNull
    @Override
    public View getView() {
        return mLayout;
    }

    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (WinLayoutSmall) getLayoutInflater().inflate(R.layout.win, container, false);
        if (mGame.getType() == Type.VS_ANDROID) {
            mLayout.setDuration(mTime);
            mLayout.setTotalScore(mScores);
            mLayout.setSignInClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    UiEvent.send(UiEvent.GA_ACTION_SIGN_IN, "win");
                    mApiClient.connect();
                }
            });
        } else {
            Ln.d("online game - hiding scores", mGame.getType());
            mLayout.hideScores();
            mLayout.hideDuration();
            mLayout.hideSignInForAchievements();
        }

        if (mOpponentSurrendered) {
            Ln.d("opponent has surrendered - hiding \"Yes\" option");
            mLayout.hideYesNoButtons();
            mLayout.showContinueButton();
        } else {
            mLayout.setYesClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    UiEvent.send("continue", "win");
                    backToBoardSetup();
                }
            });
        }

        mLayout.setNoClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UiEvent.send("don't_continue" + (mOpponentSurrendered ? " because surrendered" : ""), "win");
                doNotContinue();
            }
        });

        mLayout.setShips(mShips);

        Ln.d(this + " screen created");
        return mLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGame.getType() == Type.VS_ANDROID && !mApiClient.isConnected()) {
            Ln.d("game vs Android, but client is not connected - show sign button");
            mLayout.showSignInForAchievements();
        } else {
            mLayout.hideSignInForAchievements();
        }
        mSoundBar.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSoundBar.pause();
    }

    @Override
    public void onSignInSucceeded() {
        Ln.d("sign in succeeded - hiding sign in button");
        mLayout.hideSignInForAchievements();
    }

    private void processScores() {
        int penalty = mSettings.getProgressPenalty();
        Ln.d("updating player's progress [" + mScores + "] for game type: " + mGame.getType() + "; penalty=" + penalty);
        int progressIncrement = mScores - penalty;
        if (progressIncrement > 0) {
            int oldScores = mSettings.getProgress().getScores();
            Progress newProgress = mSettings.incrementProgress(progressIncrement);
            boolean newRankAchieved = AnalyticsUtils.trackPromotionEvent(oldScores, newProgress.getScores());
            if (newRankAchieved) {
                mSettings.newRankAchieved(true);
            }
            mProgressManager.updateProgress(newProgress);
            mSettings.setProgressPenalty(0);
        } else {
            mSettings.setProgressPenalty(-progressIncrement);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGame.getType() == Type.VS_ANDROID) {
            if (mApiClient.isConnected()) {
                submitScore(mScores);
                sendAnalyticsForPlayersScores(mScores);
            } else {
                Ln.d("### client is not connected - could not submit scores!");
            }
        }

        mSoundBar.release();
        Ln.d(this + " destroyed - sound pool released");
    }

    @Override
    public void onBackPressed() {
        UiEvent.send(UiEvent.GA_ACTION_BACK, "win");
        doNotContinue();
    }

    private void backToBoardSetup() {
        Ln.d("getting back to " + BoardSetupScreen.TAG);
        setScreen(ScreenCreator.newBoardSetupScreen(mGame, mSession));
    }

    private void submitScore(int totalScores) {
        Ln.d("submitting scores: " + totalScores);
        mApiClient.submitScore(getString(R.string.leaderboard_normal), totalScores);
    }

    private void sendAnalyticsForPlayersScores(int totalScores) {
        Player currentPlayer = mApiClient.getCurrentPlayer();
        if (currentPlayer != null) {
            String playerName = currentPlayer.getDisplayName();
            String player = String.valueOf(playerName.hashCode());
            AnalyticsEvent.send("scores", player, totalScores);
        }
    }

    private void doNotContinue() {
        if (shouldNotifyOpponent() && !mOpponentSurrendered) {
            showWantToLeaveRoomDialog();
        } else {
            backToSelectGame();
        }
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }

}
