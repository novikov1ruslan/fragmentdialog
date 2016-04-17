package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.win.WinScreen;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class WinScreenTest extends ScreenTest {

    private Collection<Ship> fleet = new ArrayList<>();
    private boolean surrendered;
    private Game game;
    private Rules rules;

    @Before
    public void setup() {
        super.setup();
        game = mock(Game.class);
        rules = mock(Rules.class);
        Model.instance.game = game;
        RulesFactory.setRules(rules);
    }

    @Override
    public BattleshipScreen newScreen() {
        return new WinScreen(activity(), fleet, surrendered);
    }

    @Test
    public void GameDurationShown() {
        when(game.getTimeSpent()).thenReturn(135000L);
        setScreen(newScreen());
        onView(withId(R.id.time)).check(matches(withText("2:15")));
    }

    @Test
    public void ScoresAreShown() {
        when(rules.calcTotalScores(any(Collection.class), any(Game.class))).thenReturn(100);
        setScreen(newScreen());
        onView(withId(R.id.total_scores)).check(matches(withText("100")));
    }

//    @Test
//    public void when_back_button_pressed__main_screen_opens() {
//        setScreen(newScreen());
//        pressBack();
//        checkDisplayed(MAIN_LAYOUT);
//    }

}
