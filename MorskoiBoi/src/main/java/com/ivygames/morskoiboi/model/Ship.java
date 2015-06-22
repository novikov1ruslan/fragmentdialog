package com.ivygames.morskoiboi.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Ship {
	private static final String HEALTH = "health";
	private static final String Y = "y";
	private static final String X = "x";
	private static final String IS_HORIZONTAL = "is_horizontal";
	private static final String SIZE = "size";

	public enum Orientation {
		HORIZONTAL, VERTICAL
	}

	private final int mSize;
	private Orientation mOrientation;
	private int mX;
	private int mY;
	private int mHealth;

	public JSONObject toJson() {
		JSONObject shipJson = new JSONObject();
		try {
			shipJson.put(SIZE, getSize());
			shipJson.put(IS_HORIZONTAL, isHorizontal());
			shipJson.put(X, mX);
			shipJson.put(Y, mY);
			shipJson.put(HEALTH, mHealth);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

		return shipJson;
	}

	public static Ship fromJson(String json) {
		try {
			return Ship.fromJson(new JSONObject(json));
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public static Ship fromJson(JSONObject json) {
		try {
			Ship ship = new Ship(json.getInt(SIZE));
			ship.mOrientation = json.getBoolean(IS_HORIZONTAL) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
			ship.setX(json.getInt(X));
			ship.setY(json.getInt(Y));
			ship.mHealth = json.getInt(HEALTH);
			return ship;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isInShip(Ship ship, int i, int j) {
		int x = ship.getX();
		int y = ship.getY();

		if (ship.isHorizontal()) {
			return i >= x && i < x + ship.getSize() && j == y;
		} else {
			return j >= y && j < y + ship.getSize() && i == x;
		}
	}

	// TODO: move to unit test
	public static boolean isInProximity(Ship ship, int i, int j) {
		boolean isHorizontal = ship.isHorizontal();
		int x = ship.getX();
		int y = ship.getY();

		if (isHorizontal) {
			return i >= x - 1 && i <= x + ship.getSize() && j >= y - 1 && j <= y + 1;
		} else {
			return i >= x - 1 && i <= x + 1 && j >= y - 1 && j <= y + ship.getSize();
		}
	}

	public Ship(int size, Orientation orientation) {
		mSize = size;
		mOrientation = orientation;
		mHealth = size;
	}

	public Ship(int size) {
		this(size, Orientation.HORIZONTAL);
	}

	public Ship(Ship ship) {
		mSize = ship.mSize;
		mOrientation = ship.mOrientation;
		mX = ship.mX;
		mY = ship.mY;
		mHealth = ship.mHealth;
	}

	public boolean isHorizontal() {
		return mOrientation == Orientation.HORIZONTAL;
	}

	public int getSize() {
		return mSize;
	}

	public int getX() {
		return mX;
	}

	public Ship setX(int x) {
		mX = x;
		return this;
	}

	public int getY() {
		return mY;
	}

	public Ship setY(int y) {
		mY = y;
		return this;
	}

	/**
	 * reverses the orientation
	 */
	public void rotate() {
		if (mOrientation == Orientation.HORIZONTAL) {
			mOrientation = Orientation.VERTICAL;
		} else {
			mOrientation = Orientation.HORIZONTAL;
		}
	}

	/**
	 * @return true if {@link #shoot()} was called on this ship at least {@link #getSize()} times
	 */
	public boolean isDead() {
		return mHealth == 0;
	}

	public void shoot() {
		if (mHealth > 0) {
			mHealth--;
		}
	}

	public int getHealth() {
		return mHealth;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mSize;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Ship other = (Ship) obj;
		if (mHealth != other.mHealth) {
			return false;
		}
		if (mOrientation != other.mOrientation) {
			return false;
		}
		if (mSize != other.mSize) {
			return false;
		}
		if (mX != other.mX) {
			return false;
		}
		return mY == other.mY;
	}

	@Override
	public String toString() {
		StringBuilder ship = new StringBuilder();
		for (int i = 0; i < mSize; i++) {
			if (mHealth > i) {
				ship.append('*');
			} else {
				ship.append('x');
			}
		}
		ship.append(isHorizontal() ? "(h)" : "(v)");

		return ship.toString();
	}
}
