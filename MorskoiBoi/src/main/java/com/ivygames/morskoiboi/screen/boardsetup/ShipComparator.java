package com.ivygames.morskoiboi.screen.boardsetup;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.Ship;

import java.util.Comparator;

final class ShipComparator implements Comparator<Ship> {

    @Override
    public int compare(@NonNull Ship lhs, @NonNull Ship rhs) {
        return rhs.size - lhs.size;
    }
}