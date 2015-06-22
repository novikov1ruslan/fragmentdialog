package com.ivygames.morskoiboi.model;

import org.commons.logger.Ln;

import java.util.Collection;

public abstract class Game {
	public enum Type {
		VS_ANDROID, BLUETOOTH, INTERNET
	}

	private static final int MAX_TIME_MILLIS = 300000; // 5 minutes
	private static final int MIN_TIME_MILLIS = 20000; // 20 sec

	private static final int SHIP_1X_WEIGHT = 1;
	private static final int SHIP_2X_WEIGHT = 3;
	private static final int SHIP_3X_WEIGHT = 6;
	private static final int SHIP_4X_WEIGHT = 10;

	private static final int SHELL_UNIT_BONUS = 100;
	private static final int SHIP_UNIT_BONUS = 230; // (long) (SHELL_UNIT_BONUS
													// *
													// 2.3); //
													// 80shells/35weights = 2.3
	private static final int COMBO_UNIT_BONUS = 800; // 8 * SHELL_UNIT_BONUS

	private static final float MAX_TIME_BONUS_MULTIPLIER = 2f;
	private static final float MIN_TIME_BONUS_MULTIPLIER = 1f;
	private static final int INITIAL_SHELLS_NUMBER = 100; // 10 * 10

	public static final int SURRENDER_PENALTY_PER_DECK = 100;
	public static final int MIN_SURRENDER_PENALTY = 1000;

	private int mShells = INITIAL_SHELLS_NUMBER;
	private long mTimeSpent;
	private Ship mLastShip;
	private int mCombo;
	private boolean mFinished;

	public int getShells() {
		return mShells;
	}

	public int getCombo() {
		return mCombo;
	}

	public long getTimeSpent() {
		return mTimeSpent;
	}

	public void setTimeSpent(long millis) {
		Ln.d("time spent in game = " + millis);
		mTimeSpent = millis;
	}

	public abstract Type getType();

	public void updateWithNewShot(Ship ship, Cell cell) {
		mShells--;

		// used for bonus scores calculation
		if (ship != null && mLastShip != null) {
			mCombo++;
			Ln.d("combo! " + mCombo);
		}

		if (cell.isMiss()) {
			mLastShip = null;
		} else if (ship != null) {
			Ln.v("sank");
			mLastShip = ship;
		}
	}

	private int calcComboBonus() {
		return getCombo() * COMBO_UNIT_BONUS;
	}

	public int calcTotalScores(Collection<Ship> ships) {
		float timeMultiplier = getTimeMultiplier();
		int shellsBonus = calcShellsBonus();
		int shipsBonus = calcSavedShipsBonus(ships);
		int comboBonus = calcComboBonus();

		return (int) (shellsBonus * timeMultiplier) + shipsBonus + comboBonus;
	}

	private int calcShellsBonus() {
		int shells = getShells();
		return shells * SHELL_UNIT_BONUS;
	}

	private float getTimeMultiplier() {
		long millis = getTimeSpent();

		if (millis >= MAX_TIME_MILLIS) {
			return MIN_TIME_BONUS_MULTIPLIER;
		}

		if (millis <= MIN_TIME_MILLIS) {
			return MAX_TIME_BONUS_MULTIPLIER;
		}

		return MIN_TIME_BONUS_MULTIPLIER + (float) (MAX_TIME_MILLIS - millis) / (float) (MAX_TIME_MILLIS - MIN_TIME_MILLIS);
	}

	private int calcSavedShipsBonus(Collection<Ship> ships) {

		int shipsWeight = 0;
		for (Ship ship : ships) {
			if (ship.isDead()) {
				continue;
			}

			switch (ship.getSize()) {
			case 1:
				shipsWeight += SHIP_1X_WEIGHT;
				break;
			case 2:
				shipsWeight += SHIP_2X_WEIGHT;
				break;
			case 3:
				shipsWeight += SHIP_3X_WEIGHT;
				break;
			case 4:
				shipsWeight += SHIP_4X_WEIGHT;
				break;
			default:
				Ln.e("impossible ship size = " + ship.getSize());
				break;
			}
		}
		return shipsWeight * SHIP_UNIT_BONUS;
	}

	public void finish() {
		mFinished = true;
		finishMatch();
	}

	public boolean hasFinished() {
		return mFinished;
	}

	public void finishMatch() {

	}

	/**
	 * @return timeout in seconds
	 */
	public abstract int getTurnTimeout();

	public void clearState() {
		mShells = INITIAL_SHELLS_NUMBER;
		mTimeSpent = 0;
		mLastShip = null;
		mCombo = 0;
		Ln.d("game state cleared");
	}

	@Override
	public String toString() {
		return "[type=" + getType() + ", shells=" + mShells + ", timeSpent=" + mTimeSpent + ", lastShip=" + mLastShip + ", combo=" + mCombo + "]";
	}

}
