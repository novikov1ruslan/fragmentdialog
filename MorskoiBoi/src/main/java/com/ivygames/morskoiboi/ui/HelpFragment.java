package com.ivygames.morskoiboi.ui;

import org.commons.logger.Ln;

import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;

public class HelpFragment extends BattleshipFragment implements BackPressListener {
	private static final String TAG = "HELP";
	private View mLayout;

	@Override
	public View onCreateView(ViewGroup container) {
		mLayout = getLayoutInflater().inflate(R.layout.help, container, false);
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
