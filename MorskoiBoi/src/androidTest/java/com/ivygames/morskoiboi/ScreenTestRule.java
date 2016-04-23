package com.ivygames.morskoiboi;

import android.support.test.rule.ActivityTestRule;

import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.invitations.InvitationManager;
import com.ivygames.morskoiboi.progress.ProgressManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScreenTestRule extends ActivityTestRule<BattleshipActivity> {

    private GoogleApiClientWrapper apiClient;
    private AndroidDevice device;
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

        device = mock(AndroidDevice.class);
        when(device.isTablet()).thenReturn(isTablet());
        Dependencies.inject(device);
    }

    private boolean isTablet() {
//        return getActivity().getResources().getBoolean(R.bool.is_tablet);
        return false;
    }

    public GoogleApiClientWrapper getApiClient() {
        return apiClient;
    }

    public AndroidDevice getDevice() {
        return device;
    }

    public GameSettings settings() {
        return settings;
    }
}
