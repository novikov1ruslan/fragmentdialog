package com.ivygames.common.invitations;

import android.support.annotation.NonNull;

public class GameInvitation {
    @NonNull
    public final String name;
    @NonNull
    public final String id;

    public GameInvitation(@NonNull String name, @NonNull String invitationId) {
        this.name = name;
        id = invitationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameInvitation that = (GameInvitation) o;

        if (!name.equals(that.name)) return false;
        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
