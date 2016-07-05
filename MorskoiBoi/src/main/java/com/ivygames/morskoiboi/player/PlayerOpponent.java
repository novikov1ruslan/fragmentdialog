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

    public PlayerOpponent(@NonNull String name,
                          @NonNull Placement placement,
                          @NonNull Rules rules) {
        super(name);
        mPlacement = placement;
        mRules = rules;
        Ln.v("new player created");
    }

    public void setChatListener(@NonNull ChatListener listener) {
        mChatListener = listener;
        Ln.v(getName() + ": my chat listener is: " + listener);
    }

    private void reset2() {
        Ln.v(getName() + ": resetting");
        super.reset();
        mPlayerReady = false;
        mOpponentVersion = 0;
    }

    public void startBidding(int bid) {
        mMyBid = bid;
        mPlayerReady = true;

        if (isOpponentReady() && opponentStarts()) {
            Ln.v(getName() + ": opponent is ready and it is his turn");
            mOpponent.go();
        } else {
            Ln.v(getName() + ": opponent is not ready or has higher bid - sending him my bid... " + mMyBid);
            mOpponent.onEnemyBid(mMyBid);
        }
    }

    @Override
    public void onEnemyBid(int bid) {
        super.onEnemyBid(bid);
        mCallback.opponentReady();
        if (mPlayerReady && opponentStarts()) {
            Ln.v(getName() + ": I'm ready too, but it's opponent's turn, " + mOpponent + " begins");
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
        boolean opponentReady = isOpponentReady();
        super.go();
        Ln.v(getName() + ": I go, opponent ready = " + opponentReady);

        mCallback.onPlayersTurn();
        if (!opponentReady) {
            mCallback.opponentReady();
        }
        mCallback.onPlayerGoes();
    }

    @Override
    public void onShotResult(@NonNull PokeResult result) {
        Ln.v(getName() + ": my shot result: " + result);
        updateEnemyBoard(result, mPlacement);

        mCallback.onShotResult(result);
        if (shipSank(result.ship)) {
            mCallback.onKill(PlayerCallback.Side.OPPONENT);
            if (mRules.isItDefeatedBoard(mEnemyBoard)) {
                mOpponent.onLost(mMyBoard);

                reset2();
                mCallback.onWin();
            }
        } else if (result.cell.isMiss()) {
            Ln.v(getName() + ": I missed - passing the turn to " + mOpponent);
            mOpponent.go();
            mCallback.onMiss(PlayerCallback.Side.OPPONENT);
            mCallback.onOpponentTurn();
        } else {
            Ln.v(getName() + ": it's a hit! - I continue");
            mCallback.onHit(PlayerCallback.Side.OPPONENT);
        }
    }

    private boolean shipSank(@Nullable Ship ship) {
        return ship != null;
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        PokeResult result = createResultForShootingAt(aim);
        Ln.v(getName() + ": hitting my board at " + aim + " yields: " + result);

        mCallback.onShotAt(aim);
        if (shipSank(result.ship)) {
            Ln.v(getName() + ": my ship is destroyed - " + result.ship);
            markNeighbouringCellsAsOccupied(result.ship);
            mCallback.onKill(PlayerCallback.Side.PLAYER);
        } else if (result.cell.isMiss()) {
            mCallback.onMiss(PlayerCallback.Side.PLAYER);
        } else {
            Ln.v(getName() + ": my ship is hit: " + result);
            mCallback.onHit(PlayerCallback.Side.PLAYER);
        }

        mOpponent.onShotResult(result);

        if (result.cell.isHit()) {
            if (mRules.isItDefeatedBoard(mMyBoard)) {
                // If the opponent's version does not support board reveal, just switch screen in 3 seconds.
                // In the later version of the protocol opponent notifies about players defeat sending his board along.
                if (!versionSupportsBoardReveal()) {
                    Ln.v("opponent version doesn't support board reveal = " + mOpponentVersion);
                    mCallback.onLost(null);
                }
                Ln.v(getName() + ": I'm defeated, no turn to pass");
                reset2();
            } else {
                Ln.v(getName() + ": I'm hit - " + mOpponent + " continues");
                mOpponent.go();
            }
        }
    }

    private boolean versionSupportsBoardReveal() {
        return mOpponentVersion >= PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL;
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        Ln.v(getName() + ": my opponent is " + opponent);
        mOpponent = opponent;
        opponent.setOpponentVersion(Opponent.CURRENT_VERSION);
    }

    private void markNeighbouringCellsAsOccupied(@NonNull Ship ship) {
        // if is dead we remove and put ship back to mark adjacent cells as reserved
        mMyBoard.removeShipFrom(ship.getX(), ship.getY());
        mPlacement.putShipAt(mMyBoard, ship, ship.getX(), ship.getY());
    }

    public void setBoard(Board board) {
        Ln.v(getName() + ": my board is: " + board);
        mMyBoard = board;
        debug_board = board;
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        ChatMessage message = ChatMessage.newEnemyMessage(text);
        Ln.v(getName() + " received: " + message);
        if (mChatListener != null) {
            mChatListener.showChatCrouton(message);
        }
        mCallback.onMessage(text);
    }

    @Override
    public void onLost(@NonNull Board board) {
        if (!mRules.isItDefeatedBoard(mMyBoard)) {
            Ln.v("player private board: " + mMyBoard);
            reportException("lost while not defeated");
        }

        mCallback.onLost(board);
    }

    @Override
    public void setOpponentVersion(int ver) {
        mOpponentVersion = ver;
        Ln.v(getName() + ": opponent's protocol version: v" + ver);
    }

}
