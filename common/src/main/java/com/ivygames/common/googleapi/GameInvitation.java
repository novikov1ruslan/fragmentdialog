package com.ivygames.common.googleapi;

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
}
