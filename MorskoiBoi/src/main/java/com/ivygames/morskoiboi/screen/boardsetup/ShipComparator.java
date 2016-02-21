package com.ivygames.morskoiboi.screen.boardsetup;

import com.ivygames.morskoiboi.model.Ship;

import java.util.Comparator;

final class ShipComparator implements Comparator<Ship> {

    @Override
    public int compare(Ship lhs, Ship rhs) {
        return rhs.getSize() - lhs.getSize();
    }
}