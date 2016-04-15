package com.ivygames.morskoiboi;

import android.support.test.rule.ActivityTestRule;

import com.ivygames.morskoiboi.invitations.InvitationManager;

import static org.mockito.Mockito.mock;

public class ScreenTestRule extends ActivityTestRule<BattleshipActivity> {
    private GoogleApiClientWrapper apiClient;
    private AndroidDevice androidDevice;
    private InvitationManager invitationManager;

    public ScreenTestRule() {
        super(BattleshipActivity.class);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        apiClient = mock(GoogleApiClientWrapper.class);
        Dependencies.injectApiClient(apiClient);
        invitationManager = mock(InvitationManager.class);
        Dependencies.injectInvitationManager(invitationManager);
        androidDevice = mock(AndroidDevice.class);
        AndroidDeviceFactory.inject(androidDevice);
    }

    public GoogleApiClientWrapper getApiClient() {
        return apiClient;
    }

    public AndroidDevice getAndroidDevice() {
        return androidDevice;
    }

    public InvitationManager getInvitationManager() {
        return invitationManager;
    }
}
