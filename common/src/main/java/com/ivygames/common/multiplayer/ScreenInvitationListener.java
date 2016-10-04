package com.ivygames.common.multiplayer;

import android.support.annotation.NonNull;

import com.ivygames.common.invitations.GameInvitation;
import com.ivygames.common.invitations.InvitationListener;
import com.ivygames.common.invitations.InvitationScreen;

import org.commons.logger.Ln;

import java.util.Set;

public class ScreenInvitationListener implements InvitationListener {
    @NonNull
    private final MultiplayerManager mMultiplayer;
    @NonNull
    private final InvitationScreen mScreen;

    public ScreenInvitationListener(@NonNull MultiplayerManager multiplayer,
                                    @NonNull InvitationScreen screen) {
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
            Ln.d("there are no pending invitations");
            mScreen.hideInvitation();
        } else {
            Ln.d("there is a pending invitation");
            mScreen.showInvitation();
        }
    }
}
