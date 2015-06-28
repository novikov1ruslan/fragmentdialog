package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Ship;

import java.util.Comparator;

// TODO: test
public class ShipComparator implements Comparator<Ship> {

    @Override
    public int compare(Ship lhs, Ship rhs) {
        return rhs.getSize() - lhs.getSize();
    }
}