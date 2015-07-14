package com.ivygames.morskoiboi.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.AdManager;
import com.ivygames.morskoiboi.DeviceUtils;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.PlayerOpponent;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rank;
import com.ivygames.morskoiboi.ai.AndroidGame;
import com.ivygames.morskoiboi.ai.AndroidOpponent;
import com.ivygames.morskoiboi.analytics.ExceptionEvent;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.rt.InvitationEvent;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.ui.BattleshipActivity.SignInListener;
import com.ivygames.morskoiboi.ui.view.SelectGameLayout;
import com.ivygames.morskoiboi.ui.view.SelectGameLayout.SelectGameActions;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import de.greenrobot.event.EventBus;

public class SelectGameScreen extends BattleshipScreen implements SelectGameActions, SignInListener, BackPressListener {
    private static final String TAG = "SELECT_GAME";
    private static final String DIALOG = FragmentAlertDialog.TAG;

    private static final int REQUEST_ENABLE_BT = 2;
    private SelectGameLayout mLayout;

    private boolean mViaInternetRequested;

    private View mTutView;

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (SelectGameLayout) getLayoutInflater().inflate(R.layout.select_game, container, false);
        mLayout.setScreenActions(this);

        if (!hasBluetooth()) {
            Ln.d("Bluetooth is absent - hiding the BT option");
            mLayout.hideBluetooth();
        }

        GameSettings settings = GameSettings.get();
        mLayout.setPlayerName(settings.getPlayerName());
        Rank rank = Rank.getBestRankForScore(settings.getProgress().getRank());
        mLayout.setRank(rank);
        mTutView = mLayout.setTutView(inflate(R.layout.select_game_tut));

        Ln.d(this + " screen created, rank = " + rank);
        return mLayout;
    }

    @Override
    public View getTutView() {
        if (GameSettings.get().showProgressHelp()) {
            Ln.v("rank tip needs to be shown");
            return mTutView;
        }
        return null;
    }

    @Override
    public View getView() {
        return mLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        AdManager.instance.showInterstitialAfterPlay();
        mParent.showTutorial(getTutView());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        showInvitationIfHas(mParent.hasInvitation());
    }

    @Override
    public void onPause() {
        super.onPause();
        Ln.d(this + " screen partially hidden - persisting player name");
        mParent.dismissTutorial();
        GameSettings.get().hideProgressHelp();
        GameSettings.get().setPlayerName(mLayout.getPlayerName());
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(InvitationEvent event) {
        showInvitationIfHas(mParent.hasInvitation());
    }

    private void showInvitationIfHas(boolean hasInvitations) {
        if (hasInvitations) {
            Ln.d(this + ": there is a pending invitation ");
            mLayout.showInvitation();
        } else {
            Ln.v(this + ": there are no pending invitations");
            mLayout.hideInvitation();
        }
    }

    private boolean hasBluetooth() {
        PackageManager pm = getActivity().getPackageManager();
        boolean hasBluetooth = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        hasBluetooth &= BluetoothAdapter.getDefaultAdapter() != null;
        return hasBluetooth;
    }

    @Override
    public void vsAndroid() {
        mGaTracker.send(new UiEvent("vsAndroid").build());
        AndroidOpponent opponent = new AndroidOpponent(getString(R.string.android));
        Model.instance.game = new AndroidGame(opponent);
        Model.instance.setOpponents(new PlayerOpponent(mLayout.getPlayerName()), opponent);
        showBoardSetup();
    }

    private void showBoardSetup() {
        mParent.setScreen(new BoardSetupScreen());
    }

    @Override
    public void viaBlueTooth() {
        // If BT is not on, request that it be enabled.
        boolean enabled = BluetoothAdapter.getDefaultAdapter().isEnabled();
        mGaTracker.send(new UiEvent("viaBluetooth", enabled ? 1 : 0).build());
        if (enabled) {
            showDeviceListScreen();
        } else {
            Ln.d("Bluetooth available, but not enabled - prompt to enable");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (DeviceUtils.resolverAvailableForIntent(enableIntent)) {
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                Ln.w("Bluetooth resolver is not available");
                mGaTracker.send(new ExceptionEvent("bt_error").build());
                showBtErrorDialog();
            }
        }
    }

    private void showDeviceListScreen() {
        mParent.setScreen(new DeviceListScreen());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Ln.v("result=" + resultCode + ", request=" + requestCode + ", data=" + data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            showDeviceListScreen();
        }
    }

    @Override
    public void viaInternet() {
        boolean signedIn = mApiClient.isConnected();
        mGaTracker.send(new UiEvent("viaInternet", signedIn ? 1 : 0).build());

        if (signedIn) {
            showInternetGameScreen();
        } else {
            Ln.d("user is not signed in - ask to sign in");
            showInternetDialog();
        }
    }

    @Override
    public void showHelp() {
        mParent.showTutorial(mTutView);
    }

    @Override
    public void dismissTutorial() {
        GameSettings.get().hideProgressHelp();
        mParent.dismissTutorial();
    }

    private void showInternetGameScreen() {
        mParent.setScreen(new InternetGameScreen());
    }

    private void showInternetDialog() {
        new SignInDialog.Builder().setMessage(R.string.internet_request).setPositiveButton(R.string.sign_in, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGaTracker.send(new UiEvent("sign_in", "internet").build());
                mViaInternetRequested = true;
                mApiClient.connect();
            }
        }).create().show(mFm, DIALOG);
    }

    private void showBtErrorDialog() {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        new AlertDialogBuilder().setMessage(R.string.bluetooth_not_available).setPositiveButton(R.string.ok).create().show(fm, null);
    }

    @Override
    public void onSignInSucceeded() {
        if (mLayout == null) {
            Ln.d("signed in before layout created - defer setting name");
            return;
        }

        mLayout.setPlayerName(GameSettings.get().getPlayerName());
        if (mViaInternetRequested) {
            mViaInternetRequested = false;
            showInternetGameScreen();
        }
    }

    @Override
    public void showRanks() {
        mGaTracker.send(new UiEvent("showRanks").build());
        mParent.setScreen(new RanksListScreen());
    }

    @Override
    public void onBackPressed() {
        mParent.setScreen(new MainScreen());
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }

}
