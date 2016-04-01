package com.ivygames.morskoiboi.screen.selectgame;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.ivygames.morskoiboi.AdProviderFactory;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity.SignInListener;
import com.ivygames.morskoiboi.DeviceUtils;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;
import com.ivygames.morskoiboi.PlayerOpponent;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rank;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.ai.AndroidGame;
import com.ivygames.morskoiboi.ai.AndroidOpponent;
import com.ivygames.morskoiboi.ai.DelayedOpponent;
import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.analytics.ExceptionEvent;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.rt.InvitationEvent;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.SignInDialog;
import com.ivygames.morskoiboi.screen.bluetooth.BluetoothScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;
import com.ivygames.morskoiboi.screen.internet.InternetGameScreen;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.screen.ranks.RanksListScreen;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameLayout.SelectGameActions;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import de.greenrobot.event.EventBus;

import static org.apache.commons.lang3.Validate.notNull;

public class SelectGameScreen extends BattleshipScreen implements SelectGameActions, SignInListener, BackPressListener {
    private static final String TAG = "SELECT_GAME";
    private static final String DIALOG = FragmentAlertDialog.TAG;

    private SelectGameLayout mLayout;

    private boolean mViaInternetRequested;

    private View mTutView;

    @NonNull
    private final GoogleApiClientWrapper mApiClient;

    public SelectGameScreen(@NonNull BattleshipActivity parent) {
        super(parent);
        mApiClient = parent.getApiClient();
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (SelectGameLayout) inflate(R.layout.select_game, container);
        mLayout.setScreenActions(this);

        if (!hasBluetooth()) {
            Ln.d("Bluetooth is absent - hiding the BT option");
            mLayout.hideBluetooth();
        }

        GameSettings settings = GameSettings.get();
        mLayout.setPlayerName(settings.getPlayerName());
        Rank rank = Rank.getBestRankForScore(settings.getProgress().getScores());
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
        AdProviderFactory.getAdProvider().showInterstitialAfterPlay();
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
        PackageManager pm = getParent().getPackageManager();
        boolean hasBluetooth = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        hasBluetooth &= BluetoothAdapter.getDefaultAdapter() != null;
        return hasBluetooth;
    }

    @Override
    public void vsAndroid() {
        UiEvent.send("vsAndroid");
        PlacementAlgorithm placement = PlacementFactory.getAlgorithm();
        Rules rules = RulesFactory.getRules();
        AndroidOpponent opponent = new AndroidOpponent(getString(R.string.android), placement, rules, new DelayedOpponent());
        Model.instance.game = new AndroidGame();
        String playerName = mLayout.getPlayerName();
        if (TextUtils.isEmpty(playerName)) {
            playerName = getString(R.string.player);
            Ln.i("player name is empty - replaced by " + playerName);
        }
        Model.instance.setOpponents(new PlayerOpponent(playerName, placement, rules), opponent);
        showBoardSetup();
    }

    private void showBoardSetup() {
        mParent.setScreen(new BoardSetupScreen(getParent()));
    }

    @Override
    public void viaBlueTooth() {
        // If BT is not on, request that it be enabled.
        boolean enabled = BluetoothAdapter.getDefaultAdapter().isEnabled();
        UiEvent.send("viaBluetooth", enabled ? 1 : 0);
        if (enabled) {
            showDeviceListScreen();
        } else {
            Ln.d("Bluetooth available, but not enabled - prompt to enable");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (DeviceUtils.resolverAvailableForIntent(getParent().getPackageManager(), enableIntent)) {
                startActivityForResult(enableIntent, BattleshipActivity.RC_ENABLE_BT);
            } else {
                Ln.w("Bluetooth resolver is not available");
                ExceptionEvent.send("bt_error");
                showBtErrorDialog();
            }
        }
    }

    private void showDeviceListScreen() {
//        mParent.setScreen(new DeviceListScreen());
        mParent.setScreen(new BluetoothScreen(getParent()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Ln.v("result=" + resultCode + ", request=" + requestCode + ", data=" + data);
        if (requestCode == BattleshipActivity.RC_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            showDeviceListScreen();
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
        mParent.showTutorial(mTutView);
    }

    @Override
    public void dismissTutorial() {
        GameSettings.get().hideProgressHelp();
        mParent.dismissTutorial();
    }

    private void showInternetGameScreen() {
        mParent.setScreen(new InternetGameScreen(getParent()));
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
        final FragmentManager fm = getParent().getFragmentManager();
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
        UiEvent.send("showRanks");
        mParent.setScreen(new RanksListScreen(getParent()));
    }

    @Override
    public void onBackPressed() {
        mParent.setScreen(new MainScreen(getParent(), getParent().getApiClient()));
    }

    @Override
    public int getMusic() {
        return R.raw.intro_music;
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }

}
