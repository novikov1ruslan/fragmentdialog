package com.ivygames.morskoiboi.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ShotResult {

    @NonNull
    public final Cell cell;
    @NonNull
    public final Vector2 aim;
    @Nullable
    public final Ship ship;

    public ShotResult(@NonNull Vector2 aim, @NonNull Cell cell, @Nullable Ship ship) {
        this.cell = cell;
        this.ship = ship;
        this.aim = aim;
    }

    public ShotResult(@NonNull Vector2 aim, @NonNull Cell cell) {
        this.cell = cell;
        this.ship = null;
        this.aim = aim;
    }

    @Override
    public String toString() {
        return aim + "; " + cell + "; " + (ship == null ? "" : ship.toString());
    }
}
