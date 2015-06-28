package com.ivygames.morskoiboi.model;

public class Cell {

    private final static char EMPTY = ' ';
    private final static char RESERVED = '0';
    private final static char MISS = '*';
    private final static char HIT = 'X';

    private char mState;
    private int mProximity;

    private static Cell newEmpty() {
        return new Cell(EMPTY);
    }

    private static Cell newReserved() {
        return new Cell(RESERVED);
    }

    private static Cell newMiss() {
        return new Cell(MISS);
    }

    private static Cell newHit() {
        return new Cell(HIT);
    }

    public static Cell parse(char c) {
        switch (c) {
            case RESERVED:
                return Cell.newReserved();
            case MISS:
                return Cell.newMiss();
            case HIT:
                return Cell.newHit();
            case EMPTY:
                return Cell.newEmpty();
            default:
                return Cell.parseProximityCell(c);
        }
    }

    private static Cell parseProximityCell(char c) {
        if (c >= '0' && c <= '8') {
            Cell cell = Cell.newReserved();
            cell.mProximity = c - '0';
            return cell;
        } else {
            throw new IllegalArgumentException(Character.toString(c));
        }
    }

    public Cell() {
        mState = EMPTY;
    }

    private Cell(char c) {
        mState = c;
    }

    public boolean isEmpty() {
        return mState == EMPTY;
    }

    public boolean isHit() {
        return mState == HIT;
    }

    public void setHit() {
        mState = HIT;
    }

    public boolean isMiss() {
        return mState == MISS;
    }

    public void setMiss() throws IllegalStateException {
        if (mState == HIT) {
            throw new IllegalStateException("wrong state: " + mState);
        }

        mState = MISS;
    }

    public boolean isReserved() {
        return mState == RESERVED;
    }

    public void setReserved() throws IllegalStateException {
        if (mState == HIT) {
            throw new IllegalStateException("wrong state: " + mState);
        }

        if (mState != MISS) {
            mState = RESERVED;
            mProximity++;
        }
    }

    public int getProximity() {
        return mProximity;
    }

    /**
     * sets proximity to 8 and state to {@link #RESERVED}
     */
    public void addShip() {
        mState = RESERVED;
        mProximity += 8;
    }

    // TODO: remove
    public boolean beenShot() {
        return isMiss() || isHit();// || isSunk();
    }

    public char toChar() {
        return mState;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mProximity;
        result = prime * result + mState;
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
        Cell other = (Cell) obj;
        if (mProximity != other.mProximity) {
            return false;
        }
        return mState == other.mState;
    }

    @Override
    public String toString() {
        return mState == RESERVED ? ("[" + mProximity + "]") : ("[" + mState + "]");
    }

}
