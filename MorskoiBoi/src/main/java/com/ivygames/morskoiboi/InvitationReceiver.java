package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.rt.Invitation;

public interface InvitationReceiver {
    void onNewInvitationReceived(@NonNull Invitation event);
}
