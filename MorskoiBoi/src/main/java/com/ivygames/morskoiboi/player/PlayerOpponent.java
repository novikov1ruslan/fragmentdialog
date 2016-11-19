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
import com.ivygames.morskoiboi.screen.boardsetup.BoardUtils;

import org.commons.logger.Ln;

import java.util.HashSet;
import java.util.Set;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class PlayerOpponent implements Opponent {
    public static volatile Board debug_board;
    private final Handler debug_handler = new Handler(Looper.getMainLooper());
    private final Runnable debug_thread_break_task = new Runnable() {
        @Override
        public void run() {
            Ln.v("---------------------- main thread ----------------------");
        }
    };

    private static final int NOT_READY = -1;

    @NonNull
    private final Set<PlayerCallback> mCallbacks = new HashSet<>();
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

    @NonNull
    private final String mName;
    @NonNull
    private final Rules mRules;
    private boolean mGameStarted;

    protected PlayerOpponent(@NonNull String name, @NonNull Rules rules) {
        mName = name;
        mRules = rules;
        Ln.v("new player created");
    }

    private void reset() {
        mEnemyBoard = new Board();
        mMyBoard = new Board();
        mMyBid = NOT_READY;
        mEnemyBid = NOT_READY;
        mOpponentReady = false;
        mPlayerReady = false;
        mGameStarted = false;
//        mOpponentVersion = 0;
    }

    public void setBoard(Board board) {
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
        debug_handler.post(debug_thread_break_task);

        mMyBid = bid;
        mPlayerReady = true;

        if (mOpponentReady && opponentStarts()) {
            Ln.d(mName + ": my bid: " + bid + ", opponent is ready and it is his turn");
            passFirstTurn();
        } else {
            Ln.d(mName + ": opponent " + (mOpponentReady ? "is not ready" : "has higher bid")
                    + " - sending him my bid: " + bid);
            mOpponent.onEnemyBid(bid);
        }
    }

    private void passFirstTurn() {
        if (!mGameStarted) {
            mGameStarted = true;
            mOpponent.go();
        }
    }

    @Override
    public void onEnemyBid(int bid) {
        debug_handler.post(debug_thread_break_task);

        setOpponentBid(bid);
        notifyOpponentReady();

        if (mPlayerReady && opponentStarts()) {
            Ln.d(mName + ": I'm ready , but it's opponent's turn, " + mOpponent + " begins");
            passFirstTurn();

            notifyOpponentTurn();
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

    public void registerCallback(@NonNull PlayerCallback callback) {
        mCallbacks.add(callback);
        Ln.v(mName + ": [" + callback + "] callback added");

        if (mOpponentReady) {
            Ln.d(mName + ": opponent ready, notifying");
            callback.opponentReady();

            if (mPlayerReady) {
                if (opponentStarts()) {
                    callback.onOpponentTurn();
                } else {
                    callback.onPlayersTurn();
                }
            }
        }
    }

    public void unregisterCallback(@NonNull PlayerCallback callback) {
        mCallbacks.remove(callback);
        Ln.v(mName + ": callback removed: " + callback);
    }

    @Override
    public void go() {
        debug_handler.post(debug_thread_break_task);

        Ln.d(mName + ": I go");
        if (!mOpponentReady) {
            Ln.v(mName + ": opponent was not ready, but ready now");
            mOpponentReady = true;

            notifyOpponentReady();
        }

        notifyPlayersTurn();
    }

    @Override
    public void onShotResult(@NonNull ShotResult result) {
        debug_handler.post(debug_thread_break_task);

        Ln.v(mName + ": -> my shot result: " + result);
        updateEnemyBoard(result);

        notifyOnShotResult(result);
        if (shipSank(result.ship)) {
            notifyOnKillEnemy();
            if (BoardUtils.isItDefeatedBoard(mEnemyBoard, mRules)) {
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
                notifyOnWin();
            }
        } else if (result.cell == Cell.MISS) {
            Ln.d(mName + ": I missed - passing the turn to " + mOpponent);
            mOpponent.go();
            notifyOnMiss();
            notifyOpponentTurn();
        } else {
            Ln.d(mName + ": it's a hit!");
            notifyOnHit();
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

        notifyOnShotAt(aim);
        if (shipSank(result.ship)) {
            Ln.d(mName + ": my ship is destroyed - " + result.ship);
            // FIXME: add unit test for removed below line
//            Placement.putShipAt(mMyBoard, result.ship, result.ship.getX(), result.ship.getY());
            notifyOnKillPlayer();
        } else if (result.cell == Cell.MISS) {
            notifyOnMiss();
        } else {
            Ln.d(mName + ": my ship is hit");
            notifyOnHit();
        }

        mOpponent.onShotResult(result);

        if (result.cell == Cell.HIT) {
            if (BoardUtils.isItDefeatedBoard(mMyBoard, mRules)) {
                Ln.d(mName + ": I'm defeated, no turn to pass");
                if (!versionSupportsBoardReveal()) {
                    Ln.d(mName + ": opponent version doesn't support board reveal = " + mOpponentVersion);
                    Ln.d(mName + ": resetting my state before win");
                    reset();
                    notifyOnWin();
                }
            } else {
                Ln.d(mName + ": I'm hit - " + mOpponent + " continues");
                mOpponent.go();
            }
        }
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        mOpponent = new HandlerDelegateOpponent(opponent);
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
        notifyOnMessage(text);
    }

    @Override
    public void onLost(@NonNull Board board) {
        debug_handler.post(debug_thread_break_task);

        if (!BoardUtils.isItDefeatedBoard(mMyBoard, mRules)) {
            reportException("lost while not defeated: " + mMyBoard.getShips());
        }

        Ln.d(mName + ": resetting my state before lost");
        reset();
        notifyOnLost(board);
    }

    @Override
    public void setOpponentVersion(int ver) {
        debug_handler.post(debug_thread_break_task);

        mOpponentVersion = ver;
        Ln.d(mName + ": opponent's protocol version: v" + ver);
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

        if (ship == null) {
            mMyBoard.setCell(Cell.MISS, aim);
        } else {
            mMyBoard.setCell(Cell.HIT, aim);
            ship.shoot();

            if (ship.isDead()) {
                return new ShotResult(aim, mMyBoard.getCellAt(aim), ship);
            }
        }

        return new ShotResult(aim, mMyBoard.getCellAt(aim));
    }

    private void updateEnemyBoard(@NonNull ShotResult result) {
        Ship ship = result.ship;
        if (ship == null) {
            mEnemyBoard.setCell(result.cell, result.aim);
        } else {
            mEnemyBoard.setCell(result.cell, result.aim);
            Placement.putShipAt(mEnemyBoard, ship, ship.getX(), ship.getY());
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

    private void notifyOpponentTurn() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onOpponentTurn();
        }
    }

    private void notifyOnHit() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onHit();
        }
    }

    private void notifyOnMiss() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onMiss();
        }
    }

    private void notifyOnKillEnemy() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onKillEnemy();
        }
    }

    private void notifyOnKillPlayer() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onKillPlayer();
        }
    }

    private void notifyOnLost(@NonNull Board board) {
        for (PlayerCallback callback : mCallbacks) {
            callback.onLost(board);
        }
    }

    private void notifyOnMessage(@NonNull String text) {
        for (PlayerCallback callback : mCallbacks) {
            callback.onMessage(text);
        }
    }

    private void notifyOnWin() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onWin();
        }
    }

    private void notifyOnShotAt(@NonNull Vector2 aim) {
        for (PlayerCallback callback : mCallbacks) {
            callback.onShotAt(aim);
        }
    }

    private void notifyPlayersTurn() {
        for (PlayerCallback callback : mCallbacks) {
            callback.onPlayersTurn();
        }
    }

    private void notifyOnShotResult(@NonNull ShotResult result) {
        for (PlayerCallback callback : mCallbacks) {
           callback.onShotResult(result);
        }
    }

    private void notifyOpponentReady() {
        for (PlayerCallback callback : mCallbacks) {
            callback.opponentReady();
        }
    }

    @Override
    public String toString() {
        return mName;
    }
}
