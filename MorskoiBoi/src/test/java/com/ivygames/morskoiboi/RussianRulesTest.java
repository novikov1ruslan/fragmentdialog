package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.variant.RussianPlacement;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RussianRulesTest {

    private Rules mRules;
    private PlacementAlgorithm mAlgorithm;

    @BeforeClass
    public static void runBeforeClass() {

    }

    @Before
    public void setUp() {
        Random random = new Random(1);
        PlacementFactory.setPlacementAlgorithm(new RussianPlacement(random));
        mAlgorithm = PlacementFactory.getAlgorithm();
        RulesFactory.setRules(new RussianRules());
        mRules = RulesFactory.getRules();
    }

    @Test
    public void board_is_set_when_it_has_full_russian_fleet_and_no_conflicting_cells() {
        assertThat(mRules.isBoardSet(mAlgorithm.generateBoard()), is(true));
    }

    @Test
    public void empty_board_is_not_set() {
        assertThat(mRules.isBoardSet(new Board()), is(false));
    }

    @Test
    public void board_is_not_set_when_it_has_less_than_full_russian_fleet() {
        Board board = mAlgorithm.generateBoard();
        for (Ship ship :
                board.getShips()) {
            board.removeShipFrom(ship.getX(), ship.getY());
            break;
        }
        assertThat(mRules.isBoardSet(board), is(false));
    }

    @Test
    public void board_is_not_set_when_it_has_conflicting_cells_although_all_the_fleet_is_on_a_board() {
        Board board = mAlgorithm.generateBoard();
        Collection<Ship> shipsCopy = new ArrayList<>(board.getShips());
        for (Ship ship : shipsCopy) {
            board.removeShipFrom(ship.getX(), ship.getY());
            mAlgorithm.putShipAt(board, ship, 0, 0);
        }
        assertThat(board.getShips().size(), is(10));
        assertThat(mRules.isBoardSet(board), is(false));
    }

}
