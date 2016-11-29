package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.Rules;
import com.ivygames.morskoiboi.ScreenUtils;

import java.util.List;
import java.util.Random;

class Shooter {

    @NonNull
    private final List<Vector> mShots;
    private final View mView;
    private final int mPadding;
    private int mCurShot;

    Shooter(Rules rules, Random random, View view, int padding) {
        mPadding = padding;
        mShots = Utils.getShots(rules, random);
        mView = view;
    }

    public void shoot() {
        MyProcessor processor = new MyProcessor(10, mPadding);
        processor.measure(mView.getWidth(), mView.getHeight(), mView.getPaddingLeft(), mView.getPaddingTop());

        Vector coordinate = mShots.get(mCurShot);
        mCurShot++;
        int x = processor.getX(coordinate.x);
        int y = processor.getY(coordinate.y);
        ScreenUtils.clickOnEnemyCell(x, y);
    }

    public boolean hasShots() {
        return mCurShot < mShots.size();
    }

}
