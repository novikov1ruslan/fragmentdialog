package com.ivygames.morskoiboi.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.DeviceUtils;
import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.ShipComparator;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.ui.view.BoardSetupLayout;
import com.ivygames.morskoiboi.ui.view.BoardSetupLayout.BoardSetupLayoutListener;
import com.ivygames.morskoiboi.utils.GameUtils;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import java.util.PriorityQueue;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class BoardSetupScreen extends OnlineGameScreen implements BoardSetupLayoutListener, BackPressListener {
    static final String TAG = "BOARD_SETUP";
    private static final String DIALOG = FragmentAlertDialog.TAG;
    private static final int TOTAL_SHIPS = RulesFactory.getRules().getTotalShips().length;
    private static final long BOARD_SETUP_TIMEOUT = 2 * 60 * 1000;

    private Board mBoard = new Board();
    private PriorityQueue<Ship> mFleet = new PriorityQueue<Ship>(TOTAL_SHIPS, new ShipComparator());

    private BoardSetupLayout mLayout;
    private View mTutView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final Rules mRules = RulesFactory.getRules();
    private final Runnable mTimeoutTask = new Runnable() {
        @Override
        public void run() {
            Ln.d("board setup timeout");
            AnalyticsEvent.send(mGaTracker, "board setup timeout");
            Model.instance.game.finish();
//            DialogUtils.showNote(getActivity().getSupportFragmentManager(), R.string.session_timeout);
            DialogUtils.newOkDialog(R.string.session_timeout, new Runnable() {
                @Override
                public void run() {
                    backToSelectGameScreen();
                }
            }).show(mParent.getSupportFragmentManager(), FragmentAlertDialog.TAG);
        }
    };

    @Override
    public View onCreateView(ViewGroup container) {
        GameUtils.populateFullHorizontalFleet(mFleet);
        Ln.d("new board created, fleet initialized");

        mLayout = (BoardSetupLayout) getLayoutInflater().inflate(R.layout.board_setup, container, false);
        mLayout.setScreenActionsListener(this);
        mLayout.setBoard(mBoard, mFleet);
        mTutView = mLayout.setTutView(inflate(R.layout.board_setup_tut));

        if (Model.instance.game.getType() == Game.Type.INTERNET) {
            Ln.d("initializing timeout: " + BOARD_SETUP_TIMEOUT);
            mHandler.postDelayed(mTimeoutTask, BOARD_SETUP_TIMEOUT);
        }

        Ln.d(this + " screen created");
        return mLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        mParent.showTutorial(getTutView());
    }

    @Override
    public void onPause() {
        super.onPause();
        GameSettings.get().hideBoardSetupHelp();
        mParent.dismissTutorial();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacks(mTimeoutTask);
        Crouton.cancelAllCroutons();
        Ln.d(this + " screen destroyed - all croutons canceled");
    }

    @Override
    public void autoSetup() {
        mGaTracker.send(new UiEvent("auto").build());
        mBoard = PlacementFactory.getAlgorithm().generateBoard();
        mFleet = new PriorityQueue<Ship>(TOTAL_SHIPS, new ShipComparator());
        mLayout.setBoard(mBoard, mFleet);
    }

    @Override
    public void showHelp() {
        mParent.showTutorial(mTutView);
    }

    @Override
    public void done() {
        mGaTracker.send(new UiEvent("done").build());
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
        GameSettings.get().hideBoardSetupHelp();
        mParent.dismissTutorial();
    }

    private void showGameplayScreen() {
        mParent.setScreen(new GameplayScreen());
    }

    private void showSetupValidationError() {
        View view = getLayoutInflater().inflate(R.layout.ships_setup_validation_crouton, mLayout, false);
        Crouton.make(getActivity(), view).show();
    }

    @Override
    public void onBackPressed() {
        mGaTracker.send(new UiEvent(GameConstants.GA_ACTION_BACK, "setup").build());
        if (shouldNotifyOpponent()) {
            Ln.d("match against a real human - ask the player if he really wants to exit");
            showWantToLeaveRoomDialog();
        } else {
            backToSelectGameScreen();
        }
    }

    private void backToSelectGameScreen() {
        new BackToSelectGameCommand(mParent).run();
    }

    private void showWantToLeaveRoomDialog() {
        String displayName = Model.instance.opponent.getName();
        String message = getString(R.string.want_to_leave_room, displayName);

        new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGaTracker.send(new UiEvent("left_from_setup", "ok").build());
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
    public View getTutView() {
        if (!DeviceUtils.isTablet(getResources()) && GameSettings.get().showSetupHelp()) {
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
