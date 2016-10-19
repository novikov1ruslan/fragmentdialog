package com.ivygames.morskoiboi.scenario;

import android.support.annotation.NonNull;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.action.ViewActions;
import android.util.Log;
import android.view.View;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

class Shooter {

    @NonNull
    private final List<Vector2> mShots;
    private final View mView;
    private final int mPadding;
    private int mCurShot;

    Shooter(Rules rules, Placement placement, View view, int padding) {
        mPadding = padding;
        mShots = Utils.getShots(rules, placement);
        mView = view;
    }

    public void shoot() {
        MyProcessor processor = new MyProcessor(10, mPadding);
        processor.measure(mView.getWidth(), mView.getHeight(), mView.getPaddingLeft(), mView.getPaddingTop());

        Vector2 vector2 = mShots.get(mCurShot);
        mCurShot++;
        int x = processor.getX(vector2.getX());
        int y = processor.getY(vector2.getY());
            onView(withId(R.id.enemy_board)).perform(clickXY(x, y));
//        onView(withId(R.id.enemy_board)).perform(ViewActions.click());
    }

    public boolean hasShots() {
        return mCurShot < mShots.size();
    }

    public static ViewAction clickXY(final int x, final int y){
        Log.v("TEST", "clicking on (" + x + "," + y + ")");

        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }
}
