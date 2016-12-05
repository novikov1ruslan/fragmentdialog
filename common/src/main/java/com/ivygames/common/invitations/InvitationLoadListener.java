package com.ivygames.common.invitations;

import android.support.annotation.NonNull;

import java.util.Collection;

public interface InvitationLoadListener {
    void onLoaded(@NonNull Collection<GameInvitation> invitations);
}
