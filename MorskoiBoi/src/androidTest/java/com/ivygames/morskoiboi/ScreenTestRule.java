package com.ivygames.morskoiboi;

import android.support.test.rule.ActivityTestRule;

import com.ivygames.common.AndroidDevice;
import com.ivygames.common.multiplayer.MultiplayerImpl;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.progress.ProgressManager;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScreenTestRule extends ActivityTestRule<BattleshipActivity> {

    private ThrowingApiClient apiClient;
    private AndroidDevice device;
    private GameSettings settings;

    public ScreenTestRule() {
        super(BattleshipActivity.class);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();

        settings = mock(GameSettings.class);
        when(settings.getProgress()).thenReturn(new Progress(1));
        when(settings.incrementProgress(anyInt())).thenReturn(new Progress(1));
        Dependencies.inject(settings);
        apiClient = new ThrowingApiClient();
        apiClient.connect();
        Dependencies.inject(apiClient);
        Dependencies.inject(new MultiplayerImpl(apiClient, 1000));
        Dependencies.inject(mock(AchievementsManager.class));
        Dependencies.inject(mock(ProgressManager.class));

        device = mock(AndroidDevice.class);
        when(device.isTablet()).thenReturn(false);
        Dependencies.inject(device);
    }

    public ThrowingApiClient getApiClient() {
        return apiClient;
    }

    public AndroidDevice getDevice() {
        return device;
    }

    public GameSettings settings() {
        return settings;
    }

}
