package com.ivygames.morskoiboi.rt;

import android.support.annotation.NonNull;

import java.util.Set;

public class InvitationEvent {

    @NonNull
    public final Set<String> invitations;

    public InvitationEvent(@NonNull Set<String> invitations) {
        this.invitations = invitations;
    }

}
