package com.ivygames.morskoiboi.player;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivygames.morskoiboi.AbstractOpponent;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerCallback;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class PlayerOpponent extends AbstractOpponent {

    public static volatile Board debug_board;

    @NonNull
    private final Placement mPlacement;
    @NonNull
    private final Rules mRules;
    @Nullable
    private ChatListener mChatListener;

    private boolean mPlayerReady;
    private int mOpponentVersion;

    protected Opponent mOpponent;
    @NonNull
    private PlayerCallback mCallback = new DummyCallback();

    private Handler debug_handler = new Handler(Looper.getMainLooper());
    private Runnable debug_thread_break_task = new Runnable() {
        @Override
        public void run() {
            Ln.i("---------------------- main thread ----------------------");
        }
    };

    public PlayerOpponent(@NonNull String name,
                          @NonNull Placement placement,
                          @NonNull Rules rules) {
        super(name);
        mPlacement = placement;
        mRules = rules;
        Ln.v("new player created");
    }

    @Override
    protected void reset() {
        Ln.d(getName() + ": resetting my state");
        super.reset();
        mPlayerReady = false;
        mOpponentVersion = 0;
    }

    public void setBoard(Board board) {
        Ln.d(getName() + ": my board is: " + board);
        mMyBoard = board;
        if (!board.getShips().isEmpty()) {
            debug_board = board;
        }
    }

    public void setChatListener(@NonNull ChatListener listener) {
        mChatListener = listener;
        Ln.v(getName() + ": my chat listener is: " + listener);
    }

    public void startBidding(int bid) {
        debug_handler.post(debug_thread_break_task);

        mMyBid = bid;
        mPlayerReady = true;

        if (isOpponentReady() && opponentStarts()) {
            Ln.d(getName() + ": opponent is ready and it is his turn");
            mOpponent.go();
        } else {
            Ln.d(getName() + ": opponent is not ready or has higher bid - sending him my bid... " + mMyBid);
            mOpponent.onEnemyBid(mMyBid);
        }
    }

    @Override
    public void onEnemyBid(int bid) {
        debug_handler.post(debug_thread_break_task);

        super.onEnemyBid(bid);
        mCallback.opponentReady();
        if (mPlayerReady && opponentStarts()) {
            Ln.d(getName() + ": I'm ready too, but it's opponent's turn, " + mOpponent + " begins");
            mOpponent.go();

            mCallback.onOpponentTurn();
        }
    }

    public void setCallback(@NonNull PlayerCallback callback) {
        mCallback = callback;
        Ln.v(getName() + ": callback set, opponent ready = " + isOpponentReady());

        if (isOpponentReady()) {
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
        Ln.v(getName() + ": callback removed");
    }

    @Override
    public void go() {
        debug_handler.post(debug_thread_break_task);

        boolean opponentReady = isOpponentReady();
        Ln.d(getName() + ": I go");
        super.go();

        if (!opponentReady) {
            Ln.d("opponent was not ready");
            mCallback.opponentReady();
        }
        mCallback.onPlayersTurn();
    }

    @Override
    public void onShotResult(@NonNull PokeResult result) {
        debug_handler.post(debug_thread_break_task);

        Ln.v(getName() + ": my shot result: " + result);
        updateEnemyBoard(result, mPlacement);

        mCallback.onShotResult(result);
        if (shipSank(result.ship)) {
            mCallback.onKill(PlayerCallback.Side.OPPONENT);
            if (mRules.isItDefeatedBoard(mEnemyBoard)) {
                Ln.d(getName() + ": actually opponent lost: " + mEnemyBoard);
                mOpponent.onLost(mMyBoard);

                reset();
                mCallback.onWin();
            }
        } else if (result.cell.isMiss()) {
            Ln.d(getName() + ": I missed - passing the turn to " + mOpponent);
            mOpponent.go();
            mCallback.onMiss(PlayerCallback.Side.OPPONENT);
            mCallback.onOpponentTurn();
        } else {
            Ln.d(getName() + ": it's a hit! - I continue");
            mCallback.onHit(PlayerCallback.Side.OPPONENT);
        }
    }

    private boolean shipSank(@Nullable Ship ship) {
        return ship != null;
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        debug_handler.post(debug_thread_break_task);

        PokeResult result = createResultForShootingAt(aim);
        Ln.v(getName() + ": hitting my board at " + aim + " yields: " + result);

        mCallback.onShotAt(aim); // TODO: either use this or onKill...
        if (shipSank(result.ship)) {
            Ln.d(getName() + ": my ship is destroyed - " + result.ship);
            markNeighbouringCellsAsOccupied(result.ship);
            mCallback.onKill(PlayerCallback.Side.PLAYER);
        } else if (result.cell.isMiss()) {
            mCallback.onMiss(PlayerCallback.Side.PLAYER);
        } else {
            Ln.d(getName() + ": my ship is hit");
            mCallback.onHit(PlayerCallback.Side.PLAYER);
        }

        mOpponent.onShotResult(result);

        if (result.cell.isHit()) {
            if (mRules.isItDefeatedBoard(mMyBoard)) {
                // If the opponent's version does not support board reveal, just switch screen in 3 seconds.
                // In the later version of the protocol opponent notifies about players defeat sending his board along.
                if (!versionSupportsBoardReveal()) {
                    Ln.d("opponent version doesn't support board reveal = " + mOpponentVersion);
                    mCallback.onLost(null);
                }
                Ln.d(getName() + ": I'm defeated, no turn to pass");
                reset();
            } else {
                Ln.d(getName() + ": I'm hit - " + mOpponent + " continues");
                mOpponent.go();
            }
        }
    }

    private boolean versionSupportsBoardReveal() {
        return mOpponentVersion >= PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL;
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        Ln.d(getName() + ": my opponent is " + opponent);
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
        Ln.d(getName() + ": I received message: " + message);
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
        Ln.v(getName() + ": opponent's protocol version: v" + ver);
    }

}
