package com.ivygames.morskoiboi.invitations;

public interface InvitationReceivedListener {
    void onInvitationReceived(GameInvitation invitation);

    void onInvitationRemoved(String invitationId);
}
