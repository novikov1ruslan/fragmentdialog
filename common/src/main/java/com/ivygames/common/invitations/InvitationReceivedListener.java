package com.ivygames.common.invitations;

import com.ivygames.common.googleapi.GameInvitation;

import java.util.Set;

public interface InvitationReceivedListener {
    void onInvitationReceived(GameInvitation invitation);

    void onInvitationsUpdated(Set<String> invitationIds);
}
