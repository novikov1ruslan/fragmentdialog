package com.ivygames.morskoiboi.screen;

import android.support.annotation.NonNull;

import com.ivygames.common.dialog.DialogUtils;
import com.ivygames.common.dialog.SimpleActionDialog;
import com.ivygames.common.multiplayer.ConnectionLostListener;
import com.ivygames.common.multiplayer.MultiplayerEvent;
import com.ivygames.common.multiplayer.RealTimeMultiplayer;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.Game;
import com.ivygames.morskoiboi.R;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

public abstract class OnlineGameScreen extends BattleshipScreen implements ConnectionLostListener {
    protected static final String DIALOG = FragmentAlertDialog.TAG;

    @NonNull
    protected final Game mGame;
    @NonNull
    private final Runnable mEndGameCommand;
    @NonNull
    private final String mOpponentName;
    @NonNull
    private final RealTimeMultiplayer mMultiplayer = Dependencies.getMultiplayer();

    protected OnlineGameScreen(@NonNull BattleshipActivity parent, @NonNull Game game, @NonNull String opponentName) {
        super(parent);
        mGame = game;
        mOpponentName = opponentName;

        mEndGameCommand = new EndGameCommand(game, new BackToSelectGameCommand(parent));

        // FIXME: if registered here it can happen that event will arrive to more than 1 screen
        mMultiplayer.registerConnectionLostListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Ln.v(this + " unregister event listener");
        mMultiplayer.unregisterConnectionLostListener(this);
    }

    @Override
    public void onConnectionLost(@NonNull MultiplayerEvent event) {
        if (event == MultiplayerEvent.OPPONENT_LEFT) {
            Ln.d("opponent left the game - notifying player");
            showOpponentLeftDialog();
        } else if (event == MultiplayerEvent.CONNECTION_LOST) {
            Ln.d("connection lost - notifying player");
            showConnectionLostDialog();
        }
    }

    private void showOpponentLeftDialog() {
        SimpleActionDialog.create(R.string.opponent_left, mEndGameCommand).show(mFm, DIALOG);
    }

    protected final void showConnectionLostDialog() {
        SimpleActionDialog.create(R.string.connection_lost,
                mEndGameCommand).show(mFm, DIALOG);
    }

    protected final void showWantToLeaveRoomDialog() {
        String message = getString(R.string.want_to_leave_room, mOpponentName);
        DialogUtils.newOkCancelDialog(message, mEndGameCommand).show(mFm, DIALOG);
    }

    protected final void backToSelectGame() {
        mEndGameCommand.run();
    }

}
