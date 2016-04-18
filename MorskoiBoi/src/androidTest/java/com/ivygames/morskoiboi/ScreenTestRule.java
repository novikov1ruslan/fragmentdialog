package com.ivygames.morskoiboi;

import android.support.test.rule.ActivityTestRule;

import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.invitations.InvitationManager;
import com.ivygames.morskoiboi.progress.ProgressManager;

import static org.mockito.Mockito.mock;

public class ScreenTestRule extends ActivityTestRule<BattleshipActivity> {

    private GoogleApiClientWrapper apiClient;
    private AndroidDevice androidDevice;
    private GameSettings settings;

    public ScreenTestRule() {
        super(BattleshipActivity.class);
        GameConstants.IS_TEST_MODE = false;
        settings = mock(GameSettings.class);
        Dependencies.inject(settings);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        apiClient = mock(GoogleApiClientWrapper.class);
        Dependencies.inject(apiClient);
        Dependencies.inject(mock(InvitationManager.class));
        Dependencies.inject(mock(InvitationManager.class));
        Dependencies.inject(mock(AchievementsManager.class));
        Dependencies.inject(mock(ProgressManager.class));

        androidDevice = mock(AndroidDevice.class);
        Dependencies.inject(androidDevice);
    }

    public GoogleApiClientWrapper getApiClient() {
        return apiClient;
    }

    public AndroidDevice getAndroidDevice() {
        return androidDevice;
    }

    public GameSettings settings() {
        return settings;
    }
}
