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
	public void testBeenShotAfterHit() {
		mCell.setHit();
		assertTrue(mCell.beenShot());
	}

	@Test
	public void testBeenShotAfterEmpty() {
		assertFalse(mCell.beenShot());
	}

}
