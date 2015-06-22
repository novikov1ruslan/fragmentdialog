package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rank;

import org.commons.logger.Ln;

public class SelectGameLayout extends NotepadRelativeLayout implements View.OnClickListener {
	public interface SelectGameActions {
		void vsAndroid();

		void viaBlueTooth();

		void viaInternet();

		void showRanks();
	}

	private SelectGameActions mScreenActions;
	private TextView mPlayerName;
	private ImageView mPlayerRank;
	private TextView mRankText;

	private InvitationButton mViaInternetButton;

	public SelectGameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScreenActions(SelectGameActions screenActions) {
		mScreenActions = screenActions;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (isInEditMode()) {
			return;
		}

		findViewById(R.id.vs_android).setOnClickListener(this);
		findViewById(R.id.via_bluetooth).setOnClickListener(this);
		mViaInternetButton = (InvitationButton) findViewById(R.id.via_internet);
		mViaInternetButton.setOnClickListener(this);
		mPlayerName = (TextView) findViewById(R.id.player_name);
		mPlayerRank = (ImageView) findViewById(R.id.player_rank);
		mRankText = (TextView) findViewById(R.id.rank_text);

		mPlayerRank.setOnClickListener(this);
	}

	public String getPlayerName() {
		return mPlayerName.getText().toString();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.vs_android:
			mScreenActions.vsAndroid();
			break;
		case R.id.via_bluetooth:
			mScreenActions.viaBlueTooth();
			break;
		case R.id.via_internet:
			mScreenActions.viaInternet();
			break;
		case R.id.player_rank:
			mScreenActions.showRanks();
			break;
		default:
			Ln.w("unprocessed select button =" + v.getId());
			break;
		}
	}

	public void hideBluetooth() {
		findViewById(R.id.via_bluetooth).setVisibility(GONE);
	}

	public void setPlayerName(CharSequence name) {
		mPlayerName.setText(name);
	}

	public void setRank(Rank rank) {
		mRankText.setText(rank.getNameRes());
		mPlayerRank.setImageResource(rank.getBitmapRes());
		if (rank.compareTo(Rank.ENSIGN) < 0) {
			mPlayerRank.setBackgroundResource(R.drawable.enlisted_rank_background);
		} else {
			mPlayerRank.setBackgroundResource(R.drawable.officer_rank_background);
		}
	}

	public void hideInvitation() {
		mViaInternetButton.hideInvitation();
	}

	public void showInvitation() {
		mViaInternetButton.showInvitation();
	}
}
