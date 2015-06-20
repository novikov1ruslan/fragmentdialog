package com.ivygames.morskoiboi.ui;

import org.commons.logger.Ln;

import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.ui.view.RanksLayout;

public class RanksListFragment extends BattleshipFragment implements BackPressListener {
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
		mParent.setScreen(new MainFragment());
	}

	@Override
	public String toString() {
		return TAG + debugSuffix();
	}
}
