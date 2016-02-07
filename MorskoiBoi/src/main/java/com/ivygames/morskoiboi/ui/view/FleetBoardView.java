package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.ivygames.morskoiboi.R;

public class FleetBoardView extends BaseBoardView {

    public FleetBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected BasePresenter getPresenter() {
        return new BasePresenter(10, getResources().getDimension(R.dimen.ship_border));
    }

}
