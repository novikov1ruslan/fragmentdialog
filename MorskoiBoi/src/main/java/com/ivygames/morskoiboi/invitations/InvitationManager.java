package com.ivygames.morskoiboi.invitations;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.multiplayer.InvitationBuffer;
import com.google.android.gms.games.multiplayer.Invitations;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;
import com.ivygames.morskoiboi.InvitationReceiver;
import com.ivygames.morskoiboi.rt.Invitation;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvitationManager {

    @Nullable
    private InvitationReceivedListener mListener;

    @NonNull
    private final GoogleApiClientWrapper mGoogleApiClient;

    @NonNull
    private final Set<String> mIncomingInvitationIds = new HashSet<>();

    @NonNull
    private final OnInvitationReceivedListener mInvitationListener = new OnInvitationReceivedListenerImpl();

    @NonNull
    final
    private ResultCallback<Invitations.LoadInvitationsResult> mResultCallback = new LoadInvitationsResultResultCallback();

    @NonNull
    private final List<InvitationReceiver> mInvitationReceivers = new ArrayList<>();

    public InvitationManager(@NonNull GoogleApiClientWrapper client) {
        mGoogleApiClient = client;
    }

    public void setInvitationReceivedListener(@Nullable InvitationReceivedListener listener) {
        mListener = listener;
    }

    public void registerInvitationReceiver(@NonNull InvitationReceiver receiver) {
        mInvitationReceivers.add(receiver);
    }

    public void unregisterInvitationReceiver(@NonNull InvitationReceiver receiver) {
        mInvitationReceivers.remove(receiver);
    }

    public boolean hasInvitation() {
        return mIncomingInvitationIds.size() > 0;
    }

    public void loadInvitations() {
        if (!mGoogleApiClient.isConnected()) {
            Ln.w("API client has to be connected");
            return;
        }
        mGoogleApiClient.registerInvitationListener(mInvitationListener);

        Ln.d("loading invitations...");
        PendingResult<Invitations.LoadInvitationsResult> invitations = mGoogleApiClient.loadInvitations();
        invitations.setResultCallback(mResultCallback);
    }

    private void notifyReceivers() {
        for (InvitationReceiver receiver : mInvitationReceivers) {
            receiver.onNewInvitationReceived(new Invitation(mIncomingInvitationIds));
        }
    }

    private class OnInvitationReceivedListenerImpl implements OnInvitationReceivedListener {

        @Override
        public void onInvitationReceived(com.google.android.gms.games.multiplayer.Invitation invitation) {
            String displayName = invitation.getInviter().getDisplayName();
            Ln.d("received invitation from: " + displayName);
            if (mListener != null) {
                mListener.onInvitationReceived(displayName);
            }
            mIncomingInvitationIds.add(invitation.getInvitationId());
            notifyReceivers();
        }

        @Override
        public void onInvitationRemoved(String invitationId) {
            Ln.d("invitationId=" + invitationId + " withdrawn");
            mIncomingInvitationIds.remove(invitationId);
            notifyReceivers();
        }
    }

    private class LoadInvitationsResultResultCallback implements ResultCallback<Invitations.LoadInvitationsResult> {

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
            notifyReceivers();
        }
    }
}