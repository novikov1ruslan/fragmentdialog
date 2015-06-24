package com.ivygames.morskoiboi.ui;

import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.ui.view.RanksLayout;

import org.commons.logger.Ln;

public class RanksListScreen extends BattleshipScreen implements BackPressListener {
	private static final String TAG = "RANKS";
	private RanksLayout mLayout;

	@Override
	public View onCreateView(ViewGroup container) {
		mLayout = (RanksLayout) getLayoutInflater().inflate(R.layout.ranks_list, container, false);
		mLayout.setTotalScore(GameSettings.get().getProgress().getRank());

		Ln.d(this + " screen created");
		return mLayout;
	}

	@Override
	public View getView() {
		return mLayout;
	}

	@Override
	public void onBackPressed() {
		mParent.setScreen(new SelectGameScreen());
	}

	@Override
	public String toString() {
		return TAG + debugSuffix();
	}
}
