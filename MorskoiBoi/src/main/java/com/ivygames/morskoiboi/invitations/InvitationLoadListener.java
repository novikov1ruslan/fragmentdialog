package com.ivygames.morskoiboi.invitations;

import android.support.annotation.NonNull;

import com.google.android.gms.games.multiplayer.Invitation;

import java.util.Collection;

public interface InvitationLoadListener {
    void onResult(@NonNull Collection<Invitation> invitations);
}
