package com.ivygames.morskoiboi.invitations;

import java.util.Collection;
import java.util.Set;

public interface InvitationReceivedListener {
    void onInvitationReceived(GameInvitation invitation);

    void onInvitationsUpdated(Set<String> invitationIds);
}
