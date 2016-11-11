package com.ivygames.morskoiboi.screen.selectgame;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.common.AndroidDevice;
import com.ivygames.common.DebugUtils;
import com.ivygames.common.ui.SignInListener;
import com.ivygames.common.ads.AdProvider;
import com.ivygames.common.analytics.ExceptionEvent;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.multiplayer.InvitationToShowListener;
import com.ivygames.common.multiplayer.RealTimeMultiplayer;
import com.ivygames.common.ui.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerFactory;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rank;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.ai.AndroidGame;
import com.ivygames.morskoiboi.ai.DelayedOpponent;
import com.ivygames.morskoiboi.bluetooth.BluetoothAdapterWrapper;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.player.PlayerOpponent;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ivygames.morskoiboi.screen.SignInDialog;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameLayout.SelectGameActions;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

public class SelectGameScreen extends BattleshipScreen implements SelectGameActions,
        SignInListener, BackPressListener {
    private static final String DIALOG = FragmentAlertDialog.TAG;

    private SelectGameLayout mLayout;

    private boolean mViaInternetRequested;

    private View mTutView;

    @NonNull
    private final ApiClient mApiClient = Dependencies.getApiClient();
    @NonNull
    private final RealTimeMultiplayer mMultiplayer = Dependencies.getMultiplayer();
    @NonNull
    private final AndroidDevice mDevice = Dependencies.getDevice();
    @NonNull
    private final Rules mRules = Dependencies.getRules();
    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();
    @NonNull
    private final AdProvider mAdProvider = Dependencies.getAdProvider();

    private InvitationToShowListener mScreenInvitationListener;
    @NonNull
    private final Placement mPlacement = Dependencies.getPlacement();
    @NonNull
    private final PlayerFactory mPlayerFactory = Dependencies.getPlayerFactory();

    @NonNull
    private final PlayerFactory mOpponentFactory = Dependencies.getAiPlayerFactory();

    public SelectGameScreen(@NonNull BattleshipActivity parent) {
        super(parent);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (SelectGameLayout) inflate(R.layout.select_game, container);
        mLayout.setScreenActions(this);

        if (!mDevice.hasBluetooth()) {
            Ln.d("Bluetooth is absent - hiding the BT option");
            mLayout.hideBluetooth();
        }

        mLayout.setPlayerName(mSettings.getPlayerName());
        Rank rank = Rank.getBestRankForScore(mSettings.getProgress().getScores());
        mLayout.setRank(rank);
        mTutView = mLayout.setTutView(inflate(R.layout.select_game_tut));

        mScreenInvitationListener = new InvitationToShowListener(mMultiplayer, mLayout);

        Ln.d(this + " screen created, rank = " + rank);
        return mLayout;
    }

    @Override
    public View getTutView() {
        if (mSettings.showProgressHelp()) {
            Ln.v("rank tip needs to be shown");
            return mTutView;
        }
        return null;
    }

    @NonNull
    @Override
    public View getView() {
        return mLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        parent().showTutorial(getTutView());
        mAdProvider.showAfterPlayAd();
    }

    @Override
    public void onStart() {
        super.onStart();
        Ln.d("displaying screen, registering invitation receiver");
        mMultiplayer.addInvitationListener(mScreenInvitationListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        Ln.d(this + " screen partially hidden - persisting player name");
        parent().dismissTutorial();
        if (mSettings.showProgressHelp()) {
            mSettings.hideProgressHelp();
        }
        setPlayerName();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMultiplayer.removeInvitationListener(mScreenInvitationListener);
    }

    @Override
    public void vsAndroid() {
        UiEvent.send("vsAndroid");
        setPlayerName();
        PlayerOpponent player = createPlayerOpponent();
        Opponent android = createAiOpponent();

        Session session = new Session(player, android);
        player.setOpponent(android);
        android.setOpponent(new DelayedOpponent(player));
//        Session.bindOpponents(delegate, android);
        setScreen(ScreenCreator.newBoardSetupScreen(new AndroidGame(), session));
    }

    private void setPlayerName() {
        String playerName = mLayout.getPlayerName();
        if (!playerName.equals(mSettings.getPlayerName())) {
            mSettings.setPlayerName(playerName);
        }
    }

    @NonNull
    private Opponent createAiOpponent() {
        return mOpponentFactory.createPlayer(getString(R.string.android), mPlacement, mRules);
    }

    @NonNull
    private PlayerOpponent createPlayerOpponent() {
        String playerName = mLayout.getPlayerName();
        if (TextUtils.isEmpty(playerName)) {
            playerName = getString(R.string.player);
            Ln.i("player name is empty - replaced by " + playerName);
        }
        PlayerOpponent player = mPlayerFactory.createPlayer(playerName, mPlacement, mRules);
        player.setChatListener(parent());
        return player;
    }

    @Override
    public void viaBlueTooth() {
        // If BT is not on, request that it be enabled.
        boolean enabled = mDevice.bluetoothEnabled();
        UiEvent.send("viaBluetooth", enabled ? 1 : 0);
        if (enabled) {
            showBluetoothScreen();
        } else {
            Ln.d("Bluetooth available, but not enabled - prompt to enable");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (mDevice.canResolveIntent(enableIntent)) {
                startActivityForResult(enableIntent, BattleshipActivity.RC_ENABLE_BT);
            } else {
                Ln.w("Bluetooth resolver is not available");
                ExceptionEvent.send("bt_error");
                showBtErrorDialog();
            }
        }
    }

    private void showBluetoothScreen() {
        BluetoothAdapterWrapper adapter = new BluetoothAdapterWrapper(BluetoothAdapter.getDefaultAdapter());
        setScreen(ScreenCreator.newBluetoothScreen(adapter));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Ln.v("result=" + resultCode + ", request=" + requestCode + ", data=" + data);
        if (requestCode == BattleshipActivity.RC_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            showBluetoothScreen();
        }
    }

    @Override
    public void viaInternet() {
        boolean signedIn = mApiClient.isConnected();
        UiEvent.send("viaInternet", signedIn ? 1 : 0);

        if (signedIn) {
            showInternetGameScreen();
        } else {
            Ln.d("user is not signed in - ask to sign in");
            showInternetDialog();
        }
    }

    @Override
    public void showHelp() {
        parent().showTutorial(mTutView);
    }

    @Override
    public void dismissTutorial() {
        mSettings.hideProgressHelp();
        parent().dismissTutorial();
    }

    private void showInternetGameScreen() {
        setScreen(ScreenCreator.newInternetGameScreen());
    }

    private void showInternetDialog() {
        new SignInDialog.Builder().setMessage(R.string.internet_request).setPositiveButton(R.string.sign_in, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                UiEvent.send("sign_in", "internet");
                mViaInternetRequested = true;
                mApiClient.connect();
            }
        }).create().show(mFm, DIALOG);
    }

    private void showBtErrorDialog() {
        final FragmentManager fm = getFragmentManager();
        new AlertDialogBuilder().setMessage(R.string.bluetooth_not_available).setPositiveButton(R.string.ok).create().show(fm, null);
    }

    @Override
    public void onSignInSucceeded() {
        if (mLayout == null) {
            // TODO: check if it is possible
            Ln.d("signed in before layout created - defer setting name");
            return;
        }

        mLayout.setPlayerName(mSettings.getPlayerName());
        if (mViaInternetRequested) {
            mViaInternetRequested = false;
            showInternetGameScreen();
        }
    }

    @Override
    public void showRanks() {
        UiEvent.send("showRanks");
        setScreen(ScreenCreator.newRanksListScreen());
    }

    @Override
    public void onBackPressed() {
        setScreen(ScreenCreator.newMainScreen());
    }

    @Override
    public int getMusic() {
        return R.raw.intro_music;
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }

}
