package com.ivygames.common.achievements;

import android.support.annotation.NonNull;

class GameAchievement {
    @NonNull
    public final String id;
    @NonNull
    public final String name;
    public final int state;

    GameAchievement(@NonNull String id, @NonNull String name, int state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    @Override
    public String toString() {
        return "{id='" + id + ", name='" + name + ", state=" + state + "}";
    }
}
