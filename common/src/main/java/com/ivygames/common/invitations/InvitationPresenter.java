package com.ivygames.common.invitations;

import android.support.annotation.NonNull;

import org.commons.logger.Ln;

import java.util.Set;

public class InvitationPresenter implements InvitationListener {

    @NonNull
    private final InvitationObserver mObserver;
    @NonNull
    private final InvitationManager mInvitationManager;

    public InvitationPresenter(@NonNull InvitationObserver observer,
                               @NonNull InvitationManager invitationManager) {
        mObserver = observer;
        mInvitationManager = invitationManager;
    }

    public void updateInvitations() {
        if (mInvitationManager.getInvitationIds().isEmpty()) {
            Ln.v("there are no pending invitations");
            mObserver.hideInvitation();
        } else {
            Ln.d("there is a pending invitation");
            mObserver.showInvitation();
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
