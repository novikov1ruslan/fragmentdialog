package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationBuffer;
import com.google.android.gms.games.multiplayer.Invitations;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.ivygames.morskoiboi.rt.InvitationEvent;

import org.commons.logger.Ln;

import java.util.HashSet;
import java.util.Set;

import de.greenrobot.event.EventBus;

public class InvitationManager implements OnInvitationReceivedListener, ResultCallback<Invitations.LoadInvitationsResult> {
    public interface InvitationReceivedListener {
        void showReceivedInvitationCrouton(String displayName);
    }

    @NonNull
    private final InvitationReceivedListener mListener;

    @NonNull
    private final Set<String> mIncomingInvitationIds = new HashSet<>();

    public InvitationManager(@NonNull InvitationReceivedListener listener) {
        mListener = listener;
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        String displayName = invitation.getInviter().getDisplayName();
        Ln.d("received invitation from: " + displayName);
        mListener.showReceivedInvitationCrouton(displayName);
        mIncomingInvitationIds.add(invitation.getInvitationId());
        EventBus.getDefault().post(new InvitationEvent(mIncomingInvitationIds));
    }

    @Override
    public void onInvitationRemoved(String invitationId) {
        Ln.d("invitationId=" + invitationId + " withdrawn");
        mIncomingInvitationIds.remove(invitationId);
        EventBus.getDefault().post(new InvitationEvent(mIncomingInvitationIds));
    }

    @Override
    public void onResult(@NonNull Invitations.LoadInvitationsResult list) {
        mIncomingInvitationIds.clear();
        if (list.getInvitations().getCount() > 0) {
            InvitationBuffer invitations = list.getInvitations();
            Ln.v("loaded " + invitations.getCount() + " invitations");
            for (int i = 0; i < invitations.getCount(); i++) {
                mIncomingInvitationIds.add(invitations.get(i).getInvitationId());
            }
            list.getInvitations().release();
        } else {
            Ln.v("no invitations");
        }
        EventBus.getDefault().post(new InvitationEvent(mIncomingInvitationIds));
    }

    public boolean hasInvitation() {
        return mIncomingInvitationIds.size() > 0;
    }

    public void registerInvitationListener(GoogleApiClientWrapper mGoogleApiClient) {
        mGoogleApiClient.registerInvitationListener(this);
        loadInvitations(mGoogleApiClient);
    }

    public void loadInvitations(GoogleApiClientWrapper mGoogleApiClient) {
        Ln.d("loading invitations...");
        PendingResult<Invitations.LoadInvitationsResult> invitations = mGoogleApiClient.loadInvitations();
        invitations.setResultCallback(this);
    }
}