package com.ivygames.morskoiboi.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

public abstract class Screen {

	protected BattleshipActivity mParent;

	public abstract View getView();

	protected final View inflate(int layoutId) {
		return mParent.getLayoutInflater().inflate(layoutId, null);
	}

	public final BattleshipActivity getActivity() {
		return mParent;
	}

	protected final void setScreen(Screen screen) {
		mParent.setScreen(screen);
	}

	protected final String getString(int resId) {
		return mParent.getString(resId);
	}

	protected final String getString(int resId, Object... formatArgs) {
		return mParent.getString(resId, formatArgs);
	}

	protected final void startActivity(Intent intent) {
		mParent.startActivity(intent);
	}

	protected final void startActivityForResult(Intent intent, int requestCode) {
		mParent.startActivityForResult(intent, requestCode);
	}

	protected final LayoutInflater getLayoutInflater() {
		return mParent.getLayoutInflater();
	}
}
