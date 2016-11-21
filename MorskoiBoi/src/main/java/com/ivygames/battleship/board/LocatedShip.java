package com.ivygames.battleship.board;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.Ship;

public class LocatedShip {

    @NonNull
    public final Ship ship;
    @NonNull
    public final Vector2 position;

    public LocatedShip(@NonNull Ship ship) {
        this(ship, Vector2.INVALID_VECTOR);
    }

    public LocatedShip(@NonNull Ship ship, @NonNull Vector2 position) {
        this.ship = ship;
        this.position = position;
    }

    public LocatedShip(@NonNull Ship ship, int i, int j) {
        this(ship, Vector2.get(i, j));
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
                ", position=" + position +
                '}';
    }
}
