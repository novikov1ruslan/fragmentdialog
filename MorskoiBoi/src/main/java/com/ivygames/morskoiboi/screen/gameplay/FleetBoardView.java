package com.ivygames.morskoiboi.screen.gameplay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.renderer.BaseBoardRenderer;
import com.ivygames.morskoiboi.renderer.BaseGeometryProcessor;
import com.ivygames.morskoiboi.screen.view.BaseBoardView;

public class FleetBoardView extends BaseBoardView {

    public FleetBoardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @NonNull
    @Override
    protected BaseBoardRenderer renderer() {
        BaseGeometryProcessor presenter = new BaseGeometryProcessor(10, getResources().getDimension(R.dimen.ship_border));
        return new BaseBoardRenderer(getResources(), presenter);
    }

}
