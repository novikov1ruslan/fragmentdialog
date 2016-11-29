package com.ivygames.morskoiboi;

import com.ivygames.battleship.RussianRules;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RussianRulesTest {

    private final RussianRules mRules = new RussianRules();

    @Test
    public void russian_fleet_has_following_ships_4_3_3_2_2_2_1_1_1_1() {
        assertThat(mRules.getAllShipsSizes(), is(new int[]{4,3,3,2,2,2,1,1,1,1}));
    }

}
