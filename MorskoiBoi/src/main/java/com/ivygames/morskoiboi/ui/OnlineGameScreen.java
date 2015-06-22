package com.ivygames.morskoiboi.ui;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.GameEvent;
import com.ivygames.morskoiboi.model.Model;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import de.greenrobot.event.EventBus;

public abstract class OnlineGameScreen extends BattleshipScreen {
	private static final String DIALOG = FragmentAlertDialog.TAG;

	@Override
	public void onStart() {
		super.onStart();
		Ln.v(this + " fragment started - register event listener");
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		Ln.v(this + " fragment stopped - unregister event listener");
		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(GameEvent event) {
		EventBus.getDefault().removeAllStickyEvents();
		Ln.d(this + " screen received event: " + event);
		if (event == GameEvent.OPPONENT_LEFT) {
			Ln.d("opponent left the game - notifying player");
			showOpponentLeftDialog();
		} else if (event == GameEvent.CONNECTION_LOST) {
			Ln.i("connection lost - notifying player");
			showConnectionLostDialog();
		}
	}

	private void showOpponentLeftDialog() {
		SimpleActionDialog.create(R.string.opponent_left, new BackToSelectGameCommand(mParent)).show(mFm, DIALOG);
	}

	private void showConnectionLostDialog() {
		SimpleActionDialog.create(R.string.connection_lost, new BackToSelectGameCommand(mParent)).show(mFm, DIALOG);
	}

	protected final boolean shouldNotifyOpponent() {
		return Model.instance.game.getType() != Game.Type.VS_ANDROID;
	}
}
