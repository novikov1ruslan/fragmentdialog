package com.ivygames.battleship.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Vector {
    private static final Vector[][] POOL = new Vector[Board.DIMENSION][Board.DIMENSION];
    public static final Vector INVALID_VECTOR = new Vector(-1, -1);

    static {
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                POOL[i][j] = new Vector(i, j);
            }
        }
    }

    public final int x;
    public final int y;

    private Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // TODO: test
    public static Vector get(int i, int j) {
        if (!containsCell(i, j)) {
            return INVALID_VECTOR;
        }
        return POOL[i][j];
    }

    public static List<Vector> getAllCoordinates() {
        ArrayList<Vector> coordinates = new ArrayList<>(Board.DIMENSION * Board.DIMENSION);
        for (int i = 0; i < Board.DIMENSION; i++) {
            coordinates.addAll(Arrays.asList(POOL[i]));
        }
        return coordinates;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }

    private static boolean containsCell(int i, int j) {
        return i < Board.DIMENSION && i >= 0 && j < Board.DIMENSION && j >= 0;
    }
}
