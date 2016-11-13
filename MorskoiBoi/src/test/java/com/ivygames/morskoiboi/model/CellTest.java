package com.ivygames.morskoiboi.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class CellTest {

	private Cell mCell;

	@Before
	public void setUp() throws Exception {
		mCell = new Cell();
	}

	@Test
	public void after_creation_cell_state_is_empty() {
        assertThat(mCell.isEmpty(), is(true));
    }

	@Test
	public void testEmpty() {
//		mCell.setReserved();
//		assertFalse(mCell.isEmpty());
//		mCell.setEmpty();
//		assertTrue(mCell.isEmpty());
	}

	@Test
	public void testBeenShotAfterHit() {
		mCell.setHit();
		assertTrue(mCell.beenShot());
	}

	@Test
	public void testBeenShotAfterEmpty() {
//		mCell.setEmpty();
		assertFalse(mCell.beenShot());
	}

	@Test
	public void testBeenShotAfterMiss() {
		mCell.setMiss();
		assertTrue(mCell.beenShot());
	}

	@Test
	public void testBeenShotAfterReserved() {
		mCell.setReserved();
		assertFalse(mCell.beenShot());
	}

//	@Test
//	public void testBeenShotAfterSunk() {
//		mCell.setSunk();
//		assertTrue(mCell.beenShot());
//	}

//	@Test
//	public void testHit() {
////		mCell.setEmpty();
//		assertFalse(mCell.isHit());
//		mCell.setHit();
//		assertTrue(mCell.isHit());
//		try {
////			mCell.setEmpty();
//			fail();
//		} catch (IllegalStateException ise) {
//		}
//		try {
//			mCell.setReserved();
//			fail();
//		} catch (IllegalStateException ise) {
//		}
//	}

//	@Test
//	public void testSunk() {
//		mCell.setEmpty();
//		assertFalse(mCell.isSunk());
//		mCell.setSunk();
//		assertTrue(mCell.isSunk());
//		try {
//			mCell.setEmpty();
//			fail();
//		} catch (IllegalStateException ise) {
//		}
//		try {
//			mCell.setReserved();
//			fail();
//		} catch (IllegalStateException ise) {
//		}
//	}

//	@Test
//	public void testMiss() {
////		mCell.setEmpty();
//		assertFalse(mCell.isMiss());
//		mCell.setMiss();
//		assertTrue(mCell.isMiss());
//		try {
////			mCell.setEmpty();
//			fail();
//		} catch (IllegalStateException ise) {
//		}
//	}

	@Test
	public void testReserved() {
		assertFalse(mCell.isReserved());
		mCell.setReserved();
		assertTrue(mCell.isReserved());
		mCell.setReserved();
		assertTrue(mCell.isReserved());
		mCell.setMiss();
		mCell.setReserved();
		assertTrue(mCell.isMiss());
	}

	@Test
	public void testAddShip() {
		mCell.addShip();
		assertTrue(mCell.isReserved());
	}

}
