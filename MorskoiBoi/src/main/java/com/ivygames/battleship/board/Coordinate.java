package com.ivygames.battleship.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Coordinate {
    private static final Coordinate[][] POOL = new Coordinate[Board.DIMENSION][Board.DIMENSION];
    public static final Coordinate INVALID_VECTOR = new Coordinate(-1, -1);

    static {
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                POOL[i][j] = new Coordinate(i, j);
            }
        }
    }

    public final int i;
    public final int j;

    private Coordinate(int i, int j) {
        this.i = i;
        this.j = j;
    }

    // TODO: test
    public static Coordinate get(int i, int j) {
        if (!containsCell(i, j)) {
            return INVALID_VECTOR;
        }
        return POOL[i][j];
    }

    public static List<Coordinate> getAllCoordinates() {
        ArrayList<Coordinate> coordinates = new ArrayList<>(Board.DIMENSION * Board.DIMENSION);
        for (int i = 0; i < Board.DIMENSION; i++) {
            coordinates.addAll(Arrays.asList(POOL[i]));
        }
        return coordinates;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + i;
        result = prime * result + j;
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
        Coordinate other = (Coordinate) obj;
        if (i != other.i) {
            return false;
        }
        return j == other.j;
    }

    @Override
    public String toString() {
        return "[" + i + "," + j + "]";
    }

    private static boolean containsCell(int i, int j) {
        return i < Board.DIMENSION && i >= 0 && j < Board.DIMENSION && j >= 0;
    }
}
