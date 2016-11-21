package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.LocatedShip;
import com.ivygames.battleship.board.Vector2;
import com.ivygames.battleship.ship.Ship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

// TODO: write unit tests
public class ShipUtils {

    public static boolean isInShip(@NonNull Vector2 v, @NonNull LocatedShip locatedShip) {
        int x = locatedShip.position.x;
        int y = locatedShip.position.y;
        Ship ship = locatedShip.ship;

        int i = v.x;
        int j = v.y;

        if (ship.isHorizontal()) {
            return i >= x && i < x + ship.size && j == y;
        } else {
            return j >= y && j < y + ship.size && i == x;
        }
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
