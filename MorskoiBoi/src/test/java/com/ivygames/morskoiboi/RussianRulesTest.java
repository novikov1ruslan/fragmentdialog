package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.variant.RussianPlacement;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RussianRulesTest {

    private Board mBoard;
    private Rules mRules;
    private RussianPlacement mAlgorithm;

    @BeforeClass
    public static void runBeforeClass() {

    }

    @Before
    public void setUp() {
        Random random = new Random(1);
        mAlgorithm = new RussianPlacement(random);
        RulesFactory.setRules(new RussianRules());
        mRules = RulesFactory.getRules();
        mBoard = new Board();
    }

    @Test
    public void board_is_set_when_it_has_full_russian_fleet_and_no_conflicting_cells() {
        Collection<Ship> fleet = mAlgorithm.generateFullFleet();
        for (Ship ship : fleet) {
            mAlgorithm.place(ship, mBoard);
        }
        assertThat(mRules.isBoardSet(mBoard), is(true));
    }

    @Test
    public void board_is_not_set_when_it_has_less_than_full_russian_fleet() {
        Collection<Ship> fleet = mAlgorithm.generateFullFleet();
        for (Ship ship : fleet) {
            mAlgorithm.place(ship, mBoard);
            if (mBoard.getShips().size() == fleet.size() - 1) {
                break;
            }
        }
        assertThat(mRules.isBoardSet(mBoard), is(false));
    }

    @Test
    public void board_is_set_when_it_has_full_russian_fleet_and_no_conflicting_cells_and_first_ship_is_at_0_0() {
        Collection<Ship> fleet = mAlgorithm.generateFullFleet();
        for (Ship ship : fleet) {
            if (mBoard.getShips().size() == 0) {
                mAlgorithm.putShipAt(mBoard, ship, 0, 0);
                continue;
            }
            else {
                mAlgorithm.place(ship, mBoard);
            }
        }
        assertThat(mRules.isBoardSet(mBoard), is(true));
    }

    @Test
    public void board_is_not_set_when_it_has_conflicting_cells_although_all_the_fleet_is_on_a_board() {
        Collection<Ship> fleet = mAlgorithm.generateFullFleet();
        for (Ship ship : fleet) {
            if (mBoard.getShips().size() == 0 || mBoard.getShips().size() == 1) {
                mAlgorithm.putShipAt(mBoard, ship, 0, 0);
                continue;
            }
            else {
                mAlgorithm.place(ship, mBoard);
            }
        }
        assertThat(mBoard.getShips().size(), is(fleet.size()));
        assertThat(mRules.isBoardSet(mBoard), is(false));
    }

}
