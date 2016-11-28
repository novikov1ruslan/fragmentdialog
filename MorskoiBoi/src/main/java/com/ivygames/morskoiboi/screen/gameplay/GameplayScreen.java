package com.ivygames.morskoiboi.screen.gameplay;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.ShipUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.common.DebugUtils;
import com.ivygames.common.VibratorWrapper;
import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.dialog.SimpleActionDialog;
import com.ivygames.common.game.Bidder;
import com.ivygames.common.multiplayer.MultiplayerEvent;
import com.ivygames.common.timer.TurnListener;
import com.ivygames.common.timer.TurnTimerController;
import com.ivygames.common.ui.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.OrientationBuilder;
import com.ivygames.morskoiboi.PlayerCallback;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.ai.Cancellable;
import com.ivygames.morskoiboi.config.ScoresUtils;
import com.ivygames.battleship.ChatMessage;
import com.ivygames.morskoiboi.Game;
import com.ivygames.morskoiboi.ScoreStatistics;
import com.ivygames.morskoiboi.rt.InternetService;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class GameplayScreen extends OnlineGameScreen implements BackPressListener {
    private static final String DIALOG = FragmentAlertDialog.TAG;

    private static final int START_DELAY = 3000;
    private static final long WON_GAME_DELAY = 3000; // milliseconds
    private static final long LOST_GAME_DELAY = 5000; // milliseconds

    private static final int VIBRATION_ON_KILL = 500;

    private static final int SHOT_HANG_DETECTION_TIMEOUT = 10000; // milliseconds
    private static final int TURN_HANG_DETECTION_TIMEOUT = 60000; // milliseconds

    private static final int ALARM_TIME_SECONDS = 10;


    @NonNull
    private final Handler mUiThreadHandler = new Handler(Looper.getMainLooper());
    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();
    @NonNull
    private final Rules mRules = Dependencies.getRules();
    @NonNull
    private final Random mRandom = Dependencies.getRandom();
    @NonNull
    private final Runnable mShowLostScreenCommand = new ShowLostScreenCommand();
    @NonNull
    private final Runnable mShotHangDetectionTask = new ShotHangDetectionTask();
    @NonNull
    private final Runnable mTurnHangDetectionTask = new TurnHangDetectionTask();
    @NonNull
    private final ScoreStatistics mStatistics = new ScoreStatistics();

    @NonNull
    private final PlayerOpponent mPlayer;
    @NonNull
    private final Opponent mEnemy;
    @NonNull
    private final VibratorWrapper mVibrator;
    @NonNull
    private final Board mEnemyPublicBoard;
    @NonNull
    private final Board mPlayerPrivateBoard;
    @NonNull
    private final ChatAdapter mChatAdapter;
    @NonNull
    private final GameplaySoundManager mGameplaySounds;
    @NonNull
    private final Session mSession;
    @NonNull
    private final TurnTimerController mTimerController;
    @NonNull
    private final Intent mMatchStatusIntent;

    private long mUnlockedTime;
    private long mStartTime;
    private boolean mGameIsOn;
    private GameplayLayoutInterface mLayout;
    private boolean mBackPressEnabled = true;
    private UiPlayerCallback mUiPlayerCallback;

    public GameplayScreen(@NonNull BattleshipActivity parent, @NonNull Game game, @NonNull Session session,
                          @NonNull TurnTimerController timerController) {
        super(parent, game, session.opponent.getName());
        mSession = session;
        mTimerController = timerController;

        Dependencies.getAdProvider().needToShowAfterPlayAd();
        mGameplaySounds = new GameplayScreenSounds((AudioManager) parent.getSystemService(Context.AUDIO_SERVICE), this, mSettings);
        mGameplaySounds.prepareSoundPool(parent.getAssets());
        mPlayer = session.player;
        mEnemy = session.opponent;
        mEnemyPublicBoard = mPlayer.getEnemyBoard();
        mPlayerPrivateBoard = mPlayer.getBoard();

        mVibrator = new VibratorWrapper((Vibrator) parent.getSystemService(Context.VIBRATOR_SERVICE));

        mMatchStatusIntent = new Intent(parent, InternetService.class);
        mMatchStatusIntent.putExtra(InternetService.EXTRA_CONTENT_TITLE, getString(R.string.match_against) + " " + mEnemy.getName());
        mChatAdapter = new ChatAdapter(getLayoutInflater());
        mTimerController.setListener(new TurnTimerListener());
    }

    @NonNull
    @Override
    public View getView() {
        return (View) mLayout;
    }

    private Intent getServiceIntent(String contentText) {
        mMatchStatusIntent.putExtra(InternetService.EXTRA_CONTENT_TEXT, contentText);
        return mMatchStatusIntent;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (GameplayLayoutInterface) getLayoutInflater().inflate(R.layout.gameplay, container, false).findViewById(R.id.gameplay_layout);
        mLayout.setShipsSizes(mRules.getAllShipsSizes());
        if (!mGame.isRemote()) {
            Ln.v("not internet game - hide chat button");
            mLayout.hideChatButton();
        }

        mLayout.setLayoutListener(new GameplayLayoutListenerImpl());

        // --- for tablet ---
        mLayout.setSound(mSettings.isSoundOn());
        // ------------------

        updateMyStatus();
        mLayout.setPlayerBoard(mPlayerPrivateBoard);
        updateEnemyStatus();
        mLayout.setEnemyBoard(mEnemyPublicBoard);
        if (mRules.allowAdjacentShips()) {
            mLayout.allowAdjacentShips();
        }
        mLayout.setAlarmTime(ALARM_TIME_SECONDS * 1000);

        mLayout.setPlayerName(mPlayer.getName());
        mLayout.setEnemyName(mEnemy.getName());

        if (mPlayer.isOpponentReady()) {
            showOpponentTurn();
        } else {
            Ln.d("opponent is still setting board");
            showOpponentSettingBoardNote();
        }

        mUiPlayerCallback = new UiPlayerCallback();
        mPlayer.registerCallback(mUiPlayerCallback);

        mLayout.setShotListener(new BoardShotListener(mEnemy, mGameplaySounds));

        Ln.d("screen is fully created - start bidding in " + START_DELAY + "ms");
        mUiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayer.startBidding(new Bidder(mRandom).newBid());
            }
        }, START_DELAY);

        return (View) mLayout;
    }

    private void showOpponentSettingBoardNote() {
        String message = getString(R.string.opponent_setting_board, mEnemy.getName());
        mLayout.showOpponentSettingBoardNote(message);
    }

    @Override
    public void onPause() {
        super.onPause();
        Ln.d(this + " is partially obscured - pausing sounds");
        if (mGame.isPausable()) {
            // timer is not running if it is not player's turn, but cancel it just in case
            mTimerController.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Ln.d(this + " is fully visible - resuming sounds");
        if (mGame.isPausable() && !mLayout.isLocked()) {
            Ln.v("showing pause dialog");
            showPauseDialog();
        }
    }

    private void showPauseDialog() {
        mLayout.lock();
        updateUnlockedTime();
        Runnable pauseCommand = new Runnable() {

            @Override
            public void run() {
                Ln.v("continue pressed");
                if (!isResumed()) {
                    Ln.w("timer resume cancelled due to background");
                    return;
                }
                // Dialog is shown only if it is player's turn.
                // So it is safe to resume timer
                mTimerController.start();
                // before dialog was displayed, layout has been locked
                mLayout.unLock();
                mGameIsOn = true;
                mStartTime = SystemClock.elapsedRealtime();
            }
        };
        SimpleActionDialog.create(R.string.pause, R.string.continue_str, pauseCommand).show(mFm, DIALOG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Player can receive messages related to the new game round (bid).
        // Player can receive more messages after this screen is destroyed.
        // So UI of this screen should not be affected.
        mPlayer.unregisterCallback(mUiPlayerCallback);

        Fragment fragment = mFm.findFragmentByTag(DIALOG);
        if (fragment != null && !mParent.isFinishing()) {
            Ln.v("removing dialog: " + fragment);
            mFm.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }

        Crouton.cancelAllCroutons();

        mTimerController.stop();
        if (mEnemy instanceof Cancellable) {
            ((Cancellable) mEnemy).cancel();
        }
        mGameplaySounds.release();
        mParent.stopService(mMatchStatusIntent);

        stopDetectingShotTimeout();
        stopDetectingTurnTimeout();
        Ln.d(this + " screen destroyed");
    }

    @Override
    public void onConnectionLost(@NonNull MultiplayerEvent event) {
        mTimerController.stop();
        mParent.stopService(mMatchStatusIntent);
        if (event == MultiplayerEvent.OPPONENT_LEFT && mPlayer.isOpponentReady()) {
            Ln.d("opponent surrendered - notifying player, (shortly game will finish)");
            AnalyticsEvent.send("opponent_surrendered");
            showOpponentSurrenderedDialog(mPlayerPrivateBoard.getShips());
        } else {
            super.onConnectionLost(event);
        }

        stopDetectingShotTimeout();
        stopDetectingTurnTimeout();
    }

    @Override
    public void onBackPressed() {
        if (!mBackPressEnabled) {
            Ln.d("backpress temporarily disabled");
            return;
        }

        if (mGame.shouldNotifyOpponent()) {
            if (mPlayer.isOpponentReady()) {
                Ln.d("Captain, do you really want to surrender?");
                showSurrenderDialog();
            } else {
                Ln.d("Do you want to abandon the game with XXX?");
                showWantToLeaveRoomDialog();
            }
        } else {
            Ln.d("Are you sure you want to abandon the battle?");
            showAbandonGameDialog();
        }
    }

    private void showAbandonGameDialog() {
        showLeaveGameDialog(getString(R.string.abandon_game_question));
    }

    private void showLeaveGameDialog(String message) {
        new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                UiEvent.send("left_from_game", "ok");
                Ln.d("player decided to leave the game");
                backToSelectGame();
            }
        }).setNegativeButton(R.string.cancel).create().show(mFm, DIALOG);
    }

    private void showSurrenderDialog() {
        final int penalty = ScoresUtils.calcSurrenderPenalty(mRules.getAllShipsSizes(), mPlayerPrivateBoard.getShips());
        String message = getString(R.string.surrender_question, -penalty);

        new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                UiEvent.send("surrender", "ok");
                Ln.d("player chose to surrender");
                surrender(penalty);
            }
        }).setNegativeButton(R.string.cancel).create().show(mFm, DIALOG);
    }

    private class BoardShotListener implements ShotListener {
        @NonNull
        private final Opponent mEnemy;
        private boolean debug_aiming_started;
        @NonNull
        private final GameplaySoundManager mGameplaySounds;

        BoardShotListener(@NonNull Opponent opponent, @NonNull GameplaySoundManager gameplaySounds) {
            mEnemy = opponent;
            mGameplaySounds = gameplaySounds;
        }

        @Override
        public void onAimingFinished(int i, int j) {
            Ln.v("aiming finished: (" + i + ", " + j + ")");
            debug_aiming_started = false;
            mGameplaySounds.stopKantropSound();

            if (!BoardUtils.contains(i, j)) {
                Ln.w("pressing outside the board: " + i + "," + j);
                return;
            }

            Cell cell = mEnemyPublicBoard.getCell(i, j);
            if (!(cell == Cell.EMPTY)) {
                Ln.d(cell + " is not empty");
                // TODO: play sound
                return;
            }
            // ----------------------------------
            mTimerController.stop();

            Vector aim = Vector.get(i, j);
            Ln.d("shooting at: " + aim + cell);
            mLayout.lock();
            updateUnlockedTime();

            startDetectingShotTimeout();
            mLayout.setAim(aim);
            mEnemy.onShotAt(aim);

            mGameplaySounds.playWhistleSound();
        }

        @Override
        public void onAimingStarted() {
            Ln.v("aiming started");
            if (debug_aiming_started) {
                reportException("aiming started");
            } else {
                debug_aiming_started = true;
                mGameplaySounds.playKantrop();
            }
        }

    }

    private void updateUnlockedTime() {
        if (!mGameIsOn) {
            return;
        }
        long d = SystemClock.elapsedRealtime() - mStartTime;
        mUnlockedTime += d;
        Ln.v("d = " + d + ", mUnlockedTime=" + mUnlockedTime);
    }

    private void showOpponentTurn() {
        mParent.startService(getServiceIntent(getString(R.string.opponent_s_turn)));
        mLayout.enemyTurn();
        updateUnlockedTime();
    }

    private class UiPlayerCallback implements PlayerCallback {

        @Override
        public void onPlayerShotResult(@NonNull ShotResult result) {
            stopDetectingShotTimeout();

            mStatistics.updateWithNewShot(result.isaKill(), result.cell);

            mLayout.removeAim();
            mLayout.setShotResult(result);

            updateEnemyStatus();
        }

        @Override
        public void onWin() {
            Ln.d("enemy has lost!!!");
            disableBackPress();

            mStatistics.setTimeSpent(mUnlockedTime);
            mLayout.win();

            showWinScreenDelayed(new ArrayList<>(mPlayerPrivateBoard.getShips()));
        }

        @Override
        public void onKillEnemy() {
            Ln.d("enemy ship is sunk!! - shake enemy board");
            mLayout.shakeEnemyBoard();

            mGameplaySounds.playKillSound();
            vibrate();
        }

        @Override
        public void onKillPlayer() {
            Ln.d("my ship is sunk!! - shake my board");
            mLayout.shakePlayerBoard();

            mGameplaySounds.playKillSound();
            vibrate();
        }

        @Override
        public void onMiss() {
            mGameplaySounds.playSplash();
        }

        @Override
        public void onHit() {
            mGameplaySounds.playHitSound();

            mLayout.invalidate();
        }

        @Override
        public void onPlayerShotAt() {
            stopDetectingTurnTimeout();
            updateMyStatus();
        }

        @Override
        public void onPlayerLost(@Nullable Board board) {
            if (board == null) {
                Ln.d("player lost");
                AnalyticsEvent.send("reveal_not_supported");
            } else {
                Ln.d("player lost, opponent is revealing board");
                mLayout.setEnemyBoard(board);
            }

            disableBackPress();
            mLayout.lost();
            showLostScreenDelayed();
        }

        @Override
        public void opponentReady() {
            Ln.d("hiding \"opponent setting board\" notification");
            mLayout.hideOpponentSettingBoardNotification();
        }

        @Override
        public void onOpponentTurn() {
            Ln.d("opponent's turn");
            showOpponentTurn();
            startDetectingTurnTimeout();
        }

        @Override
        public void onPlayersTurn() {
            mParent.startService(getServiceIntent(getString(R.string.your_turn)));
            mLayout.playerTurn();
            mGameIsOn = true;
            mStartTime = SystemClock.elapsedRealtime();

            if (!mGame.isPausable() || isResumed()) {
                Ln.d("player's turn - starting timer");
                mTimerController.start(); // for all practical scenarios - start will only be called from here
            } else {
                Ln.d("player's turn, but screen is paused - DO NOT START TIMER");
            }
        }

        @Override
        public void onMessage(@NonNull String text) {
            ChatMessage message = ChatMessage.newEnemyMessage(text);
            mChatAdapter.add(message);
        }

        @Override
        public String toString() {
            return DebugUtils.getSimpleName(this);
        }
    }

    private void showLostScreenDelayed() {
        mUiThreadHandler.postDelayed(mShowLostScreenCommand, GameplayScreen.LOST_GAME_DELAY);
    }

    private void disableBackPress() {
        Ln.v("disabling backpress");
        mBackPressEnabled = false;
    }

    private void vibrate() {
        if (mSettings.isVibrationOn() && isResumed()) {
            mVibrator.vibrate(VIBRATION_ON_KILL);
        }
    }

    private void stopDetectingShotTimeout() {
        mUiThreadHandler.removeCallbacks(mShotHangDetectionTask);
        Ln.v("stopped detecting shot timeout.");
    }

    private void startDetectingShotTimeout() {
        mUiThreadHandler.postDelayed(mShotHangDetectionTask, SHOT_HANG_DETECTION_TIMEOUT);
        Ln.v("detecting shot timeout...");
    }

    private void stopDetectingTurnTimeout() {
        mUiThreadHandler.removeCallbacks(mTurnHangDetectionTask);
        Ln.v("stopped detecting turn timeout.");
    }

    private void startDetectingTurnTimeout() {
        mUiThreadHandler.postDelayed(mTurnHangDetectionTask, TURN_HANG_DETECTION_TIMEOUT);
        Ln.v("detecting turn timeout...");
    }

    private void updateMyStatus() {
        mLayout.updateMyWorkingShips(GameplayUtils.getWorkingShips(mPlayerPrivateBoard.getShips()));
    }

    private void updateEnemyStatus() {
        Collection<Ship> fleet = getWorkingEnemyShips();
        mLayout.updateEnemyWorkingShips(fleet);
    }

    @NonNull
    private Collection<Ship> getWorkingEnemyShips() {
        Collection<Ship> killedShips = mEnemyPublicBoard.getShips();
        OrientationBuilder orientationBuilder = new OrientationBuilder(new Random());
        Collection<Ship> fleet = ShipUtils.generateFullFleet(mRules.getAllShipsSizes(), orientationBuilder);
        for (Ship ship : killedShips) {
            GameplayUtils.removeShipFromFleet(fleet, ship);
        }
        return fleet;
    }

    private void showWinScreenDelayed(@NonNull Collection<Ship> remainedFleet) {
        mUiThreadHandler.postDelayed(new ShowWinCommand(false, remainedFleet), WON_GAME_DELAY);
    }

    private void showOpponentSurrenderedDialog(@NonNull Collection<Ship> remainedFleet) {
        SimpleActionDialog.create(R.string.opponent_surrendered, new ShowWinCommand(true, remainedFleet)).show(mFm, DIALOG);
    }

    private void surrender(final int penalty) {
        mSettings.setProgressPenalty(mSettings.getProgressPenalty() + penalty);
        backToSelectGame();
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }

    private class GameplayLayoutListenerImpl implements GameplayLayoutListener {

        @Override
        public void onChatClicked() {
            UiEvent.send("chat", "open");
            new ChatDialog.Builder(mChatAdapter).setName(mPlayer.getName()).setPositiveButton(R.string.send, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UiEvent.send("chat", "sent");
                    String text = ((ChatDialog) dialog).getChatMessage().toString();
                    if (TextUtils.isEmpty(text)) {
                        Ln.d("chat text is empty - not sending");
                    } else {
                        mChatAdapter.add(ChatMessage.newMyMessage(text));
                        mEnemy.onNewMessage(text);
                    }
                }
            }).setNegativeButton(R.string.cancel).create().show(mFm, DIALOG);
        }

        @Override
        public void onSoundChanged() {
            boolean on = !mSettings.isSoundOn();
            mSettings.setSound(on);
            mLayout.setSound(on);
            if (!on) {
                mGameplaySounds.stopPlaying();
            }
        }

    }

    private class ShowWinCommand implements Runnable {

        private final boolean mOpponentSurrendered;
        @NonNull
        private final Collection<Ship> mShips;

        private ShowWinCommand(boolean opponentSurrendered, @NonNull Collection<Ship> ships) {
            mOpponentSurrendered = opponentSurrendered;
            mShips = ships;
        }

        @Override
        public void run() {
            setScreen(ScreenCreator.newWinScreen(mGame, mSession, mShips, mStatistics, mOpponentSurrendered));
        }
    }

    private class ShowLostScreenCommand implements Runnable {

        @Override
        public void run() {
            setScreen(ScreenCreator.newLostScreen(mGame, mSession));
        }
    }

    private class TurnTimerListener implements TurnListener {

        @Override
        public void onTimerExpired() {
            stopAlarmSound();

            Ln.d("turn skipped");
            showOpponentTurn();
            startDetectingTurnTimeout();
            mEnemy.go();
        }

        @Override
        public void onPlayerIdle() {
            stopAlarmSound();

            AnalyticsEvent.send("surrendered_passively");
            int penalty = ScoresUtils.calcSurrenderPenalty(mRules.getAllShipsSizes(), mPlayerPrivateBoard.getShips());
            Ln.d("player surrender passively with penalty: " + penalty);
            surrender(penalty);
        }

        @Override
        public void setCurrentTime(int time) {
            mLayout.setCurrentTime(time);
            if (shouldPlayAlarmSound(time)) {
                mGameplaySounds.playAlarmSound();
            }
        }

        @Override
        public void onCanceled() {
            if (mGameplaySounds.isAlarmPlaying()) {
                Ln.d("timer canceled - stopping alarm");
                mGameplaySounds.stopAlarmSound();
            } else {
                Ln.d("timer canceled");
            }
        }

        private void stopAlarmSound() {
            if (mGameplaySounds.isAlarmPlaying()) {
                Ln.v("timer expired - stopping alarm");
                mGameplaySounds.stopAlarmSound();
            }
        }

        private boolean shouldPlayAlarmSound(int timeLeft) {
            return timeLeft <= (ALARM_TIME_SECONDS * 1000) && !mGameplaySounds.isAlarmPlaying();
        }
    }

    private class ShotHangDetectionTask implements Runnable {
        @Override
        public void run() {
            Ln.w("shot_hanged");
            showConnectionLostDialog();
        }
    }

    private class TurnHangDetectionTask implements Runnable {
        @Override
        public void run() {
            Ln.w("turn_hanged");
            showConnectionLostDialog();
        }
    }
}
