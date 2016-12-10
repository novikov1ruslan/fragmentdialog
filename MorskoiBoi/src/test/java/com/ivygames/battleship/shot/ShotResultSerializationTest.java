package com.ivygames.battleship.shot;

import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class ShotResultSerializationTest {
    private static final String MISS_5_5 = "{\"AIM\":{\"X\":5,\"Y\":5},\"CELL\":42}";
    private static final String HIT_4_7 = "{\"AIM\":{\"X\":4,\"Y\":7},\"CELL\":88}";
    private static final String KILL_2H_4_7 = "{\"AIM\":{\"X\":4,\"Y\":7}," +
            "\"SHIP\":{\"size\":2,\"is_horizontal\":true,\"x\":1,\"y\":2,\"health\":2}," +
            "\"CELL\":88}";

    @Test
    public void missSerializedCorrectly() {
        ShotResult shot = new ShotResult(Vector.get(5, 5), Cell.MISS);

        String data = ShotResultSerialization.toJson(shot).toString();

        assertThat(data, is(MISS_5_5));
    }

    @Test
    public void missDeSerializedCorrectly() {
        ShotResult shot = ShotResultSerialization.fromJson(MISS_5_5);

        assertThat(shot.cell, is(Cell.MISS));
        assertThat(shot.aim, is(Vector.get(5, 5)));
        assertThat(shot.locatedShip, is(nullValue()));
    }

    @Test
    public void hitSerializedCorrectly() {
        ShotResult shot = new ShotResult(Vector.get(4, 7), Cell.HIT);

        String data = ShotResultSerialization.toJson(shot).toString();

        assertThat(data, is(HIT_4_7));
    }

    @Test
    public void hitDeSerializedCorrectly() {
        ShotResult shot = ShotResultSerialization.fromJson(HIT_4_7);

        assertThat(shot.cell, is(Cell.HIT));
        assertThat(shot.aim, is(Vector.get(4, 7)));
        assertThat(shot.locatedShip, is(nullValue()));
    }

    @Test
    public void killSerializedCorrectly() {
        ShotResult shot = new ShotResult(Vector.get(4, 7), Cell.HIT, new LocatedShip(new Ship(2), Vector.get(1, 2)));

        String data = ShotResultSerialization.toJson(shot).toString();

        assertThat(data, is(KILL_2H_4_7));
    }

    @Test
    public void killDeSerializedCorrectly() {
        ShotResult shot = ShotResultSerialization.fromJson(KILL_2H_4_7);

        assertThat(shot.cell, is(Cell.HIT));
        assertThat(shot.aim, is(Vector.get(4, 7)));
        assertThat(shot.locatedShip, is(not(nullValue())));
    }
}