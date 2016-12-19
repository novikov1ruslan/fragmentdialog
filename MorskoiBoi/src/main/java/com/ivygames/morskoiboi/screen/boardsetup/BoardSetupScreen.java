package com.ivygames.morskoiboi.screen.boardsetup;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.Placement;
import com.ivygames.battleship.Rules;
import com.ivygames.battleship.ShipUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.common.AndroidDevice;
import com.ivygames.common.DebugUtils;
import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.dialog.DialogUtils;
import com.ivygames.common.ui.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.BuildConfig;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.Game;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.RandomOrientationBuilder;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupLayout.BoardSetupLayoutListener;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Random;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public final class BoardSetupScreen extends OnlineGameScreen implements BackPressListener {
    public static final String TAG = "BOARD_SETUP";
    private static final int INITIAL_CAPACITY = 10;
    private static final long BOARD_SETUP_TIMEOUT = 60 * 1000;

    @NonNull
    private final Board mBoard;
    @NonNull
    private final PriorityQueue<Ship> mFleet = new PriorityQueue<>(INITIAL_CAPACITY, new ShipComparator());
    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();
    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    @NonNull
    private final AndroidDevice mDevice = Dependencies.getDevice();
    @NonNull
    private final Rules mRules = Dependencies.getRules();
    @NonNull
    private final Random mRandom = Dependencies.getRandom();

    private BoardSetupLayout mLayout;
    private View mTutView;

    @NonNull
    private final Runnable mTimeoutTask = new Runnable() {
        @Override
        public void run() {
            Ln.d("board setup timeout");
            AnalyticsEvent.send("board setup timeout");
            mGame.finish();
            DialogUtils.newOkDialog(R.string.session_timeout,
                    mEndGameCommand).show(mFm, FragmentAlertDialog.TAG);
        }
    };
    @NonNull
    private final Session mSession;

    public BoardSetupScreen(@NonNull BattleshipActivity parent, @NonNull Game game, @NonNull Session session) {
        super(parent, game, session.opponent.getName());
        mSession = session;
        mFleet.addAll(ShipUtils.generateFullHorizontalFleet(mRules.getAllShipsSizes()));
        mBoard = session.player.getBoard();
        Ln.d("new board created, fleet initialized");
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (BoardSetupLayout) getLayoutInflater().inflate(R.layout.board_setup, container, false);
        mLayout.setScreenActionsListener(mLayoutListener);
        mLayout.setBoard(mBoard, mFleet);
        if (mRules.allowAdjacentShips()) {
            mLayout.allowAdjacentShips();
        }
        mTutView = mLayout.setTutView(inflate(R.layout.board_setup_tut));

        if (mGame.isRemote()) {
            Ln.d("initial timeout: " + BOARD_SETUP_TIMEOUT);
            mHandler.postDelayed(mTimeoutTask, BOARD_SETUP_TIMEOUT);
        }

        Ln.d(this + " screen created");
        return mLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        parent().showTutorial(getTutView());
    }

    @Override
    public void onPause() {
        super.onPause();
        mSettings.hideBoardSetupHelp();
        parent().dismissTutorial();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mTimeoutTask);
        Crouton.cancelAllCroutons();
        Ln.d(this + " screen destroyed - all croutons canceled");
    }

    @NonNull
    private final BoardSetupLayoutListener mLayoutListener = new BoardSetupLayoutListener() {
        @Override
        public void autoSetup() {
            UiEvent.send("auto");
            mBoard.clearBoard();
            int[] allShipsSizes = mRules.getAllShipsSizes();
            RandomOrientationBuilder orientationBuilder = new RandomOrientationBuilder(mRandom);
            Collection<Ship> ships = ShipUtils.createNewShips(allShipsSizes, orientationBuilder);
            if (!BuildConfig.DEBUG) { // needed for ui testing to simulate all horizontal ships
                while (ShipUtils.onlyHorizontalShips(ships)) {
                    ships = ShipUtils.createNewShips(allShipsSizes, orientationBuilder);
                }
            }
            new Placement(mRandom, mRules.allowAdjacentShips()).populateBoardWithShips(mBoard, ships);
            mFleet.clear();
            mLayout.notifyDataChanged();
            mLayout.invalidate();
        }

        @Override
        public void showHelp() {
            parent().showTutorial(mTutView);
        }

        @Override
        public void done() {
            UiEvent.send("done");
            if (BoardUtils.isBoardSet(mBoard, mRules)) {
                if (ShipUtils.onlyHorizontalShips(mBoard.getShips())) {
                    showOnlyHorizontalDialog();
                } else {
                    continueToGameplay();
                }
            } else {
                Ln.d("!: board is not set yet");
                showSetupValidationError();
            }
        }

        @Override
        public void dismissTutorial() {
            mSettings.hideBoardSetupHelp();
            parent().dismissTutorial();
        }
    };

    private void continueToGameplay() {
        Ln.d("board set - showing gameplay screen");
        mSession.player.setBoard(mBoard);
        showGameplayScreen();
    }

    private void showGameplayScreen() {
        setScreen(ScreenCreator.newGameplayScreen(mGame, mSession));
    }

    private void showSetupValidationError() {
        View view = getLayoutInflater().inflate(R.layout.ships_setup_validation_crouton, mLayout, false);
        Crouton.make(mParent, view).show();
    }

    @Override
    public void onBackPressed() {
        UiEvent.send(UiEvent.GA_ACTION_BACK, "setup");
        if (mGame.shouldNotifyOpponent()) {
            Ln.d("match against a real human - ask the player if he really wants to exit");
            showWantToLeaveRoomDialog();
        } else {
            mEndGameCommand.run();
        }
    }

    @Override
    @Nullable
    public View getTutView() {
        if (!mDevice.isTablet() && mSettings.showSetupHelp()) {
            Ln.v("setup tip needs to be shown");
            return mTutView;
        }
        return null;
    }

    @Override
    @NonNull
    public View getView() {
        return mLayout;
    }

    private void showOnlyHorizontalDialog() {
        AnalyticsEvent.send("only_horizontal");

        String message = getString(R.string.only_horizontal_ships) + " " +
                getString(R.string.rotate_instruction);
        String option1 = getString(R.string.rearrange);
        String option2 = getString(R.string.continue_str);

        Runnable command1 = new Runnable() {
            @Override
            public void run() {
            }
        };
        Runnable command2 = new Runnable() {
            @Override
            public void run() {
                continueToGameplay();
            }
        };
        DialogUtils.twoOptions(message, option1, command1, option2, command2).show(mFm, DIALOG);
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }

}
