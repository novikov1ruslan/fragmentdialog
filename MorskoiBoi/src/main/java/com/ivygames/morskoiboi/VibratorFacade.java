package com.ivygames.morskoiboi;

import android.content.Context;
import android.os.Build;
import android.os.Vibrator;

import com.ivygames.morskoiboi.ui.BattleshipFragment;

public class VibratorFacade {

	private static final int MIN_VERSION_SUPPORTING_HAS_VIBRATOR = 11;
	private final Vibrator mVibrator;
	private final BattleshipFragment mFragment;

	public VibratorFacade(BattleshipFragment fragment) {
		mFragment = fragment;
		mVibrator = (Vibrator) fragment.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void vibrate(int milliseconds) {
		if (GameSettings.get().isVibrationOn() && hasVibrator() && mFragment.isResumed()) {
			mVibrator.vibrate(milliseconds);
		}
	}

	public boolean hasVibrator() {
		boolean has = mVibrator != null;
		if (has && Build.VERSION.SDK_INT >= MIN_VERSION_SUPPORTING_HAS_VIBRATOR) {
			has &= mVibrator.hasVibrator();
		}
		return has;
	}
}
