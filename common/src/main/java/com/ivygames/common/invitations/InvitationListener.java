package com.ivygames.common.invitations;

import android.support.annotation.NonNull;

import java.util.Set;

public interface InvitationListener {
    void onInvitationReceived(@NonNull GameInvitation invitation);

    void onInvitationsUpdated(@NonNull Set<String> invitationIds);
}
