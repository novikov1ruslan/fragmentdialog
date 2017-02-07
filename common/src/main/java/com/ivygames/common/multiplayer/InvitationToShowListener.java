package com.ivygames.common.multiplayer;

import android.support.annotation.NonNull;

import org.commons.logger.LoggerUtils;
import com.ivygames.common.invitations.GameInvitation;
import com.ivygames.common.invitations.InvitationListener;
import com.ivygames.common.invitations.InvitationShowListener;

import org.commons.logger.Ln;

import java.util.Set;

public class InvitationToShowListener implements InvitationListener {
    @NonNull
    private final RealTimeMultiplayer mMultiplayer;
    @NonNull
    private final InvitationShowListener mScreen;

    public InvitationToShowListener(@NonNull RealTimeMultiplayer multiplayer,
                                    @NonNull InvitationShowListener screen) {
        mMultiplayer = multiplayer;
        mScreen = screen;
    }

    @Override
    public void onInvitationReceived(@NonNull GameInvitation invitation) {
        updateInvitations();
    }

    @Override
    public void onInvitationsUpdated(@NonNull Set<String> invitationIds) {
        updateInvitations();
    }

    private void updateInvitations() {
        if (mMultiplayer.getInvitationIds().isEmpty()) {
            Ln.v("there are no pending invitations");
            mScreen.hideInvitation();
        } else {
            Ln.v("there is a pending invitation");
            mScreen.showInvitation();
        }
    }

    @Override
    public String toString() {
        return LoggerUtils.getSimpleName(this);
    }
}
