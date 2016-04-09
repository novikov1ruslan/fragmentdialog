package com.ivygames.morskoiboi;

import android.support.test.rule.ActivityTestRule;

import static org.mockito.Mockito.mock;

public class ScreenTestRule extends ActivityTestRule<BattleshipActivity> {
    private GoogleApiClientWrapper apiClient;

    public ScreenTestRule() {
        super(BattleshipActivity.class);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        apiClient = mock(GoogleApiClientWrapper.class);
        GoogleApiFactory.inject(apiClient);
    }

    public GoogleApiClientWrapper getApiClient() {
        return apiClient;
    }

}
