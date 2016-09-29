package com.ivygames.morskoiboi;

import android.support.test.rule.ActivityTestRule;

import com.ivygames.common.AndroidDevice;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.progress.ProgressManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScreenTestRule extends ActivityTestRule<BattleshipActivity> {

    private ApiClient apiClient;
    private AndroidDevice device;
    private GameSettings settings;

    public ScreenTestRule() {
        super(BattleshipActivity.class);
        settings = mock(GameSettings.class);
        Dependencies.inject(settings);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        apiClient = mock(ApiClient.class);
        when(apiClient.isConnected()).thenReturn(true);
        Dependencies.inject(apiClient);
        Dependencies.inject(new TestMultiplayerManager(apiClient));
        Dependencies.inject(mock(AchievementsManager.class));
        Dependencies.inject(mock(ProgressManager.class));

        device = mock(AndroidDevice.class);
        when(device.isTablet()).thenReturn(false);
        Dependencies.inject(device);
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public AndroidDevice getDevice() {
        return device;
    }

    public GameSettings settings() {
        return settings;
    }

}
