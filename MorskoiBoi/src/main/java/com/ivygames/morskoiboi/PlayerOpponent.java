package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.acra.ACRA;
import org.commons.logger.Ln;

import de.greenrobot.event.EventBus;

public final class PlayerOpponent extends AbstractOpponent {

    public static volatile Board debug_board;

    private boolean mPlayerReady;

    private final String mName;
    private final PlacementAlgorithm mPlacement;
    private int mOpponentVersion;
    private Rules mRules;

    private Opponent mOpponent;

    public PlayerOpponent(String name, PlacementAlgorithm placement, Rules rules) {
        mPlacement = placement;
        mName = name;
        mRules = rules;
        reset(new Bidder().newBid());
        Ln.v("new player created");
    }

    @Override
    public final void reset(int myBid) {
        super.reset(myBid);
        mPlayerReady = false;
        mOpponentVersion = 0;
    }

    @Override
    public void onShotResult(PokeResult result) {
        updateEnemyBoard(result, mPlacement);
    }

    @Override
    public void onShotAt(final Vector2 aim) {
        throw new RuntimeException("never used");
    }

    @Override
    public void setOpponent(Opponent opponent) {
        mOpponent = opponent;
        Ln.d(this + ": my opponent is " + opponent);
    }

    public PokeResult onShotAtForResult(Vector2 aim) {
        PokeResult result = createResultForShootingAt(aim);
        Ln.v(this + ": hitting my board at " + aim + " yields result: " + result);
        mOpponent.onShotResult(result);

        final Ship ship = result.ship;
        if (ship != null) {
            Ln.v(this + ": my ship is destroyed - " + ship);
            markNeighbouringCellsAsOccupied(ship);
        }

        if (result.cell.isHit() && !mRules.isItDefeatedBoard(mMyBoard)) {
            Ln.d(this + ": I'm hit - " + mOpponent + " continues");
            mOpponent.go();
        }
        return result;
    }

    private void markNeighbouringCellsAsOccupied(final Ship ship) {
        // if is dead we remove and put ship back to mark adjacent cells as reserved
        mMyBoard.removeShipFrom(ship.getX(), ship.getY());
        mPlacement.putShipAt(mMyBoard, ship, ship.getX(), ship.getY());
    }

    @Override
    public String getName() {
        return mName;
    }

    public void shoot(int x, int y) {
        Ln.v(x + "," + y);
        mOpponent.onShotAt(Vector2.get(x, y));
    }

    public Board getEnemyBoard() {
        return mEnemyBoard;
    }

    public Board getBoard() {
        return mMyBoard;
    }

    public void setBoard(Board board) {
        Ln.v("player's board set: " + board);
        mMyBoard = board;
        debug_board = board;
    }

    @Override
    public void onEnemyBid(int bid) {
        super.onEnemyBid(bid);
        if (mPlayerReady && isOpponentTurn()) {
            Ln.d(this + ": I'm ready too, but it's opponent's turn - " + mOpponent + " begins");
            mOpponent.go();
        }
    }

    @Override
    public void onNewMessage(String text) {
        EventBus.getDefault().post(ChatMessage.newEnemyMessage(text));
    }

    public void startBidding() {
        mPlayerReady = true;
        if (isOpponentReady() && isOpponentTurn()) {
            Ln.d(this + ": opponent is ready and it is his turn");
            mOpponent.go();
        } else {
            Ln.d(this + ": opponent is not ready - sending him my bid");
            mOpponent.onEnemyBid(mMyBid);
        }
    }

    @Override
    public void onLost(Board board) {
        Ln.e("never happens");
        ACRA.getErrorReporter().handleException(new RuntimeException("never happens"));
    }

    @Override
    public void setOpponentVersion(int ver) {
        mOpponentVersion = ver;
        Ln.d(this + ": opponent's protocol version: v" + ver);
    }

    public int getOpponentVersion() {
        return mOpponentVersion;
    }

    @Override
    public String toString() {
        return getName();
    }

}
