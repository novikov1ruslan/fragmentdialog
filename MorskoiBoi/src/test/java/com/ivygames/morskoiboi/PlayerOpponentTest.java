package com.ivygames.morskoiboi;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class PlayerOpponentTest {

    private PlayerOpponent mOpponent;

    @Before
    public void setUp() throws Exception {
        mOpponent = new PlayerOpponent("Sagi");
    }

    @Test
    public void testReset() throws Exception {
//        mOpponent.reset();
        assertThat(mOpponent.isOpponentReady(), is(false));
    }

    @Test
    public void testGo() throws Exception {
        mOpponent.go();
    }

    @Test
    public void testOnShotResult() throws Exception {

    }

    @Test
    public void testOnShotAt() throws Exception {

    }

    @Test
    public void testOnShotAtForResult() throws Exception {

    }

    @Test
    public void testGetName() throws Exception {

    }

    @Test
    public void testShoot() throws Exception {

    }

    @Test
    public void testGetEnemyBoard() throws Exception {

    }

    @Test
    public void testGetBoard() throws Exception {

    }

    @Test
    public void testSetBoard() throws Exception {

    }

    @Test
    public void testBid() throws Exception {
        mOpponent.onEnemyBid(1);
    }

    @Test
    public void testOnNewMessage() throws Exception {

    }

    @Test
    public void testStartBidding() throws Exception {

    }

    @Test
    public void testIsOpponentReady() throws Exception {

    }

    @Test
    public void testOpponentLost() throws Exception {

    }

    @Test
    public void testSetOpponentVersion() throws Exception {

    }

    @Test
    public void testSetReady() throws Exception {

    }

    @Test
    public void testGetOpponentVersion() throws Exception {

    }
}