package com.ivygames.morskoiboi.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ShipComparator;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.model.Board;
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

/**
 * In this screen it is already known what is the game type.
 */
public class BoardSetupFragment extends OnlineGameFragment implements BoardSetupLayoutListener, BackPressListener {
	static final String TAG = "BOARD_SETUP";
	private static final String DIALOG = FragmentAlertDialog.TAG;

	private Board mBoard;
	private PriorityQueue<Ship> mFleet;

	private BoardSetupLayout mLayout;

	@Override
	public View onCreateView(ViewGroup container) {
		mFleet = new PriorityQueue<Ship>(10, new ShipComparator());
		GameUtils.populateFullHorizontalFleet(mFleet);
		mBoard = new Board();
		Ln.d("new board created, fleet initialized");

		mLayout = (BoardSetupLayout) getLayoutInflater().inflate(R.layout.board_setup, container, false);
		mLayout.setScreenActionsListener(this);
		mLayout.setBoard(mBoard, mFleet);

		if (GameSettings.get().showTips()) {
			Ln.d("showing tips");
			mLayout.showTips();
		} else {
			Ln.d("hiding tips");
			mLayout.hideTips();
		}

		Ln.d(this + " screen created");
		return mLayout;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Crouton.cancelAllCroutons();
		Ln.d(this + " screen destroyed - all croutons canceled");
	}

	@Override
	public void autoSetup() {
		mGaTracker.send(new UiEvent("auto").build());
		mBoard = PlacementFactory.getAlgorithm().generateBoard();
		mFleet = new PriorityQueue<Ship>(10, new ShipComparator());
		mLayout.setBoard(mBoard, mFleet);
	}

	@Override
	public void done() {
		mGaTracker.send(new UiEvent("done").build());
		if (mLayout.isSet()) {
			Ln.d("board set - showing gameplay screen");
			Model.instance.player.setBoard(mBoard);
			showGameplayScreen();
		} else {
			Ln.d("!: board is not set yet");
			showSetupValidationError();
		}
	}

	private void showGameplayScreen() {
		mParent.setScreen(new GameplayFragment());
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
				mGaTracker.send(new UiEvent("left_from_sutup", "ok").build());
				Ln.d("player decided to leave the game - finishing");
				backToSelectGameScreen();
			}
		}).setNegativeButton(R.string.cancel).create().show(mFm, DIALOG);
	}

	@Override
	public void toggleTips(boolean showTips) {
		GameSettings.get().setShowTips(showTips);
	}

	@Override
	public View getView() {
		return mLayout;
	}

	@Override
	public String toString() {
		return TAG + debugSuffix();
	}

}
