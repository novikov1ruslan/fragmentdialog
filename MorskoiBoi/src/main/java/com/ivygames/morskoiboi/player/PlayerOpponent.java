package com.ivygames.morskoiboi.player;

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

    public static final int PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL = 2;
    public static volatile Board debug_board;

    @NonNull
    private final String mName;
    @NonNull
    private final Placement mPlacement;
    @NonNull
    private final Rules mRules;
    @NonNull
    private final ChatListener mChatListener;

    private boolean mPlayerReady;
    private int mOpponentVersion;

    private Opponent mOpponent;
    private PlayerCallback mCallback;

    public PlayerOpponent(@NonNull String name,
                          @NonNull Placement placement,
                          @NonNull Rules rules,
                          @NonNull ChatListener listener) {
        mPlacement = placement;
        mName = name;
        mRules = rules;
        mChatListener = listener;
        Ln.v("new player created");
    }

    public void setCallback(@NonNull PlayerCallback callback) {
        mCallback = callback;
        Ln.v("callback set, opponent ready = " + isOpponentReady());

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

    private void reset2() {
        Ln.v("resetting");
        super.reset();
        mPlayerReady = false;
        mOpponentVersion = 0;
    }

    @Override
    public void go() {
        Ln.d("player goes");
        if (mCallback != null) {
            mCallback.onPlayersTurn();
            if (!isOpponentReady()) {
                mCallback.opponentReady();
            }
        }
        super.go();
        if (mCallback != null) {
            mCallback.go();
        }
    }

    @Override
    public void onShotResult(@NonNull PokeResult result) {
        Ln.v("shot result: " + result);
        updateEnemyBoard(result, mPlacement);

        if (mCallback != null) {
            mCallback.onShotResult(result);
        }
        if (shipSank(result.ship)) {
            if (mCallback != null) {
                mCallback.onKill(PlayerCallback.Side.OPPONENT);
            }
            if (mRules.isItDefeatedBoard(mEnemyBoard)) {
                mOpponent.onLost(mMyBoard);

                reset2();
                if (mCallback != null) {
                    mCallback.onWin();
                }
            }
        } else if (result.cell.isMiss()) {
            Ln.d(this + ": I missed - passing the turn to " + mOpponent);
            mOpponent.go();
            if (mCallback != null) {
                mCallback.onMiss(PlayerCallback.Side.OPPONENT);
                mCallback.onOpponentTurn();
            }
        } else {
            Ln.v("it's a hit! - player continues");
            if (mCallback != null) {
                mCallback.onHit(PlayerCallback.Side.OPPONENT);
            }
        }
    }

    private boolean shipSank(@Nullable Ship ship) {
        return ship != null;
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        PokeResult result = createResultForShootingAt(aim);
        Ln.v(this + ": hitting my board at " + aim + " yields result: " + result);

        if (mCallback != null) {
            mCallback.onShotAt(aim);
        }
        if (shipSank(result.ship)) {
            Ln.v(this + ": my ship is destroyed - " + result.ship);
            markNeighbouringCellsAsOccupied(result.ship);
            if (mCallback != null) {
                mCallback.onKill(PlayerCallback.Side.PLAYER);
            }
        } else if (result.cell.isMiss()) {
            if (mCallback != null) {
                mCallback.onMiss(PlayerCallback.Side.PLAYER);
            }
        } else {
            Ln.v("player's ship is hit: " + result);
            if (mCallback != null) {
                mCallback.onHit(PlayerCallback.Side.PLAYER);
            }
        }

        mOpponent.onShotResult(result);

        if (result.cell.isHit()) {
            if (mRules.isItDefeatedBoard(mMyBoard)) {
                // If the opponent's version does not support board reveal, just switch screen in 3 seconds.
                // In the later version of the protocol opponent notifies about players defeat sending his board along.
                if (!versionSupportsBoardReveal()) {
                    Ln.v("opponent version doesn't support board reveal = " + mOpponentVersion);
                    if (mCallback != null) {
                        mCallback.onLost(null);
                    }
                }
                Ln.d(this + ": I'm defeated, no turn to pass");
                reset2();
            } else {
                Ln.d(this + ": I'm hit - " + mOpponent + " continues");
                mOpponent.go();
            }
        }
    }

    private boolean versionSupportsBoardReveal() {
        return mOpponentVersion >= PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL;
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        mOpponent = opponent;
        mOpponent.setOpponentVersion(Opponent.CURRENT_VERSION);
        Ln.d(this + ": my opponent is " + opponent);
    }

    private void markNeighbouringCellsAsOccupied(@NonNull Ship ship) {
        // if is dead we remove and put ship back to mark adjacent cells as reserved
        mMyBoard.removeShipFrom(ship.getX(), ship.getY());
        mPlacement.putShipAt(mMyBoard, ship, ship.getX(), ship.getY());
    }

    @Override
    public String getName() {
        return mName;
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
        Ln.d("opponent's bid received: " + bid);
        if (mCallback != null) {
            mCallback.opponentReady();
        }

        super.onEnemyBid(bid);
        if (mPlayerReady && opponentStarts()) {
            Ln.d(this + ": I'm ready too, but it's opponent's turn - " + mOpponent + " begins");
            mOpponent.go();

            if (mCallback != null) {
                mCallback.onOpponentTurn();
            }
        }
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        ChatMessage message = ChatMessage.newEnemyMessage(text);
        Ln.d(this + " received: " + message);
        mChatListener.showChatCrouton(message);
        if (mCallback != null) {
            mCallback.onMessage(text);
        }
    }

    @Override
    public void startBidding(int bid) {
        super.startBidding(bid);
        mPlayerReady = true;

        if (isOpponentReady() && opponentStarts()) {
            Ln.d(this + ": opponent is ready and it is his turn");
            mOpponent.go();
        } else {
            Ln.d(this + ": opponent is not ready - sending him my bid");
            mOpponent.onEnemyBid(mMyBid);
        }
    }

    @Override
    public void onLost(@NonNull Board board) {
        if (!mRules.isItDefeatedBoard(mMyBoard)) {
            Ln.v("player private board: " + mMyBoard);
            reportException("lost while not defeated");
        }

        if (mCallback != null) {
            mCallback.onLost(board);
        }
    }

    @Override
    public void setOpponentVersion(int ver) {
        mOpponentVersion = ver;
        Ln.d(this + ": opponent's protocol version: v" + ver);
    }

    @Override
    public String toString() {
        return getName();
    }
}
