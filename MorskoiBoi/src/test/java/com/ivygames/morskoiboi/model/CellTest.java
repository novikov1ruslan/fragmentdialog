package com.ivygames.morskoiboi.model;

import junit.framework.TestCase;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

//@RunWith(RobolectricTestRunner.class)
public class CellTest extends TestCase {

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
//		mCell.setEmpty();
		assertFalse(mCell.isReserved());
		assertEquals(0, mCell.getProximity());
		mCell.setReserved();
		assertTrue(mCell.isReserved());
		assertEquals(1, mCell.getProximity());
		mCell.setReserved();
		assertTrue(mCell.isReserved());
		assertEquals(2, mCell.getProximity());
		mCell.setMiss();
		mCell.setReserved();
		assertTrue(mCell.isMiss());
	}

	@Test
	public void testProximity() {
		assertEquals(0, mCell.getProximity());
		mCell.addShip();
		assertEquals(8, mCell.getProximity());
		mCell.setReserved();
		assertEquals(9, mCell.getProximity());
	}

	@Test
	public void testAddShip() {
		assertEquals(0, mCell.getProximity());
		mCell.addShip();
		assertTrue(mCell.isReserved());
		assertEquals(8, mCell.getProximity());
	}

//	@Test
//	public void testToChar() {
////		mCell.setEmpty();
//		assertEquals(Cell.EMPTY, mCell.toChar());
//		mCell.setReserved();
//		assertEquals(Cell.RESERVED, mCell.toChar());
//		mCell.setMiss();
//		assertEquals(Cell.MISS, mCell.toChar());
//		mCell.setHit();
//		assertEquals(Cell.HIT, mCell.toChar());
////		mCell.setSunk();
////		assertEquals(Cell.SUNK, mCell.toChar());
//	}
//
//	@Test
//	public void testNewEmpty() {
//		Cell cell = Cell.newEmpty();
//		assertNotSame(cell, Cell.newEmpty());
//
//		assertTrue(cell.isEmpty());
//	}
//
//	@Test
//	public void testNewReserved() {
//		Cell cell = Cell.newReserved();
//		assertNotSame(cell, Cell.newReserved());
//
//		assertTrue(cell.isReserved());
//	}
//
//	@Test
//	public void testNewMiss() {
//		Cell cell = Cell.newMiss();
//		assertNotSame(cell, Cell.newMiss());
//
//		assertTrue(cell.isMiss());
//	}

//	@Test
//	public void testNewHit() {
//		Cell cell = Cell.newHit();
//		assertNotSame(cell, Cell.newHit());
//
//		assertTrue(cell.isHit());
//	}

//	@Test
//	public void testNewSunk() {
//		Cell cell = Cell.newSunk();
//		assertNotSame(cell, Cell.newSunk());
//
//		assertTrue(cell.isSunk());
//	}

//	@Test
//	public void testEquals() {
//		List<Cell> allCells = new ArrayList<Cell>();
//		allCells.add(Cell.newEmpty());
//		allCells.add(Cell.newReserved());
//		allCells.add(Cell.newMiss());
//		allCells.add(Cell.newHit());
////		allCells.add(Cell.newSunk());
//
//		Cell cell1 = Cell.newEmpty();
//		for (Cell cell : allCells) {
//			if (cell.isEmpty()) {
//				assertTrue(cell1.equals(cell));
//			} else {
//				assertFalse(cell1.equals(cell));
//			}
//		}
//
//		cell1 = Cell.newReserved();
//		for (Cell cell : allCells) {
//			if (cell.isReserved()) {
//				assertTrue(cell1.equals(cell));
//			} else {
//				assertFalse(cell1.equals(cell));
//			}
//		}
//
//		cell1 = Cell.newMiss();
//		for (Cell cell : allCells) {
//			if (cell.isMiss()) {
//				assertTrue(cell1.equals(cell));
//			} else {
//				assertFalse(cell1.equals(cell));
//			}
//		}
//
//		cell1 = Cell.newHit();
//		for (Cell cell : allCells) {
//			if (cell.isHit()) {
//				assertTrue(cell1.equals(cell));
//			} else {
//				assertFalse(cell1.equals(cell));
//			}
//		}
//
////		cell1 = Cell.newSunk();
////		for (Cell cell : allCells) {
////			if (cell.isSunk()) {
////				assertTrue(cell1.equals(cell));
////			} else {
////				assertFalse(cell1.equals(cell));
////			}
////		}
//	}
//
//	@Test
//	public void testParse() {
//		Cell cell = Cell.parse(Cell.EMPTY);
//		assertTrue(cell.isEmpty());
//		cell = Cell.parse(Cell.RESERVED);
//		assertTrue(cell.isReserved());
//		cell = Cell.parse(Cell.MISS);
//		assertTrue(cell.isMiss());
//		cell = Cell.parse(Cell.HIT);
//		assertTrue(cell.isHit());
////		cell = Cell.parse(Cell.SUNK);
////		assertTrue(cell.isSunk());
//
//		for (int i = '0'; i <= '8'; i++) {
//			cell = Cell.parse((char) i);
//			assertTrue(cell.isReserved());
//			assertEquals(i - '0', cell.getProximity());
//		}
//
//		try {
//			cell = Cell.parse('9');
//			fail();
//		} catch (IllegalArgumentException iae) {
//
//		}
//	}
}
