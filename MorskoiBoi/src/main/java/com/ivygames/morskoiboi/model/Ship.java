package com.ivygames.morskoiboi.model;

import android.support.annotation.NonNull;

public class Ship {

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    private final int mSize;
    Orientation mOrientation;
    //TODO: store it on board
    int mX;
    int mY;
    int mHealth;

    public static boolean isInShip(@NonNull Vector2 v, @NonNull Ship ship) {
        return isInShip(v, ship, ship.getPosition());
    }

    public static boolean isInShip(@NonNull Vector2 v, @NonNull Ship ship, @NonNull Vector2 shipPisition) {
        int x = shipPisition.getX();
        int y = shipPisition.getY();

        int i = v.getX();
        int j = v.getY();

        if (ship.isHorizontal()) {
            return i >= x && i < x + ship.getSize() && j == y;
        } else {
            return j >= y && j < y + ship.getSize() && i == x;
        }
    }

    public Ship(int size, Orientation orientation) {
        mSize = size;
        mOrientation = orientation;
        mHealth = size;
    }

    /**
     * Creates a ship with default (horizontal) orientation
     *
     * @param size size of the ship - number of squares it will occupy when placed on a board
     */
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

    //TODO: remove as not used
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

    public Vector2 getPosition() {
        return Vector2.get(mX, mY);
    }

    public Ship setCoordinates(int x, int y) {
        mX = x;
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mSize; i++) {
            if (mHealth > i) {
                sb.append('*');
            } else {
                sb.append('x');
            }
        }
        sb.append(isHorizontal() ? "(h)" : "(v)");

        return sb.toString();
    }
}
