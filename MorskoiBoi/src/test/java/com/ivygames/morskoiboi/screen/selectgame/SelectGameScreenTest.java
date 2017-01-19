package com.ivygames.morskoiboi.screen.selectgame;

import android.view.View;
import android.widget.TextView;

import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.BuildConfig;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;

import org.commons.logger.Ln;
import org.commons.logger.NullLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class SelectGameScreenTest {

    private BattleshipActivity activity;
    private SelectGameScreen screen;

    private GameSettings settings = Dependencies.getSettings();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        activity = Robolectric.setupActivity(BattleshipActivity.class);
        screen = new SelectGameScreen(activity);
        Ln.injectLogger(new NullLogger());
    }

    @Test
    public void whenScreenDisplayedAndNoNameSet__NameFieldIsEmptyAndHintTextPresent() {
        settings.setPlayerName("");

        activity.setScreen(screen);

        assertThat(playerName().getText().toString(), is(""));
        assertThat(playerName().getHint().toString(), is(activity.getString(R.string.nick)));
    }

    @Test
    public void whenScreenDisplayedAndNameSet__NameFieldDisplaysName() {
        settings.setPlayerName("Ruslan");

        activity.setScreen(screen);

        assertThat(playerName().getText().toString(), is("Ruslan"));
        assertThat(playerName().getHint().toString(), is(activity.getString(R.string.nick)));
    }

    @Test
    public void whenNameOnScreenIsDifferentFromSettingsAfterSelectingVsAndroid__TheNameSavedToSettings() {
        settings.setPlayerName("Ruslan");
        activity.setScreen(screen);

        playerName().setText("Sagi");
        vsAndroid().performClick();

        assertThat(settings.getPlayerName(), is("Sagi"));
    }

//    @Test
//    public void whenNoNameSetSelectingVsAndroid__CreatesPlayerWithDefaultName() {
//        settings.setPlayerName("");
//        PlayerFactory spy = spy(Dependencies.getPlayerFactory());
//        Dependencies.inject(spy);
//        screen = new SelectGameScreen(activity);
//        activity.setScreen(screen);
//
//        vsAndroid().performClick();
//
//        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
//        verify(spy, times(1)).createPlayer(argument.capture(), anyInt());
//        assertThat(argument.getValue(), is(activity.getString(R.string.player)));
//    }


    private TextView playerName() {
        return (TextView) activity.findViewById(R.id.player_name);
    }

    private View vsAndroid() {
        return activity.findViewById(R.id.vs_android);
    }
}