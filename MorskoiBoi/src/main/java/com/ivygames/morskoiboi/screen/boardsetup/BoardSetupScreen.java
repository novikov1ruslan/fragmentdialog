package com.ivygames.morskoiboi.screen.boardsetup;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.morskoiboi.AndroidDevice;
import com.ivygames.morskoiboi.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.screen.DialogUtils;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupLayout.BoardSetupLayoutListener;
import com.ivygames.morskoiboi.utils.GameUtils;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.PriorityQueue;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public final class BoardSetupScreen extends OnlineGameScreen implements BackPressListener {
    public static final String TAG = "BOARD_SETUP";
    private static final int INITIAL_CAPACITY = 10;
    private static final long BOARD_SETUP_TIMEOUT = 60 * 1000;

    @NonNull
    private final Board mBoard = new Board();
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
    private final Placement mPlacement = PlacementFactory.getAlgorithm();

    private BoardSetupLayout mLayout;
    private View mTutView;

    @NonNull
    private final Runnable mTimeoutTask = new Runnable() {
        @Override
        public void run() {
            Ln.d("board setup timeout");
            AnalyticsEvent.send("board setup timeout");
            mGame.finish();
            DialogUtils.newOkDialog(R.string.session_timeout, new Runnable() {
                @Override
                public void run() {
                    backToSelectGame();
                }
            }).show(mFm, FragmentAlertDialog.TAG);
        }
    };

    public BoardSetupScreen(@NonNull BattleshipActivity parent, @NonNull Game game) {
        super(parent, game, Model.opponent.getName());
        mFleet.addAll(generateFullHorizontalFleet());
        Ln.d("new board created, fleet initialized");
    }

    @NonNull
    private Collection<Ship> generateFullHorizontalFleet() {
        return Ship.setOrientationForShips(generateFullFleet(), Ship.Orientation.HORIZONTAL);
    }

    @NonNull
    private Collection<Ship> generateFullFleet() {
        return GameUtils.generateShipsForSizes(mRules.getAllShipsSizes());
    }

    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (BoardSetupLayout) getLayoutInflater().inflate(R.layout.board_setup, container, false);
        mLayout.setScreenActionsListener(mLayoutListener);
        mLayout.setBoard(mBoard, mFleet);
        mTutView = mLayout.setTutView(inflate(R.layout.board_setup_tut));

        if (mGame.getType() == Game.Type.INTERNET || GameConstants.IS_TEST_MODE) {
            Ln.d("initializing timeout: " + BOARD_SETUP_TIMEOUT);
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
            mPlacement.populateBoardWithShips(mBoard, generateFullFleet());
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
            if (mRules.isBoardSet(mBoard)) {
                Ln.d("board set - showing gameplay screen");
                Model.player.setBoard(mBoard);
                showGameplayScreen();
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

    private void showGameplayScreen() {
        setScreen(ScreenCreator.newGameplayScreen(mGame));
    }

    private void showSetupValidationError() {
        View view = getLayoutInflater().inflate(R.layout.ships_setup_validation_crouton, mLayout, false);
        Crouton.make(mParent, view).show();
    }

    @Override
    public void onBackPressed() {
        UiEvent.send(UiEvent.GA_ACTION_BACK, "setup");
        if (shouldNotifyOpponent()) {
            Ln.d("match against a real human - ask the player if he really wants to exit");
            showWantToLeaveRoomDialog();
        } else {
            backToSelectGame();
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

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }

}
