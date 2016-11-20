package com.ivygames.morskoiboi.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ShotResult {

    @NonNull
    public final Cell cell;
    @NonNull
    public final Vector2 aim;
    @Nullable
    public final Board.LocatedShip locatedShip;

    public ShotResult(@NonNull Vector2 aim, @NonNull Cell cell, @Nullable Board.LocatedShip locatedShip) {
        this.cell = cell;
        this.aim = aim;
        this.locatedShip = locatedShip;
    }

    public ShotResult(@NonNull Vector2 aim, @NonNull Cell cell) {
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
