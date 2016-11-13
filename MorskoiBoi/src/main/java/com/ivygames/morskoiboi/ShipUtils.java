package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Ship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

// TODO: write unit tests
public class ShipUtils {
    public static boolean similar(Ship ship1, Ship ship2) {
        if (ship1.isDead() && !ship1.isDead()) {
            return false;
        }
        if (ship1.isHorizontal() && !ship2.isHorizontal()) {
            return false;
        }
        if (ship1.getHealth() != ship2.getHealth()) {
            return false;
        }
        if (ship1.getX() != ship2.getX()) {
            return false;
        }
        if (ship1.getY() != ship2.getY()) {
            return false;
        }

        return true;
    }

    public static class OrientationBuilder {
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

    @NonNull
    public static Collection<Ship> generateFullHorizontalFleet(@NonNull int[] allShipsSizes,
                                                               @NonNull Random random) {
        OrientationBuilder orientationBuilder = new OrientationBuilder(random) {
            @Override
            public Ship.Orientation nextOrientation() {
                return Ship.Orientation.HORIZONTAL;
            }
        };

        return generateFullFleet(allShipsSizes, orientationBuilder);
    }

    @NonNull
    public static Collection<Ship> generateFullFleet(@NonNull int[] allShipsSizes,
                                                     @NonNull OrientationBuilder orientationBuilder) {
        List<Ship> fleet = new ArrayList<>();
        for (int length : allShipsSizes) {
            fleet.add(new Ship(length, orientationBuilder.nextOrientation()));
        }

        return fleet;
    }
}
