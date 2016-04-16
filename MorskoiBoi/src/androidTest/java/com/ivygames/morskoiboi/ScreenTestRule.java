package com.ivygames.morskoiboi;

import android.support.test.rule.ActivityTestRule;

import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.invitations.InvitationManager;
import com.ivygames.morskoiboi.progress.ProgressManager;

import static org.mockito.Mockito.mock;

public class ScreenTestRule extends ActivityTestRule<BattleshipActivity> {
    private GoogleApiClientWrapper apiClient;
    private AndroidDevice androidDevice;

    public ScreenTestRule() {
        super(BattleshipActivity.class);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        apiClient = mock(GoogleApiClientWrapper.class);
        Dependencies.injectApiClient(apiClient);
        Dependencies.injectInvitationManager(mock(InvitationManager.class));
        Dependencies.injectInvitationManager(mock(InvitationManager.class));
        Dependencies.injectAchievementsManager(mock(AchievementsManager.class));
        Dependencies.injectProgressManager(mock(ProgressManager.class));

        androidDevice = mock(AndroidDevice.class);
        Dependencies.injectAndroidDevice(androidDevice);
    }

    public GoogleApiClientWrapper getApiClient() {
        return apiClient;
    }

    public AndroidDevice getAndroidDevice() {
        return androidDevice;
    }
}
