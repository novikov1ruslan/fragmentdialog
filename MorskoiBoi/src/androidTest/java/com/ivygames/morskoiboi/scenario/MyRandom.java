package com.ivygames.morskoiboi.scenario;

import java.util.Random;

class MyRandom extends Random {
    boolean b;

    @Override
    public int nextInt() {
        int i = b ? 0 : 1;
        b = !b;
        return i;
    }

    @Override
    public int nextInt(int n) {
        if (n > 1) {
            return nextInt();
        }

        return 0;
    }
}
