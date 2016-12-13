package com.ivygames.battleship;

import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Vector;

import java.util.Collection;

public class VectorUtils {

    public static Vector any(@NonNull Collection<Vector> collection) {
        return collection.iterator().next();
    }
}
