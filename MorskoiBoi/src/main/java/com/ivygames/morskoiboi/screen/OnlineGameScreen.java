package com.ivygames.morskoiboi.screen;

import android.support.annotation.NonNull;

import com.ivygames.bluetooth.peer.BluetoothPeer;
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
    protected final Runnable mEndGameCommand;
    @NonNull
    private final BackToSelectGameCommand mBackToSelectGameCommand;
    @NonNull
    private final String mOpponentName;
    @NonNull
    private final RealTimeMultiplayer mMultiplayer = Dependencies.getMultiplayer();
    @NonNull
    private final BluetoothPeer mBluetooth = Dependencies.getBluetooth();
    @NonNull
    private final ConnectionLostListener mRtConnectionLostListener = new ConnectionLostListener() {
        @Override
        public void onConnectionLost(@NonNull MultiplayerEvent event) {
            if (event == MultiplayerEvent.OPPONENT_LEFT) {
                Ln.d("opponent left the game - notifying player");
                mGame.finish();
                showOpponentLeftDialog();
            } else if (event == MultiplayerEvent.CONNECTION_LOST) {
                Ln.d("connection lost - notifying player");
                mGame.finish();
                showConnectionLostDialog();
            }
        }
    };

    protected OnlineGameScreen(@NonNull BattleshipActivity parent, @NonNull Game game, @NonNull String opponentName) {
        super(parent);
        mGame = game;
        mOpponentName = opponentName;

        mBackToSelectGameCommand = new BackToSelectGameCommand(parent);
        mEndGameCommand = new EndGameCommand(game, mBackToSelectGameCommand);

        mMultiplayer.registerConnectionLostListener(this);

        mBluetooth.setConnectionLostListener(new com.ivygames.bluetooth.peer.ConnectionLostListener() {
            @Override
            public void onConnectionLost() {
                mRtConnectionLostListener.onConnectionLost(MultiplayerEvent.CONNECTION_LOST);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Ln.v(this + " unregister connection listener");
        mMultiplayer.unregisterConnectionLostListener(this);
        mBluetooth.resetConnectionLostListener();
    }

    @Override
    public void onConnectionLost(@NonNull MultiplayerEvent event) {
        mRtConnectionLostListener.onConnectionLost(event);
    }

    private void showOpponentLeftDialog() {
        SimpleActionDialog.create(R.string.opponent_left, mBackToSelectGameCommand).show(mFm, DIALOG);
    }

    protected final void showConnectionLostDialog() {
        SimpleActionDialog.create(R.string.connection_lost,
                mBackToSelectGameCommand).show(mFm, DIALOG);
    }

    protected final void showWantToLeaveRoomDialog() {
        String message = getString(R.string.want_to_leave_room, mOpponentName);
        DialogUtils.newOkCancelDialog(message, mEndGameCommand).show(mFm, DIALOG);
    }

}
