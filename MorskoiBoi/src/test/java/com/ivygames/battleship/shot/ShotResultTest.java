package com.ivygames.battleship.shot;

import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.LocatedShip;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ShotResultTest {

    @Test(expected = IllegalArgumentException.class)
    public void killResult__cannotBeMiss() {
        new ShotResult(Vector.get(8, 2), Cell.MISS, mock(LocatedShip.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void killResult__cannotBeEmptyCell() {
        new ShotResult(Vector.get(8, 2), Cell.EMPTY, mock(LocatedShip.class));
    }

    @Test
    public void whenShipIsProvided__itsaKill() {
        ShotResult shotResult = new ShotResult(Vector.get(8, 2), Cell.HIT, mock(LocatedShip.class));

        assertThat(shotResult.isaKill(), is(true));
    }

    @Test
    public void whenShipIsNotProvided__itsNotKill() {
        ShotResult shotResult = new ShotResult(Vector.get(8, 2), Cell.HIT);

        assertThat(shotResult.isaKill(), is(false));
    }

}