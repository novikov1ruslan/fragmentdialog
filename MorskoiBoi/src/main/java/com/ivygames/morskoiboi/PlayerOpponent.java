package com.ivygames.morskoiboi;

import android.text.TextUtils;

import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.acra.ACRA;
import org.commons.logger.Ln;

import de.greenrobot.event.EventBus;

public final class PlayerOpponent extends AbstractOpponent {

    public static volatile Board debug_board;

    private boolean mOpponentReady;
    private boolean mPlayerReady;
    private final String mName;
    private int mOpponentVersion;

    public PlayerOpponent(String name) {
        if (TextUtils.isEmpty(name)) {
            name = BattleshipApplication.get().getString(R.string.player);
            Ln.i("player name is empty - replaced by " + name);
        }
        mName = name;
        reset();
        Ln.v("new player created");
    }

    @Override
    public void reset() {
        super.reset();
        mOpponentReady = false;
        mPlayerReady = false;
        mOpponentVersion = 0;
    }

    @Override
    public void go() {
        if (!mOpponentReady) {
            Ln.d(this + ": opponent is ready");
            mOpponentReady = true;
        }
    }

    @Override
    public void onShotResult(PokeResult result) {
        updateEnemyBoard(result);
    }

    @Override
    public void onShotAt(final Vector2 aim) {
        throw new RuntimeException("never used");
    }

    private void markNeighbouringCellsAsOccupied(final Ship ship) {
        // if is dead we remove and put ship back to mark adjacent cells as reserved
        mMyBoard.removeShipFrom(ship.getX(), ship.getY());
        PlacementFactory.getAlgorithm().putShipAt(mMyBoard, ship, ship.getX(), ship.getY());
    }

    public PokeResult onShotAtForResult(Vector2 aim) {
        PokeResult result = createResultFor(aim);
        Ln.v(this + ": hitting my board at " + aim + " yields result: " + result);
        mOpponent.onShotResult(result);

        final Ship ship = result.ship;
        if (ship != null) {
            Ln.v(this + ": my ship is destroyed - " + ship);
            markNeighbouringCellsAsOccupied(ship);
        }

        if (result.cell.isHit() && !RulesFactory.getRules().isItDefeatedBoard(mMyBoard)) {
            Ln.d(this + ": I'm hit - " + mOpponent + " continues");
            mOpponent.go();
        }
        return result;
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
        debug_board = mMyBoard;
    }

    @Override
    public void bid(int bid) {
        mEnemyBid = bid;
        mOpponentReady = true;
        Ln.d(this + ": opponent is ready");

        if (mPlayerReady && isOpponentTurn()) {
            Ln.d(this + ": I'm ready too, but its opponent's turn - " + mOpponent + " begins");
            mOpponent.go();
        }
    }

    @Override
    public void onNewMessage(String text) {
        EventBus.getDefault().post(ChatMessage.newEnemyMessage(text));
    }

    public void startBidding() {
        if (isOpponentReady() && isOpponentTurn()) {
            Ln.d(this + ": opponent is ready and it is his turn");
            mOpponent.go();
        } else {
            Ln.d(this + ": opponent is not ready - sending him my bid");
            mOpponent.bid(mMyBid);
        }
    }

    public boolean isOpponentReady() {
        return mOpponentReady;
    }

    @Override
    public void opponentLost(Board board) {
        Ln.e("never happens");
        ACRA.getErrorReporter().handleException(new RuntimeException("never happens"));
    }

    @Override
    public void setOpponentVersion(int ver) {
        mOpponentVersion = ver;
        Ln.d(this + ": opponent's protocol version: v" + ver);
    }

    public void setReady() {
        mPlayerReady = true;
    }

    public int getOpponentVersion() {
        return mOpponentVersion;
    }

    @Override
    public String toString() {
        return getName();
    }

}
