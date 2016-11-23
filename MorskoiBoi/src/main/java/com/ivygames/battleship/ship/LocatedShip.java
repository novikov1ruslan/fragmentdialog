package com.ivygames.battleship.ship;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Vector;

public class LocatedShip {

    @NonNull
    public final Ship ship;
    @NonNull
    public final Vector coordinate;

    public LocatedShip(@NonNull Ship ship) {
        this(ship, Vector.INVALID_VECTOR);
    }

    public LocatedShip(@NonNull Ship ship, @NonNull Vector coordinate) {
        this.ship = ship;
        this.coordinate = coordinate;
    }

    public LocatedShip(@NonNull Ship ship, int i, int j) {
        this(ship, Vector.get(i, j));
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
