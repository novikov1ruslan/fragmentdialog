package com.ivygames.morskoiboi.screen.boardsetup;

import com.ivygames.morskoiboi.model.Ship;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BoardSetupUtilsTest {

    @Test
    public void testOnlyHorizontalShips() {
        Collection<Ship> ships = new ArrayList<>();

        boolean onlyHorizontalShips = BoardSetupUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(true));

        Ship vertical1 = new Ship(1, Ship.Orientation.VERTICAL);
        Ship horizontal2 = new Ship(2, Ship.Orientation.HORIZONTAL);
        Ship vertical2 = new Ship(2, Ship.Orientation.VERTICAL);

        ships = Arrays.asList(vertical1);
        onlyHorizontalShips = BoardSetupUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(true));

        ships = Arrays.asList(horizontal2);
        onlyHorizontalShips = BoardSetupUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(true));

        ships = Arrays.asList(vertical2);
        onlyHorizontalShips = BoardSetupUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(false));

        ships = Arrays.asList(vertical2, horizontal2);
        onlyHorizontalShips = BoardSetupUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(false));

        ships = Arrays.asList(vertical2, horizontal2, vertical1);
        onlyHorizontalShips = BoardSetupUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(false));
    }
}