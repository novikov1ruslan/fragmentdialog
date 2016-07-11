package com.ivygames.morskoiboi.screen;

import android.support.annotation.NonNull;

import com.ivygames.common.googleapi.GameInvitation;
import com.ivygames.common.invitations.InvitationManager;
import com.ivygames.common.invitations.InvitationReceivedListener;

import org.commons.logger.Ln;

import java.util.Set;

public class InvitationPresenter implements InvitationReceivedListener {

    @NonNull
    private final InvitationObserver mObserver;
    @NonNull
    private final InvitationManager mInvitationManager;

    public InvitationPresenter(@NonNull InvitationObserver observer, @NonNull InvitationManager invitationManager) {
        mObserver = observer;
        mInvitationManager = invitationManager;
    }

    public void updateInvitations() {
        if (mInvitationManager.getInvitations().isEmpty()) {
            Ln.v("there are no pending invitations");
            mObserver.hideInvitation();
        } else {
            Ln.d("there is a pending invitation");
            mObserver.showInvitation();
        }
    }

    @Override
    public void onInvitationReceived(GameInvitation invitation) {
        updateInvitations();
    }

    @Override
    public void onInvitationsUpdated(Set<String> invitationIds) {
        updateInvitations();
    }
}
