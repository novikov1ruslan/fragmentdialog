package com.ivygames.battleship.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.ChatMessage;
import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.morskoiboi.PlayerCallback;

import org.commons.logger.Ln;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class PlayerOpponent implements Opponent {
    public static volatile Board debug_board;

    private static final int NOT_READY = -1;

    @NonNull
    private Board mMyBoard = new Board();
    @NonNull
    private Board mEnemyBoard = new Board();
    private int mMyBid = NOT_READY;
    private int mEnemyBid = NOT_READY;

    @Nullable
    private ChatListener mChatListener;
    private int mOpponentVersion;
    protected Opponent mOpponent;
    @NonNull
    private final AggregatePlayerCallback mCallback = new AggregatePlayerCallback();

    /**
     * opponent is ready either when he has sent his bid or *go* command
     */
    private boolean mOpponentReady;

    @NonNull
    private final String mName;
    private final int mNumberOfShips;

    public PlayerOpponent(@NonNull String name, int numberOfShips) {
        mName = name;
        mNumberOfShips = numberOfShips;
        Ln.v("new player created: " + name);
    }

    private void reset() {
        mEnemyBoard = new Board();
        mMyBoard = new Board();
        mMyBid = NOT_READY;
        mEnemyBid = NOT_READY;
        mOpponentReady = false;
    }

    public void setBoard(@NonNull Board board) {
        Ln.v(mName + ": my board is: " + board);
        mMyBoard = board;
        if (!board.getShips().isEmpty()) {
            debug_board = board;
        }
    }

    public void setChatListener(@NonNull ChatListener listener) {
        mChatListener = listener;
        Ln.v(mName + ": my chat listener is: " + listener);
    }

    public void startBidding(int bid) {
        mMyBid = bid;

        if (mOpponentReady && opponentStarts()) {
            Ln.d(mName + ": my bid: " + bid + ", opponent is ready and it is his turn");
            mOpponent.go();
        } else {
            Ln.d(mName + ": opponent " + (mOpponentReady ? "has lower bid" : "is not ready")
                    + " - sending him my bid: " + bid);
            mOpponent.onEnemyBid(bid);
        }
    }

    @Override
    public void onEnemyBid(int bid) {
        setOpponentBid(bid);
        mCallback.opponentReady();

        if (ready() && opponentStarts()) {
            Ln.d(mName + ": I'm ready , but it's opponent's turn, " + mOpponent + " begins");
            mOpponent.go();

            mCallback.onOpponentTurn();
        }
    }

    private void setOpponentBid(int bid) {
        mEnemyBid = bid;
        if (mEnemyBid == mMyBid) {
            reportException("stall");
        }
        mOpponentReady = true;
        Ln.d(this + ": opponent is ready, bid = " + bid);
    }

    /**
     * @param callback this callback is only for 1-directional feedback,
     *                 and the implementation must not call into this {@link PlayerOpponent}
     */
    public void registerCallback(@NonNull PlayerCallback callback) {
        mCallback.registerCallback(callback);
        Ln.v(mName + ": [" + callback + "] callback added");

        if (mOpponentReady) {
            Ln.d(mName + ": opponent ready, notifying");
            callback.opponentReady();

            if (ready()) {
                if (opponentStarts()) {
                    callback.onOpponentTurn();
                } else {
                    callback.onPlayersTurn();
                }
            }
        }
    }

    public void unregisterCallback(@NonNull PlayerCallback callback) {
        mCallback.unregisterCallback(callback);
    }

    @Override
    public void go() {
        if (!ready()) {
            throw new IllegalStateException("cannot go when not ready");
        }

        Ln.d(mName + ": I go");
        if (!mOpponentReady) {
            Ln.v(mName + ": opponent was not ready, but ready now");
            mOpponentReady = true;

            mCallback.opponentReady();
        }

        mCallback.onPlayersTurn();
    }

    @Override
    public void onShotResult(@NonNull ShotResult result) {
        Ln.v(mName + ": -> my shot result: " + result);
        updateEnemyBoard(result);

        mCallback.onPlayerShotResult(result);
        if (result.isaKill()) {
            mCallback.onKillEnemy();
            if (BoardUtils.isItDefeatedBoard(mEnemyBoard, mNumberOfShips)) {
                Ln.d(mName + ": actually opponent lost");

                // If the opponent's version does not support board reveal, just switch screen in 3 seconds.
                // In the later version of the protocol opponent notifies about players defeat sending his board along.
                if (versionSupportsBoardReveal()) {
                    mOpponent.onLost(mMyBoard);
                } else {
                    Ln.d(this + ": opponent version doesn't support board reveal = " + mOpponentVersion);
                }

                Ln.d(mName + ": resetting my state before win");
                reset();
                mCallback.onWin();
            }
        } else if (result.cell == Cell.MISS) {
            Ln.d(mName + ": I missed - passing the turn to " + mOpponent);
            mOpponent.go();
            mCallback.onMiss();
            mCallback.onOpponentTurn();
        } else {
            Ln.d(mName + ": it's a hit!");
            mCallback.onHit();
        }
    }

    private boolean versionSupportsBoardReveal() {
        return mOpponentVersion >= PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL;
    }

    @Override
    public void onShotAt(@NonNull Vector aim) {
        ShotResult result = createResultForShootingAt(aim);
        Ln.v(mName + ": <- hitting my board at " + aim + " yields: " + result);

        mCallback.onPlayerShotAt();
        if (result.isaKill()) {
            Ln.d(mName + ": my ship is destroyed - " + result.locatedShip);
            mCallback.onKillPlayer();
        } else if (result.cell == Cell.MISS) {
            mCallback.onMiss();
        } else {
            Ln.d(mName + ": my ship is hit");
            mCallback.onHit();
        }

        mOpponent.onShotResult(result);

        if (result.cell == Cell.HIT) {
            if (BoardUtils.isItDefeatedBoard(mMyBoard, mNumberOfShips)) {
                Ln.d(mName + ": I'm defeated, no turn to pass");
                if (!versionSupportsBoardReveal()) {
                    Ln.d(mName + ": opponent doesn't support board reveal = " + mOpponentVersion + ", resetting my state before win");
                    reset();
                    mCallback.onPlayerLost(null);
                }
            } else {
                Ln.d(mName + ": I'm hit - " + mOpponent + " continues");
                mOpponent.go();
            }
        }
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        mOpponent = opponent;
        Ln.d(mName + ": my opponent is " + mOpponent);
        opponent.setOpponentVersion(Opponent.CURRENT_VERSION);
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        ChatMessage message = ChatMessage.newEnemyMessage(text);
        Ln.d(mName + ": I received message: " + message);
        if (mChatListener != null) {
            mChatListener.showChatCrouton(message);
        }
        mCallback.onMessage(text);
    }

    @Override
    public void onLost(@NonNull Board board) {
        if (!BoardUtils.isItDefeatedBoard(mMyBoard, mNumberOfShips)) {
            reportException("lost while not defeated: " + mMyBoard.getShips());
        }

        Ln.d(mName + ": resetting my state before lost");
        reset();
        mCallback.onPlayerLost(board);
    }

    @Override
    public void setOpponentVersion(int ver) {
        mOpponentVersion = ver;
        Ln.d(mName + ": opponent's protocol version: v" + ver);
    }

    public boolean isOpponentReady() {
        return mOpponentReady;
    }

    /**
     * marks the aimed cell
     */
    @NonNull
    private ShotResult createResultForShootingAt(@NonNull Vector aim) {
        // ship if found will be shot and returned
        LocatedShip locatedShip = mMyBoard.getShipAt(aim);

        if (locatedShip == null) {
            mMyBoard.setCell(Cell.MISS, aim);
        } else {
            Ship ship = locatedShip.ship;
            mMyBoard.setCell(Cell.HIT, aim);
            ship.shoot();

            if (ship.isDead()) {
                return new ShotResult(aim, mMyBoard.getCell(aim), locatedShip);
            }
        }

        return new ShotResult(aim, mMyBoard.getCell(aim));
    }

    private void updateEnemyBoard(@NonNull ShotResult result) {
        if (result.locatedShip == null) {
            mEnemyBoard.setCell(result.cell, result.aim);
        } else {
            Ship ship = result.locatedShip.ship;
            killShip(ship);
            mEnemyBoard.setCell(result.cell, result.aim);
            Vector location = BoardUtils.findShipLocation(mEnemyBoard);
            mEnemyBoard.addShip(ship, location);
        }
        Ln.v(this + ": opponent's board: " + mEnemyBoard);
    }

    private void killShip(@NonNull Ship ship) {
        while (!ship.isDead()) {
            ship.shoot();
        }
    }

    private boolean opponentStarts() {
        return mMyBid < mEnemyBid;
    }

    @NonNull
    public Board getEnemyBoard() {
        return mEnemyBoard;
    }

    @NonNull
    public Board getBoard() {
        // TODO: return unmodifiable board
        return mMyBoard;
    }

    protected final boolean ready() {
        return mMyBid != NOT_READY;
    }

    @NonNull
    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }
}
