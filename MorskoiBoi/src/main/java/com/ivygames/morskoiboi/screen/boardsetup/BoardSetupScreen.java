package com.ivygames.morskoiboi.screen.boardsetup;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import com.ivygames.morskoiboi.GameHandler;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.screen.BackToSelectGameCommand;
import com.ivygames.morskoiboi.screen.DialogUtils;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupLayout.BoardSetupLayoutListener;
import com.ivygames.morskoiboi.utils.GameUtils;
import com.ivygames.morskoiboi.variant.Placement;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.PriorityQueue;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public final class BoardSetupScreen extends OnlineGameScreen implements BoardSetupLayoutListener, BackPressListener {
    public static final String TAG = "BOARD_SETUP";
    private static final String DIALOG = FragmentAlertDialog.TAG;
    private static final int INITIAL_CAPACITY = 10;
    private static final long BOARD_SETUP_TIMEOUT = 60 * 1000;

    @NonNull
    private Board mBoard = new Board();
    @NonNull
    private PriorityQueue<Ship> mFleet = new PriorityQueue<>(INITIAL_CAPACITY, new ShipComparator());

    private BoardSetupLayout mLayout;
    private View mTutView;

    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();
    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    @NonNull
    private final AndroidDevice mDevice = Dependencies.getDevice();
    @NonNull
    private final Rules mRules = RulesFactory.getRules();
    @NonNull
    private final PlacementAlgorithm mPlacement = PlacementFactory.getAlgorithm();

    @NonNull
    private final Runnable mTimeoutTask = new Runnable() {
        @Override
        public void run() {
            Ln.d("board setup timeout");
            AnalyticsEvent.send("board setup timeout");
            Model.instance.game.finish();
            DialogUtils.newOkDialog(R.string.session_timeout, new Runnable() {
                @Override
                public void run() {
                    backToSelectGameScreen();
                }
            }).show(mFm, FragmentAlertDialog.TAG);
        }
    };

    public BoardSetupScreen(@NonNull BattleshipActivity parent) {
        super(parent);
        mFleet.addAll(generateFullHorizontalFleet());
        Ln.d("new board created, fleet initialized");
    }

    protected Collection<Ship> generateFullHorizontalFleet() {
        return Ship.setOrientationForShips(generateFullFleet(), Ship.Orientation.HORIZONTAL);
    }

    @NonNull
    private Collection<Ship> generateFullFleet() {
        return GameUtils.generateShipsForSizes(mRules.getAllShipsSizes());
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (BoardSetupLayout) getLayoutInflater().inflate(R.layout.board_setup, container, false);
        mLayout.setScreenActionsListener(this);
        mLayout.setBoard(mBoard, mFleet);
        mTutView = mLayout.setTutView(inflate(R.layout.board_setup_tut));

        if (Model.instance.game.getType() == Game.Type.INTERNET || GameConstants.IS_TEST_MODE) {
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

    @Override
    public void autoSetup() {
        UiEvent.send("auto");
        mBoard = mPlacement.generateBoard();
        mFleet = new PriorityQueue<>(INITIAL_CAPACITY, new ShipComparator());
        mLayout.setBoard(mBoard, mFleet);
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
            Model.instance.player.setBoard(mBoard);
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

    private void showGameplayScreen() {
        setScreen(GameHandler.newGameplayScreen());
    }

    private void showSetupValidationError() {
        View view = getLayoutInflater().inflate(R.layout.ships_setup_validation_crouton, mLayout, false);
        Crouton.make(parent(), view).show();
    }

    @Override
    public void onBackPressed() {
        UiEvent.send(UiEvent.GA_ACTION_BACK, "setup");
        if (shouldNotifyOpponent()) {
            Ln.d("match against a real human - ask the player if he really wants to exit");
            showWantToLeaveRoomDialog();
        } else {
            backToSelectGameScreen();
        }
    }

    private void backToSelectGameScreen() {
        new BackToSelectGameCommand(parent()).run();
    }

    private void showWantToLeaveRoomDialog() {
        String displayName = Model.instance.opponent.getName();
        String message = getString(R.string.want_to_leave_room, displayName);

        new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                UiEvent.send("left_from_setup", "ok");
                Ln.d("player decided to leave the game - finishing");
                backToSelectGameScreen();
            }
        }).setNegativeButton(R.string.cancel).create().show(mFm, DIALOG);
    }

    @Override
    public View getView() {
        return mLayout;
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
    public String toString() {
        return TAG + debugSuffix();
    }

}
