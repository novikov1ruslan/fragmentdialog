package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rank;

import java.util.Arrays;
import java.util.List;

public class RanksLayout extends NotepadLinearLayout {

    private ListView mRanksListView;
    private TextView mScoreView;
    private final Context mContext;

    public RanksLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mScoreView = (TextView) findViewById(R.id.total_score);
        mRanksListView = (ListView) findViewById(R.id.ranks);

        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(100);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        mRanksListView.setLayoutAnimation(controller);
    }

    public void setTotalScore(int score) {
        mScoreView.setText(Integer.toString(score));
        List<Rank> ranks = Arrays.asList(Rank.values());
        RanksAdapter mRanksAdapter = new RanksAdapter(mContext, ranks, Rank.getBestRankForScore(score));
        mRanksListView.setAdapter(mRanksAdapter);
    }
}
