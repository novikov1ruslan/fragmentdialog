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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.common.VibratorWrapper;
import com.ivygames.common.ads.AdProvider;
import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.dialog.SimpleActionDialog;
import com.ivygames.common.game.Bidder;
import com.ivygames.common.timer.TurnListener;
import com.ivygames.common.timer.TurnTimerController;
import com.ivygames.common.ui.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.PlayerCallback;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.ai.Cancellable;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Game.Type;
import com.ivygames.morskoiboi.model.GameEvent;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.ShotResult;
import com.ivygames.morskoiboi.model.ScoreStatistics;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.player.PlayerOpponent;
import com.ivygames.morskoiboi.rt.InternetService;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.Collection;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class GameplayScreen extends OnlineGameScreen implements BackPressListener {
    private static final String TAG = "GAMEPLAY";

    private static final String DIALOG = FragmentAlertDialog.TAG;

    private static final int START_DELAY = 3000;
    private static final int WON_GAME_DELAY = 3000; // milliseconds

    private static final int VIBRATION_ON_KILL = 500;

    private static final int SHOT_HANG_DETECTION_TIMEOUT = 10000; // milliseconds
    private static final int TURN_HANG_DETECTION_TIMEOUT = 60000; // milliseconds

    private static final int ALARM_TIME_SECONDS = 10;

    //    private static final long LOST_GAME_WO_REVEAL_DELAY = 3000; // milliseconds
    private static final long LOST_GAME_WITH_REVEAL_DELAY = 5000; // milliseconds

    @NonNull
    private final Handler mUiThreadHandler = new Handler(Looper.getMainLooper());
    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();
    @NonNull
    private final Rules mRules = Dependencies.getRules();
    @NonNull
    private final Runnable mShowLostScreenCommand = new ShowLostScreenCommand();
    @NonNull
    private final TurnListener mTurnTimerListener = new TurnTimerListener();
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
    private Session mSession;
    @NonNull
    private final TurnTimerController mTimerController;
    @NonNull
    private final Intent mMatchStatusIntent;
    @NonNull
    private final AdProvider mAdProvider = Dependencies.getAdProvider();

    private boolean mMyTurn;
    private long mUnlockedTime;
    private long mStartTime;
    private boolean mGameIsOn;
    private GameplayLayoutInterface mLayout;
    private boolean mBackPressEnabled = true;

    public GameplayScreen(@NonNull BattleshipActivity parent, @NonNull Game game, @NonNull Session session,
                          @NonNull TurnTimerController timerController) {
        super(parent, game, session.opponent.getName());
        mSession = session;
        mTimerController = timerController;

        mAdProvider.needToShowAfterPlayAd();
        mGameplaySounds = new GameplayScreenSounds((AudioManager) parent.getSystemService(Context.AUDIO_SERVICE), this, mSettings);
        mGameplaySounds.prepareSoundPool(parent.getAssets());
        mPlayer = session.player;
        mEnemy = session.opponent;
        mEnemyPublicBoard = mPlayer.getEnemyBoard();
        mPlayerPrivateBoard = mPlayer.getBoard();

        mVibrator = new VibratorWrapper(mParent);

        mMatchStatusIntent = new Intent(parent, InternetService.class);
        mMatchStatusIntent.putExtra(InternetService.EXTRA_CONTENT_TITLE, getString(R.string.match_against) + " " + mEnemy.getName());
        mChatAdapter = new ChatAdapter(getLayoutInflater());
        mTimerController.setListener(mTurnTimerListener);
        Ln.d("game data prepared");
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
        if (mGame.getType() != Type.INTERNET) {
            Ln.d("not internet game - hide chat button");
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
        mLayout.setAlarmTime(ALARM_TIME_SECONDS * 1000);

        mLayout.setPlayerName(mPlayer.getName());
        mLayout.setEnemyName(mEnemy.getName());

        if (mPlayer.isOpponentReady()) {
            showOpponentTurn();
        } else {
            Ln.d("opponent is still setting board");
            showOpponentSettingBoardNote();
        }

        mPlayer.setCallback(new UiPlayerCallback());

        mLayout.setShotListener(new BoardShotListener(mEnemy, mGameplaySounds));

        Ln.d("screen is fully created - start bidding in " + START_DELAY + "ms");
        mUiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayer.startBidding(new Bidder().newBid());
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
        if (mGame.getType() == Type.VS_ANDROID) {
            // timer is not running if it is not player's turn, but cancel it just in case
            mTimerController.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Ln.d(this + " is fully visible - resuming sounds");
        if (mGame.getType() == Type.VS_ANDROID && !mLayout.isLocked()) {
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
        mPlayer.removeCallback();
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
    public void onEventMainThread(GameEvent event) {
        if (event == GameEvent.OPPONENT_LEFT) {
            mTimerController.stop();
            mParent.stopService(mMatchStatusIntent);
            if (mPlayer.isOpponentReady()) {
                Ln.d("opponent surrendered - notifying player, (shortly game will finish)");
                AnalyticsEvent.send("opponent_surrendered");
                showOpponentSurrenderedDialog(mPlayerPrivateBoard.getShips());
            } else {
                super.onEventMainThread(event);
            }
        } else if (event == GameEvent.CONNECTION_LOST) {
            EventBus.getDefault().removeAllStickyEvents();
            mTimerController.stop();
            mParent.stopService(mMatchStatusIntent);
            if (mGame.hasFinished()) {
                Ln.d(event + " received, but the game has already finished - skipping this event");
            } else {
                super.onEventMainThread(event);
            }
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

        if (shouldNotifyOpponent()) {
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
        final int penalty = mRules.calcSurrenderPenalty(mPlayerPrivateBoard.getShips());
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
        public void onAimingFinished(int x, int y) {
            Ln.d("aiming finished");
            debug_aiming_started = false;
            mGameplaySounds.stopKantropSound();

            if (!Board.contains(x, y)) {
                Ln.d("pressing outside the board: " + x + "," + y);
                return;
            }

            Cell cell = mEnemyPublicBoard.getCell(x, y);
            if (!cell.isEmpty()) {
                Ln.d(cell + " is not empty");
                // TODO: play sound
                return;
            }
            // ----------------------------------
            mTimerController.stop();

            Vector2 aim = Vector2.get(x, y);
            Ln.d("shooting at: " + aim + cell + ", timer cancelled, locking board");
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
            // FIXME
            if (debug_aiming_started) {
                Ln.e("aiming started");
            }
            debug_aiming_started = true;
            mGameplaySounds.playKantrop();
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
        mMyTurn = false;
    }

    private class UiPlayerCallback implements PlayerCallback {

        @Override
        public void onShotResult(@NonNull ShotResult result) {
            stopDetectingShotTimeout();

            Ln.v(result);
            mStatistics.updateWithNewShot(result.ship, result.cell);

            mLayout.removeAim();
            mLayout.setShotResult(result);

            mLayout.invalidateEnemyBoard();

            updateEnemyStatus();
        }

        @Override
        public void onWin() {
            Ln.d("enemy has lost!!!");
            disableBackPress();

            mStatistics.setTimeSpent(mUnlockedTime);
            mLayout.win();

            Collection<Ship> fleet = new ArrayList<>();
            fleet.addAll(mPlayerPrivateBoard.getShips());

            showWinScreenDelayed(fleet);
        }

        @Override
        public void onKill(@NonNull Side side) {
            if (side == Side.OPPONENT) {
                Ln.v("enemy ship is sunk!! - shake enemy board");
                mLayout.shakeEnemyBoard();
            } else if (side == Side.PLAYER) {
                Ln.v("my ship is sunk!! - shake my board");
                mLayout.shakePlayerBoard();
            }
            mGameplaySounds.playKillSound();
            vibrate(VIBRATION_ON_KILL);
        }

        @Override
        public void onMiss(@NonNull Side side) {
            mGameplaySounds.playSplash();
        }

        @Override
        public void onHit(@NonNull Side side) {
            mGameplaySounds.playHitSound();

            if (side == Side.PLAYER) {
                mLayout.invalidatePlayerBoard();
            }
        }

        @Override
        public void onShotAt(@NonNull Vector2 aim) {
            stopDetectingTurnTimeout();
            updateMyStatus();
        }

        @Override
        public void onLost(@Nullable Board board) {
            if (board == null) {
                Ln.d("player lost");
                AnalyticsEvent.send("reveal_not_supported");
            } else {
                Ln.d("player lost, opponent is revealing board");
                mLayout.setEnemyBoard(board);
            }

            disableBackPress();
            mLayout.lost();
            showLostScreenDelayed(LOST_GAME_WITH_REVEAL_DELAY);
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
            Ln.d("player's turn");
            mParent.startService(getServiceIntent(getString(R.string.your_turn)));
            mLayout.playerTurn();
            mGameIsOn = true;
            mStartTime = SystemClock.elapsedRealtime();
            mMyTurn = true;

            if (mGame.getType() != Type.VS_ANDROID || isResumed()) {
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
            return UiPlayerCallback.class.getSimpleName() + "#" + (hashCode() % 1000);
        }
    }

    private void showLostScreenDelayed(long mseconds) {
        mUiThreadHandler.postDelayed(mShowLostScreenCommand, mseconds);
    }

    private void disableBackPress() {
        Ln.d("disabling backpress");
        mBackPressEnabled = false;
    }

    private void vibrate(int duration) {
        if (mSettings.isVibrationOn() && isResumed()) {
            mVibrator.vibrate(duration);
        }
    }

    private void stopDetectingShotTimeout() {
        mUiThreadHandler.removeCallbacks(mShotHangDetectionTask);
        Ln.v("not detecting shot timeout");
    }

    private void startDetectingShotTimeout() {
        mUiThreadHandler.postDelayed(mShotHangDetectionTask, SHOT_HANG_DETECTION_TIMEOUT);
        Ln.v("detecting shot timeout");
    }

    private void stopDetectingTurnTimeout() {
        mUiThreadHandler.removeCallbacks(mTurnHangDetectionTask);
        Ln.v("not detecting turn timeout");
    }

    private void startDetectingTurnTimeout() {
        mUiThreadHandler.postDelayed(mTurnHangDetectionTask, TURN_HANG_DETECTION_TIMEOUT);
        Ln.v("detecting turn timeout");
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
        Collection<Ship> fleet = mRules.generateFullFleet();
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
        return TAG + debugSuffix();
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
            int penalty = mRules.calcSurrenderPenalty(mPlayerPrivateBoard.getShips());
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
                Ln.v("timer canceled - stopping alarm");
                mGameplaySounds.stopAlarmSound();
            } else {
                Ln.v("timer canceled");
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
