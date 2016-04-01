package com.ivygames.morskoiboi.screen.settings;

import android.content.Context;
import android.os.Build;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.DeviceUtils;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.ShadowsAdapter;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;

@Config(manifest = "src/test/AndroidManifest.xml", sdk = Build.VERSION_CODES.JELLY_BEAN, resourceDir = "../main/res")
@RunWith(RobolectricGradleTestRunner.class)
public class SettingsScreenTest {

    private TestActivity activity;
    @Mock
    private GoogleApiClient apiClient;
    @Mock
    private GameSettings settings;
    @Mock
    private GoogleApiAvailability apiAvailability;

    @Before
    public void setup() {
//        MockitoAnnotations.initMocks(this);
////        activity = Robolectric.buildActivity(TestActivity.class).get();
//        Mockito.when(apiAvailability.isGooglePlayServicesAvailable(any(Context.class))).thenReturn(ConnectionResult.SUCCESS);
//        DeviceUtils.init(apiAvailability);
//        activity = new TestActivity();
//        ShadowsAdapter shadowsAdapter = Robolectric.getShadowsAdapter();
//        ActivityController<TestActivity> activityController = new ActivityController<>(shadowsAdapter, activity);
//        activityController.create();
//        apiClient = activityController.get().getApiClient();
//        activity.setScreen(new SettingsScreen(activity, apiClient, settings));
    }

    @Test
    public void when_not_signed_in__sign_in_button_present() {
//        Mockito.when(apiClient.isConnected()).thenReturn(false);
//        int signInVisibility = activity.findViewById(R.id.sign_in_bar).getVisibility();
//        int signOutVisibility = activity.findViewById(R.id.sign_in_bar).getVisibility();
//        assertThat(signInVisibility, is(View.VISIBLE));
//        assertThat(signOutVisibility, is(View.GONE));
    }

}