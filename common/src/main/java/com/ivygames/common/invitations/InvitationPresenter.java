package com.ivygames.common.invitations;

import android.support.annotation.NonNull;

import org.commons.logger.Ln;

import java.util.Set;

public class InvitationPresenter implements InvitationListener {

    @NonNull
    private final InvitationScreen mScreen;
    @NonNull
    private final InvitationManager mInvitationManager;

    public InvitationPresenter(@NonNull InvitationScreen screen,
                               @NonNull InvitationManager invitationManager) {
        mScreen = screen;
        mInvitationManager = invitationManager;
    }

    public void updateInvitations() {
        if (mInvitationManager.getInvitationIds().isEmpty()) {
            Ln.v("there are no pending invitations");
            mScreen.hideInvitation();
        } else {
            Ln.d("there is a pending invitation");
            mScreen.showInvitation();
        }
    }

    @Override
    public void onInvitationReceived(@NonNull GameInvitation invitation) {
        updateInvitations();
    }

    @Override
    public void onInvitationsUpdated(@NonNull Set<String> invitationIds) {
        updateInvitations();
    }
}
