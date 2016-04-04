package com.ivygames.morskoiboi;

import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;

import org.commons.logger.Ln;

public class BattleshipGameManager implements OnInvitationReceivedListener {
    public BattleshipGameManager() {
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        Ln.i("invitation received: " + invitation);
    }

    @Override
    public void onInvitationRemoved(String invitationId) {
        Ln.i("invitation withdrawn: " + invitationId);
    }
}