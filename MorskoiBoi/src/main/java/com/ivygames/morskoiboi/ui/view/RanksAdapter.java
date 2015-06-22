package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff.Mode;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rank;

import java.util.List;

class RanksAdapter extends BaseAdapter {

	private final List<Rank> mRanks;
	private Rank mCurrentRank;
	private final ColorMatrixColorFilter mIdentityFilter;
	private final int mTextColor;
	private final float mRankTextSize;
	private final float mCurrentRankTextSize;
	private final int mCurrentTextColor;
	private final LayoutInflater mInflater;

	RanksAdapter(Context context, List<Rank> ranks, Rank curRank) {
		super();
		mRanks = ranks;
		mCurrentRank = curRank;

		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);

		mIdentityFilter = new ColorMatrixColorFilter(new ColorMatrix());

		mTextColor = context.getResources().getColor(R.color.main);

		mRankTextSize = context.getResources().getDimension(R.dimen.rank_textSize);
		mCurrentRankTextSize = context.getResources().getDimension(R.dimen.current_rank_textSize);

		mCurrentTextColor = context.getResources().getColor(R.color.victory_text);

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mRanks.size();
	}

	@Override
	public Object getItem(int index) {
		return mRanks.get(index);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.rank_item, parent, false);
		}

		Rank rank = mRanks.get(position);
		TextView name = (TextView) convertView.findViewById(R.id.rank_name);
		TextView score = (TextView) convertView.findViewById(R.id.rank_points);
		ImageView image = (ImageView) convertView.findViewById(R.id.rank_img);

		name.setText(rank.getNameRes());
		score.setText(Integer.toString(rank.getScore()));
		image.setImageResource(rank.getSmallBitmapRes());
		score.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRankTextSize);

		if (mCurrentRank.getScore() > rank.getScore()) {
			int color = mTextColor & 0x88FFFFFF;
			name.setTextColor(color);
			score.setTextColor(color);

			name.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRankTextSize);
			image.setColorFilter(0x88FFFFFF, Mode.MULTIPLY);
		} else if (mCurrentRank.getScore() < rank.getScore()) {
			int color = Color.BLACK & 0x66FFFFFF;
			name.setTextColor(color);
			score.setTextColor(color);

			name.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRankTextSize);
			image.setImageResource(R.drawable.unknown_rank);
		} else {
			name.setTextColor(mCurrentTextColor);
			score.setTextColor(mTextColor);

			name.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCurrentRankTextSize);
			image.setColorFilter(mIdentityFilter);
		}

		return convertView;
	}

	public void setCurrentRank(Rank rank) {
		mCurrentRank = rank;
		notifyDataSetChanged();
	}

}
