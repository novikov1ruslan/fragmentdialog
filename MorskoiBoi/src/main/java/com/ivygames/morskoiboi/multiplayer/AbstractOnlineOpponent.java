package com.ivygames.morskoiboi.multiplayer;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.BoardSerialization;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.board.VectorSerialization;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.battleship.shot.ShotResultSerialization;

import org.commons.logger.Ln;

public abstract class AbstractOnlineOpponent implements Opponent {
    private Opponent mOpponent;

    @NonNull
    private String mName;

    private static final char NAME = 'N';
    private static final char BID = 'B';
    private static final char GO = 'G';
    private static final char SHOOT = 'S';
    private static final char SHOOT_RESULT = 'R';
    private static final char WIN = 'W';
    private static final char VERSION = 'V';
    private static final char MESSAGE = 'M';

    protected AbstractOnlineOpponent(@NonNull String defaultName) {
        mName = defaultName;
    }

    protected abstract void send(@NonNull String message);

    protected final void onRealTimeMessageReceived(@NonNull String message) {
        char opCode = message.charAt(0);
        String body = message.substring(1);
        switch (opCode) {
            case NAME:
                mName = body;
                Ln.d("opponent name: [" + mName + "]");
                break;
            case BID:
                mOpponent.onEnemyBid(Integer.parseInt(body));
                break;
            case GO:
                mOpponent.go();
                break;
            case SHOOT:
                mOpponent.onShotAt(VectorSerialization.fromJson(body));
                break;
            case SHOOT_RESULT:
                mOpponent.onShotResult(ShotResultSerialization.fromJson(body));
                break;
            case WIN:
                mOpponent.onLost(BoardSerialization.fromJson(body));
                break;
            case VERSION:
                mOpponent.setOpponentVersion(Integer.parseInt(body));
                break;
            case MESSAGE:
                mOpponent.onNewMessage(getName() + ": " + body);
                break;

            default:
                Ln.w("unprocessed message: [" + opCode + "]");
                break;
        }
    }

    @Override
    public final void setOpponent(@NonNull Opponent opponent) {
        mOpponent = opponent;
        String name = mOpponent.getName();
        if (TextUtils.isEmpty(name)) {
            name = "Player"; // TODO: think about better solution
        }
        send(NAME + name);
    }

    @Override
    public final void onShotResult(@NonNull ShotResult shotResult) {
        send(SHOOT_RESULT + ShotResultSerialization.toJson(shotResult).toString());
    }

    @Override
    public final void onShotAt(@NonNull Vector aim) {
        send(SHOOT + VectorSerialization.toJson(aim).toString());
    }

    @Override
    public final void go() {
        send(String.valueOf(GO));
    }

    @Override
    public final void onEnemyBid(int bid) {
        send(String.valueOf(BID) + bid);
    }

    @NonNull
    @Override
    public final String getName() {
        return mName;
    }

    @Override
    public final void onLost(@NonNull Board board) {
        send(WIN + BoardSerialization.toJson(board).toString());
    }

    @Override
    public final void setOpponentVersion(int ver) {
        send(VERSION + String.valueOf(ver));
    }

    @Override
    public final void onNewMessage(@NonNull String text) {
        send(MESSAGE + text);
    }

    @Override
    public String toString() {
        return getName() + "#" + hashCode() % 1000;
    }

}
