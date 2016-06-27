package com.ivygames.morskoiboi.screen.gameplay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.view.BaseBoardRenderer;
import com.ivygames.morskoiboi.screen.view.BaseBoardView;
import com.ivygames.morskoiboi.screen.view.BasePresenter;

public class FleetBoardView extends BaseBoardView {

    public FleetBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @NonNull
    @Override
    protected BasePresenter presenter() {
        if (mPresenter == null) {
            mPresenter = new BasePresenter(10, getResources().getDimension(R.dimen.ship_border));
        }

        return mPresenter;
    }

    @NonNull
    @Override
    protected BaseBoardRenderer getRenderer() {
        if (mRenderer == null) {
            mRenderer = new BaseBoardRenderer(getResources());
        }

        return mRenderer;
    }

}
