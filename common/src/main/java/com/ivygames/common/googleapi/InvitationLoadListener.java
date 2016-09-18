package com.ivygames.common.googleapi;

import android.support.annotation.NonNull;

import com.ivygames.common.invitations.GameInvitation;

import java.util.Collection;

public interface InvitationLoadListener {
    void onResult(@NonNull Collection<GameInvitation> invitations);
}
