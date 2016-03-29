package com.ivygames.morskoiboi.screen.settings;

import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class SettingsScreenTest {

    private TestActivity activity;
    @Mock
    private GoogleApiClient apiClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        activity = Robolectric.buildActivity(TestActivity.class).get();
//        activity.onCreate(null);
        activity.setScreen(new SettingsScreen(activity, apiClient));
    }

    @Test
    public void when_not_signed_in__sign_in_button_present() {
        Mockito.when(apiClient.isConnected()).thenReturn(false);
        int signInVisibility = activity.findViewById(R.id.sign_in_bar).getVisibility();
        int signOutVisibility = activity.findViewById(R.id.sign_in_bar).getVisibility();
        assertThat(signInVisibility, is(View.VISIBLE));
        assertThat(signOutVisibility, is(View.GONE));
    }

}