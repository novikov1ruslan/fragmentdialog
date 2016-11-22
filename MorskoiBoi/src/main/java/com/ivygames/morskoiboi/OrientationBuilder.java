package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.Ship;

import java.util.Random;

public class OrientationBuilder {
    @NonNull
    private final Random mRandom;

    public OrientationBuilder(@NonNull Random random) {
        mRandom = random;
    }

    public Ship.Orientation nextOrientation() {
        return calcRandomOrientation(mRandom);
    }

    @NonNull
    private static Ship.Orientation calcRandomOrientation(@NonNull Random random) {
        return random.nextInt(2) == 1 ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
    }
}
