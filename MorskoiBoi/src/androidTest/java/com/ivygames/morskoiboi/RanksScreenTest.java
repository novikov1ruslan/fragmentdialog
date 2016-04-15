package com.ivygames.morskoiboi;

import android.widget.ListAdapter;
import android.widget.ListView;

import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.ranks.RanksListScreen;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.Is.is;


public class RanksScreenTest extends ScreenTest {

    private GameSettings settings = GameSettings.get();

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new RanksListScreen(activity(), settings);
    }

    @Test
    public void WhenBackButtonPressed_SelectGameScreenOpens() {
        setScreen(newScreen());
        pressBack();
        checkDisplayed(SELECT_GAME_LAYOUT);
    }

    @Test
    public void ScoresCorrectlyDisplayed() {
        int scores = 5678;
        Progress progress = new Progress(scores);
        settings.setProgress(progress);
        setScreen(newScreen());
        onView(withId(R.id.total_score)).check(matches(withText("" + scores)));
    }

    @Test
    public void WhenRankInSettingsIsCaptain__CaptainImageIsDisplayed() {
        setScreen(newScreen());
        ListView list = (ListView) activity().findViewById(R.id.ranks);
        ListAdapter adapter = list.getAdapter();
        assertThat(getItemAt(adapter, 0), is(Rank.SEAMAN));
        assertThat(getItemAt(adapter, 6), is(Rank.WARRANT_OFFICER));
        assertThat(getItemAt(adapter, 19), is(Rank.NAVY_ADMIRAL));
    }

    private Rank getItemAt(ListAdapter adapter, int position) {
        return (Rank) adapter.getItem(position);
    }

}
