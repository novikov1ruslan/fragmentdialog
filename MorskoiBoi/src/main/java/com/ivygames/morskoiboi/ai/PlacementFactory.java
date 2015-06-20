package com.ivygames.morskoiboi.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Ship.Orientation;
import com.ivygames.morskoiboi.model.Vector2;

public final class PlacementFactory {

	private PlacementFactory() {
		// factory
	}

	public static PlacementAlgorithm getAlgorithm() {
		return new PlacementImplementation();
	}

	private static class PlacementImplementation implements PlacementAlgorithm {

		private final Random mRandom;

		public PlacementImplementation() {
			mRandom = new Random(System.currentTimeMillis());
		}

		@Override
		public Board generateBoard() {
			Board board = new Board();

			Collection<Ship> ships = generateFullFleet();
			for (Ship ship : ships) {
				place(ship, board);
			}

			return board;
		}

		private static boolean isPlaceEmpty(Ship ship, Board board, int i, int j) {
			boolean isHorizontal = ship.isHorizontal();
			for (int k = isHorizontal ? i : j; k < (isHorizontal ? i : j) + ship.getSize(); k++) {
				int x = isHorizontal ? k : i;
				int y = isHorizontal ? j : k;
				if (!board.getCell(x, y).isEmpty()) {
					return false;
				}
			}

			return true;
		}

		@Override
		public boolean place(Ship ship, Board board) {
			List<Vector2> cells = board.getEmptyCells();
			boolean found = false;

			while (!cells.isEmpty()) {
				int cellIndex = mRandom.nextInt(cells.size());
				Vector2 cell = cells.get(cellIndex);
				int i = cell.getX();
				int j = cell.getY();
				// boolean found = canPlaceShipAt(board, ship, i, j, board2);
				found = board.canPutShipAt(ship, i, j);
				if (found) {
					found = PlacementImplementation.isPlaceEmpty(ship, board, i, j);
					if (found) {
						board.putShipAt(ship, i, j);
						break;
					} else {
						cells.remove(cellIndex);
					}
				}
			}

			return found;
		}

		private Orientation calcRandomOrientaion() {
			return mRandom.nextInt(2) == 1 ? Orientation.HORIZONTAL : Orientation.VERTICAL;
		}

		// TODO: do via priority queue
		private List<Ship> generateFullFleet() {
			// order is important
			List<Ship> fullSet = new ArrayList<Ship>();
			fullSet.add(new Ship(4, calcRandomOrientaion()));
			fullSet.add(new Ship(3, calcRandomOrientaion()));
			fullSet.add(new Ship(3, calcRandomOrientaion()));
			fullSet.add(new Ship(2, calcRandomOrientaion()));
			fullSet.add(new Ship(2, calcRandomOrientaion()));
			fullSet.add(new Ship(2, calcRandomOrientaion()));
			fullSet.add(new Ship(1, calcRandomOrientaion()));
			fullSet.add(new Ship(1, calcRandomOrientaion()));
			fullSet.add(new Ship(1, calcRandomOrientaion()));
			fullSet.add(new Ship(1, calcRandomOrientaion()));

			return fullSet;
		}

	}
}
