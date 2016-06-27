package com.ivygames.morskoiboi;

import android.view.View;

import com.ivygames.morskoiboi.screen.settings.SettingsScreen;

import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.ShadowsAdapter;
import org.robolectric.util.ActivityController;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

//@Config(manifest = "src/test/AndroidManifest.xml", sdk = Build.VERSION_CODES.JELLY_BEAN, resourceDir = "../main/res")
//@RunWith(RobolectricGradleTestRunner.class)
public class SettingsScreenTest2 {

    private TestActivity activity;
    @Mock
    private GoogleApiClientWrapper apiClient;
    @Mock
    private GameSettings settings;
    @Mock
    private AndroidDevice device;
    @Mock
    private VibratorFacade vibrator;

//    @Before
    public void setup() {
        initMocks(this);
//        activity = Robolectric.buildActivity(TestActivity.class).get();
        Dependencies.inject(apiClient);
        Dependencies.inject(device);
        when(device.isGoogleServicesAvailable()).thenReturn(false);
        activity = new TestActivity();
        ShadowsAdapter shadowsAdapter = Robolectric.getShadowsAdapter();
        ActivityController<TestActivity> activityController = new ActivityController<>(shadowsAdapter, activity);
        activityController.create();
        activity.setScreen(new SettingsScreen(activity, apiClient, settings, vibrator));
    }

//    @Test
    public void when_not_signed_in__sign_in_button_present() {
        when(apiClient.isConnected()).thenReturn(false);
        int signInVisibility = activity.findViewById(R.id.sign_in_bar).getVisibility();
        int signOutVisibility = activity.findViewById(R.id.sign_in_bar).getVisibility();
        assertThat(signInVisibility, is(View.VISIBLE));
        assertThat(signOutVisibility, is(View.GONE));
    }

}