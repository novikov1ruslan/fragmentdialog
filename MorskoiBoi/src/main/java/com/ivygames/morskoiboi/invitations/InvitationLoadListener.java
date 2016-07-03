package com.ivygames.morskoiboi.invitations;

import android.support.annotation.NonNull;

import java.util.Collection;

public interface InvitationLoadListener {
    void onResult(@NonNull Collection<GameInvitation> invitations);
}
