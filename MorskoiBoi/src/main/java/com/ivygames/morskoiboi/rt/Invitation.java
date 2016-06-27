package com.ivygames.morskoiboi.rt;

import android.support.annotation.NonNull;

import java.util.Set;

// TODO: make immutable
public class Invitation {

    @NonNull
    public final Set<String> invitations;

    public Invitation(@NonNull Set<String> invitations) {
        this.invitations = invitations;
    }

}
