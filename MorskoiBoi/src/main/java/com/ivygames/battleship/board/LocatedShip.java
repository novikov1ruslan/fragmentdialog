package com.ivygames.battleship.board;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.Ship;

public class LocatedShip {

    @NonNull
    public final Ship ship;
    @NonNull
    public final Coord coordinate;

    public LocatedShip(@NonNull Ship ship) {
        this(ship, Coord.INVALID_VECTOR);
    }

    public LocatedShip(@NonNull Ship ship, @NonNull Coord coordinate) {
        this.ship = ship;
        this.coordinate = coordinate;
    }

    public LocatedShip(@NonNull Ship ship, int i, int j) {
        this(ship, Coord.get(i, j));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocatedShip that = (LocatedShip) o;

        return ship.equals(that.ship);
    }

    @Override
    public int hashCode() {
        return ship.hashCode();
    }

    @Override
    public String toString() {
        return "LocatedShip{" +
                "ship=" + ship +
                ", coordinate=" + coordinate +
                '}';
    }
}
