package com.ivygames.morskoiboi.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

public final class GameUtils {

	private GameUtils() {
		// utility
	}

	public static Collection<Ship> populateFullHorizontalFleet(Collection<Ship> ships) {
		ships.add(new Ship(1));
		ships.add(new Ship(1));
		ships.add(new Ship(1));
		ships.add(new Ship(1));
		ships.add(new Ship(2));
		ships.add(new Ship(2));
		ships.add(new Ship(2));
		ships.add(new Ship(3));
		ships.add(new Ship(3));
		ships.add(new Ship(4));

		return ships;
	}

	public static Collection<Ship> generateFullHorizontalFleet() {
		return GameUtils.populateFullHorizontalFleet(new ArrayList<Ship>());
	}

	/**
	 * @return true if the cells are aligned horizontally
	 */
	public static boolean areCellsHorizontal(List<Vector2> vector) {

		int y = vector.get(0).getY();

		for (int i = 1; i < vector.size(); i++) {
			if (y != vector.get(i).getY()) {
				return false;
			}
		}

		return true;
	}

	// /**
	// * @param vector
	// * @return true if the cells are aligned horizontally
	// */
	// public static boolean areCellsHorizontalFast(Collection<Vector2> vector)
	// {
	// if (vector.size() == 1) {
	// return true;
	// }
	//
	// vector.
	// }

	public static void rateApp(Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));
		context.startActivity(intent);
	}
}
