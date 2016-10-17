package com.ivygames.morskoiboi.lost;

import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.OnlineScreen_;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.lost.LostScreen;

import org.junit.Before;

import static org.mockito.Mockito.mock;


public class LostScreen_ extends OnlineScreen_ {

    protected Rules rules;

    @Before
    public void setup() {
        super.setup();
        rules = mock(Rules.class);
        Dependencies.inject(rules);
    }

    @Override
    public BattleshipScreen newScreen() {
        return new LostScreen(activity, game, session);
    }

}
