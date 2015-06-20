package com.ivygames.morskoiboi.ui;

import org.commons.logger.Ln;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.DeviceUtils;
import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.VibratorFacade;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.ui.BattleshipActivity.SignInListener;
import com.ivygames.morskoiboi.ui.view.SettingsLayout;
import com.ivygames.morskoiboi.ui.view.SettingsLayout.SettingsScreenActions;
import com.ivygames.morskoiboi.utils.GameUtils;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

public class SettingsFragment extends BattleshipFragment implements SettingsScreenActions, SignInListener, BackPressListener {
	private static final String TAG = "SETTINGS";

	// (arbitrary) request code for the purchase flow
	public static final int RC_PURCHASE = 10003;

	private static final String SKU_NO_ADS = "no_ads";

	private static final String DIALOG = FragmentAlertDialog.TAG;

	private static final String EMAIL = "ivy.games.studio@gmail.com";

	private GameSettings mSettings;
	private SettingsLayout mLayout;
	private VibratorFacade mVibrator;

	@Override
	public void onCreate() {
		super.onCreate();
		mVibrator = new VibratorFacade(this);
		mSettings = GameSettings.get();
	}

	@Override
	public View onCreateView(ViewGroup container) {
		mLayout = (SettingsLayout) getLayoutInflater().inflate(R.layout.settings, container, false);
		mLayout.setScreenActionsListener(this);
		mLayout.setSound(mSettings.isSoundOn());
		if (mVibrator.hasVibrator()) {
			Ln.v("show vibration setting setting");
			mLayout.setVibration(mSettings.isVibrationOn());
		} else {
			Ln.v("device does not support vibration - hide setting");
			mLayout.hideVibrationSetting();
		}

		Intent intent = getEmailIntent(EMAIL);
		if (!DeviceUtils.resolverAvailableForIntent(intent)) {
			mLayout.hideReportProblemButton();
		}

		Ln.d(this + " screen created");
		return mLayout;
	}

	@Override
	public View getView() {
		return mLayout;
	}

	private Intent getEmailIntent(String email) {
		// Intent intent = new Intent(Intent.ACTION_SENDTO);
		// // intent.setData(Uri.parse("mailto:" + email));
		// intent.setData(Uri.fromParts("mailto", "abc@gmail.com", null));
		// intent.setType("text/plain");
		// // intent.setType("message/rfc822");
		// // intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
		// return intent;

		Uri uri = Uri.parse("mailto:" + EMAIL);
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setData(uri);
		return Intent.createChooser(intent, getString(R.string.report_problem));
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mApiClient.isConnected()) {
			mLayout.showSignOutBar();
		} else {
			mLayout.showSignInBar();
		}
	}

	@Override
	public void onSignInSucceeded() {
		mLayout.showSignOutBar();
	}

	@Override
	public void onSoundCahnged() {
		boolean on = !mSettings.isSoundOn();
		mSettings.setSound(on);
		mLayout.setSound(on);
	}

	@Override
	public void onVibrstionChanged() {
		boolean on = !mSettings.isVibrationOn();
		mSettings.setVibration(on);
		mLayout.setVibration(on);
		mVibrator.vibrate(300);
	}

	@Override
	public void onRate() {
		mGaTracker.send(new UiEvent("settings_rate").build());
		mSettings.setRated();
		GameUtils.rateApp(getActivity());
	}

	@Override
	public void onReportProblem() {
		Intent intent = getEmailIntent(EMAIL);
		if (DeviceUtils.resolverAvailableForIntent(intent)) {
			startActivity(intent);
		} else {
			Ln.e("email resolver is not avilable");
		}

		// ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(getActivity());
		// // builder.setType("message/rfc822");
		// builder.
		// builder.setType("text/plain");
		// builder.addEmailTo(EMAIL);
		// builder.setSubject(getString(R.string.app_name));
		// builder.startChooser();
	}

	@Override
	public void onSignIn() {
		mGaTracker.send(new UiEvent(GameConstants.GA_ACTION_SIGN_IN, "settings").build());
		mApiClient.connect();
	}

	@Override
	public void onSignOut() {
		mGaTracker.send(new UiEvent("sign_out", "settings").build());
		mApiClient.disconnect();
		mLayout.showSignInBar();
	}

	// private void showErrorDialog() {
	// new AlertDialogBuilder().setMessage(R.string.error).setPositiveButton(R.string.ok, new OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// mGaTracker.send(new UiEvent("purchase_error", "ok").build());
	// }
	// }).setOnCancelListener(new OnCancelListener() {
	//
	// @Override
	// public void onCancel(DialogInterface dialog) {
	// mGaTracker.send(new UiEvent("purchase_error", "cancel").build());
	// }
	// }).create().show(mFm, DIALOG);
	// }

	@Override
	public void onBackPressed() {
		mParent.setScreen(new MainFragment());
	}

	@Override
	public String toString() {
		return TAG + debugSuffix();
	}
}
