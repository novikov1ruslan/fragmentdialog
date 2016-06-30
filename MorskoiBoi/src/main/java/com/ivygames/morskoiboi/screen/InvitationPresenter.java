package com.ivygames.morskoiboi.screen;

import android.support.annotation.NonNull;

import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.ivygames.morskoiboi.invitations.InvitationManager;

import org.commons.logger.Ln;

public class InvitationPresenter implements OnInvitationReceivedListener {

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
    public void onInvitationReceived(Invitation invitation) {
        updateInvitations();
    }

    @Override
    public void onInvitationRemoved(String s) {
        updateInvitations();
    }
}
