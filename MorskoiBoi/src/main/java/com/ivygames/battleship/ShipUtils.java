package com.ivygames.battleship;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.OrientationBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

// TODO: write unit tests
public class ShipUtils {

    public static boolean isInShip(@NonNull Vector v, @NonNull LocatedShip locatedShip) {
        int x = locatedShip.coordinate.x;
        int y = locatedShip.coordinate.y;
        Ship ship = locatedShip.ship;

        int i = v.x;
        int j = v.y;

        if (ship.isHorizontal()) {
            return i >= x && i < x + ship.size && j == y;
        } else {
            return j >= y && j < y + ship.size && i == x;
        }
    }

    public static Collection<Vector> getShipCoordinates(@NonNull Ship ship, @NonNull Vector coordinate) {
        Collection<Vector> coordinates = new ArrayList<>();
        int i = coordinate.x;
        int j = coordinate.y;
        boolean isHorizontal = ship.isHorizontal();
        for (int k = isHorizontal ? i : j; k < (isHorizontal ? i : j) + ship.size; k++) {
            int x = isHorizontal ? k : i;
            int y = isHorizontal ? j : k;
            coordinates.add(Vector.get(x, y));
        }

        return coordinates;
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

    public static boolean onlyHorizontalShips(@NonNull Collection<Ship> ships) {
        for (Ship ship : ships) {
            if (ship.size > 1 && !ship.isHorizontal()) {
                return false;
            }
        }

        return true;
    }

    @NonNull
    public static List<Vector> getCoordinates(@NonNull LocatedShip locatedShip, @NonNull CoordinateType type) {
        List<Vector> coordinates = new ArrayList<>();

        int x = locatedShip.coordinate.x;
        int y = locatedShip.coordinate.y;
        Ship ship = locatedShip.ship;
        boolean horizontal = ship.isHorizontal();

        for (int i = -1; i <= ship.size; i++) {
            for (int j = -1; j < 2; j++) {
                int cellX = x + (horizontal ? i : j);
                int cellY = y + (horizontal ? j : i);
                if (BoardUtils.contains(cellX, cellY)) {
                    Vector v = Vector.get(cellX, cellY);
                    boolean inShip = isInShip(v, locatedShip);
                    if (inShip && !type.isNeighboring()) {
                        coordinates.add(v);
                    } else if (!inShip && type.isNeighboring()) {
                        coordinates.add(v);
                    }
                }
            }
        }

        return coordinates;
    }
}
