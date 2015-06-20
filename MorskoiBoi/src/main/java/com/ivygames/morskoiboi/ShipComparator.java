package com.ivygames.morskoiboi;

import java.util.Comparator;

import com.ivygames.morskoiboi.model.Ship;

// TODO: test
public class ShipComparator implements Comparator<Ship> {

	@Override
	public int compare(Ship lhs, Ship rhs) {
		return rhs.getSize() - lhs.getSize();
	}
}