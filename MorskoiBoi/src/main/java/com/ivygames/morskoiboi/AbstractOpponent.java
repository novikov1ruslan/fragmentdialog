package com.ivygames.morskoiboi;

import java.util.Random;

import org.commons.logger.Ln;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

public abstract class AbstractOpponent implements Opponent {
	private static final int IMPOSSIBLE_BID = -1;

	protected Board mMyBoard;
	protected Board mEnemyBoard;
	protected Opponent mOpponent;

	protected volatile int mMyBid = IMPOSSIBLE_BID;

	protected volatile int mEnemyBid = IMPOSSIBLE_BID;

	protected void reset() {
		Ln.d(this + ": initializing boards and bids");
		mMyBoard = new Board();
		mEnemyBoard = new Board();
		mMyBid = new Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE);
		mEnemyBid = IMPOSSIBLE_BID;
	}

	@Override
	public void setOpponent(Opponent opponent) {
		mOpponent = opponent;
		Ln.d(this + ": my opponent is " + opponent);
	}

	/**
	 * marks the aimed cell
	 */
	protected final PokeResult createResultFor(Vector2 aim) {
		// ship if found will be shot and returned
		Ship ship = mMyBoard.getFirstShipAt(aim);

		// this cell will be changed and returned in result later
		Cell cell = mMyBoard.getCellAt(aim);

		if (ship == null) {
			cell.setMiss();
		} else {
			cell.setHit();
			ship.shoot();

			if (!ship.isDead()) {
				ship = null;
			}
		}

		return new PokeResult(aim, cell, ship);
	}

	protected final void updateEnemyBoard(PokeResult result) {
		Ship ship = result.ship;
		if (ship == null) {
			mEnemyBoard.setCell(result.cell, result.aim);
		} else {
			mEnemyBoard.putShipAt(ship, ship.getX(), ship.getY());
		}
		Ln.v(mEnemyBoard);
	}

	public final boolean isOpponentTurn() {
		return mMyBid < mEnemyBid;
	}

}
