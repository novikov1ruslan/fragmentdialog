package com.ivygames.battleship.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Coord {
    private static final Coord[][] POOL = new Coord[Board.DIMENSION][Board.DIMENSION];
    public static final Coord INVALID_VECTOR = new Coord(-1, -1);

    static {
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                POOL[i][j] = new Coord(i, j);
            }
        }
    }

    public final int i;
    public final int j;

    private Coord(int i, int j) {
        this.i = i;
        this.j = j;
    }

    // TODO: test
    public static Coord get(int i, int j) {
        if (!containsCell(i, j)) {
            return INVALID_VECTOR;
        }
        return POOL[i][j];
    }

    public static List<Coord> getAllCoordinates() {
        ArrayList<Coord> coordinates = new ArrayList<>(Board.DIMENSION * Board.DIMENSION);
        for (int i = 0; i < Board.DIMENSION; i++) {
            coordinates.addAll(Arrays.asList(POOL[i]));
        }
        return coordinates;
    }

    @Override
    public String toString() {
        return "[" + i + "," + j + "]";
    }

    private static boolean containsCell(int i, int j) {
        return i < Board.DIMENSION && i >= 0 && j < Board.DIMENSION && j >= 0;
    }
}
