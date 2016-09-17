package com.ivygames.morskoiboi.player;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerCallback;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.ShotResult;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class PlayerOpponent implements Opponent {
    public static volatile Board debug_board;
    private Handler debug_handler = new Handler(Looper.getMainLooper());
    private Runnable debug_thread_break_task = new Runnable() {
        @Override
        public void run() {
            Ln.v("---------------------- main thread ----------------------");
        }
    };

    private static final int NOT_READY = -1;

    @NonNull
    private final String mName;
    @NonNull
    private final Placement mPlacement;
    @NonNull
    private final Rules mRules;

    @NonNull
    private PlayerCallback mCallback = new DummyCallback();
    @NonNull
    private Board mMyBoard = new Board();
    @NonNull
    private Board mEnemyBoard = new Board();
    private int mMyBid = NOT_READY;
    private int mEnemyBid = NOT_READY;

    @Nullable
    private ChatListener mChatListener;
    private boolean mPlayerReady;
    private int mOpponentVersion;
    protected Opponent mOpponent;
    private boolean mOpponentReady;

    public PlayerOpponent(@NonNull String name,
                          @NonNull Placement placement,
                          @NonNull Rules rules) {
        mName = name;
        mPlacement = placement;
        mRules = rules;
        Ln.v("new player created");
    }

    private void reset() {
        Ln.d(mName + ": resetting my state");
        mEnemyBoard = new Board();
        mMyBoard = new Board();
        mMyBid = NOT_READY;
        mEnemyBid = NOT_READY;
        mOpponentReady = false;
        mPlayerReady = false;
        mOpponentVersion = 0;
    }

    public void setBoard(Board board) {
        Ln.d(mName + ": my board is: " + board);
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
        debug_handler.post(debug_thread_break_task);

        mMyBid = bid;
        mPlayerReady = true;

        if (mOpponentReady && opponentStarts()) {
            Ln.d(mName + ": opponent is ready and it is his turn");
            mOpponent.go();
        } else {
            Ln.d(mName + ": opponent is not ready or has higher bid - sending him my bid... " + mMyBid);
            mOpponent.onEnemyBid(mMyBid);
        }
    }

    @Override
    public void onEnemyBid(int bid) {
        debug_handler.post(debug_thread_break_task);

        setOpponentBid(bid);
        mCallback.opponentReady();
        if (mPlayerReady && opponentStarts()) {
            Ln.d(mName + ": I'm ready too, but it's opponent's turn, " + mOpponent + " begins");
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

    public void setCallback(@NonNull PlayerCallback callback) {
        mCallback = callback;
        Ln.v(mName + ": callback set, opponent ready = " + mOpponentReady);

        if (mOpponentReady) {
            mCallback.opponentReady();

            if (mPlayerReady) {
                if (opponentStarts()) {
                    mCallback.onOpponentTurn();
                } else {
                    mCallback.onPlayersTurn();
                }
            }
        }
    }

    public void removeCallback() {
        mCallback = new DummyCallback();
        Ln.v(mName + ": callback removed");
    }

    @Override
    public void go() {
        debug_handler.post(debug_thread_break_task);

        boolean opponentReady = mOpponentReady;
        Ln.d(mName + ": I go");
        if (!mOpponentReady) {
            Ln.v(mName + ": opponent is ready");
            mOpponentReady = true;
        }

        if (!opponentReady) {
            Ln.d("opponent was not ready");
            mCallback.opponentReady();
        }
        mCallback.onPlayersTurn();
    }

    @Override
    public void onShotResult(@NonNull ShotResult result) {
        debug_handler.post(debug_thread_break_task);

        Ln.v(mName + ": -> my shot result: " + result);
        updateEnemyBoard(result);

        mCallback.onShotResult(result);
        if (shipSank(result.ship)) {
            mCallback.onKill(PlayerCallback.Side.OPPONENT);
            if (mRules.isItDefeatedBoard(mEnemyBoard)) {
                Ln.d(mName + ": actually opponent lost");

                // If the opponent's version does not support board reveal, just switch screen in 3 seconds.
                // In the later version of the protocol opponent notifies about players defeat sending his board along.
                if (versionSupportsBoardReveal()) {
                    mOpponent.onLost(mMyBoard);
                } else {
                    Ln.d(this + ": opponent version doesn't support board reveal = " + mOpponentVersion);
                    mCallback.onLost(null);
                }

                reset();
                mCallback.onWin();
            }
        } else if (result.cell.isMiss()) {
            Ln.d(mName + ": I missed - passing the turn to " + mOpponent);
            mOpponent.go();
            mCallback.onMiss(PlayerCallback.Side.OPPONENT);
            mCallback.onOpponentTurn();
        } else {
            Ln.d(mName + ": it's a hit! - I continue");
            mCallback.onHit(PlayerCallback.Side.OPPONENT);
        }
    }

    private boolean shipSank(@Nullable Ship ship) {
        return ship != null;
    }

    private boolean versionSupportsBoardReveal() {
        return mOpponentVersion >= PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL;
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        debug_handler.post(debug_thread_break_task);

        ShotResult result = createResultForShootingAt(aim);
        Ln.v(mName + ": <- hitting my board at " + aim + " yields: " + result);

        mCallback.onShotAt(aim);
        if (shipSank(result.ship)) {
            Ln.d(mName + ": my ship is destroyed - " + result.ship);
            markNeighbouringCellsAsOccupied(result.ship);
            mCallback.onKill(PlayerCallback.Side.PLAYER);
        } else if (result.cell.isMiss()) {
            mCallback.onMiss(PlayerCallback.Side.PLAYER);
        } else {
            Ln.d(mName + ": my ship is hit");
            mCallback.onHit(PlayerCallback.Side.PLAYER);
        }

        mOpponent.onShotResult(result);

        if (result.cell.isHit()) {
            if (mRules.isItDefeatedBoard(mMyBoard)) {
                Ln.d(mName + ": I'm defeated, no turn to pass");
                reset();
            } else {
                Ln.d(mName + ": I'm hit - " + mOpponent + " continues");
                mOpponent.go();
            }
        }
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        Ln.d(mName + ": my opponent is " + opponent);
        mOpponent = opponent;
        opponent.setOpponentVersion(Opponent.CURRENT_VERSION);
    }

    private void markNeighbouringCellsAsOccupied(@NonNull Ship ship) {
        // if is dead we remove and put ship back to mark adjacent cells as reserved
        mPlacement.removeShipFrom(mMyBoard, ship.getX(), ship.getY());
        mPlacement.putShipAt(mMyBoard, ship, ship.getX(), ship.getY());
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
        debug_handler.post(debug_thread_break_task);

        if (!mRules.isItDefeatedBoard(mMyBoard)) {
            Ln.e("player private board: " + mMyBoard);
            reportException("lost while not defeated");
        }

        mCallback.onLost(board);
    }

    @Override
    public void setOpponentVersion(int ver) {
        debug_handler.post(debug_thread_break_task);

        mOpponentVersion = ver;
        Ln.v(mName + ": opponent's protocol version: v" + ver);
    }

    public boolean isOpponentReady() {
        return mOpponentReady;
    }

    /**
     * marks the aimed cell
     */
    private ShotResult createResultForShootingAt(@NonNull Vector2 aim) {
        // ship if found will be shot and returned
        Ship ship = mMyBoard.getFirstShipAt(aim);

        // this cell will be changed and returned in result later
        Cell cell = mMyBoard.getCellAt(aim);

        if (ship == null) {
            cell.setMiss();
        } else {
            cell.setHit();
            ship.shoot();

            if (ship.isDead()) {
                return new ShotResult(aim, cell, ship);
            }
        }

        return new ShotResult(aim, cell);
    }

    private void updateEnemyBoard(@NonNull ShotResult result) {
        Ship ship = result.ship;
        if (ship == null) {
            mEnemyBoard.setCell(result.cell, result.aim);
        } else {
            mPlacement.putShipAt(mEnemyBoard, ship, ship.getX(), ship.getY());
        }
        Ln.v(this + ": opponent's board: " + mEnemyBoard);
    }

    private boolean opponentStarts() {
        return mMyBid < mEnemyBid;
    }

    public Board getEnemyBoard() {
        return mEnemyBoard;
    }

    public Board getBoard() {
        return mMyBoard;
    }

    protected final boolean ready() {
        return mPlayerReady;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }
}
