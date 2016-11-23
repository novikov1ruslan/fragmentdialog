package com.ivygames.battleship.shot;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.board.LocatedShip;

public class ShotResult {

    @NonNull
    public final Cell cell;
    @NonNull
    public final Vector aim;
    @Nullable
    public final LocatedShip locatedShip;

    public ShotResult(@NonNull Vector aim, @NonNull Cell cell, @Nullable LocatedShip locatedShip) {
        this.cell = cell;
        this.aim = aim;
        this.locatedShip = locatedShip;
    }

    public ShotResult(@NonNull Vector aim, @NonNull Cell cell) {
        this.cell = cell;
        this.aim = aim;
        this.locatedShip = null;
    }

    public boolean isaKill() {
        return locatedShip != null;
    }

    @Override
    public String toString() {
        return aim + "; " + cell + "; " + (locatedShip == null ? "" : locatedShip.toString());
    }
}
