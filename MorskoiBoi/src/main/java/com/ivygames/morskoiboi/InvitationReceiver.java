package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import java.util.Set;

public interface InvitationReceiver {
    void onInvitationsUpdated(@NonNull Set<String> invitations);
}
