package com.ivygames.morskoiboi.multiplayer;

import android.support.annotation.NonNull;

import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.BoardSerialization;
import com.ivygames.battleship.board.Coordinate;
import com.ivygames.battleship.board.CoordinateSerialization;
import com.ivygames.battleship.shot.ShotResult;
import com.ivygames.battleship.shot.ShotResultSerialization;

import org.commons.logger.Ln;

public abstract class AbstractOnlineOpponent implements Opponent {
    protected Opponent mOpponent;

    @NonNull
    private String mName;

    protected static final char NAME = 'N';
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

    protected final void onRealTimeMessageReceived(String message) {
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
                mOpponent.onShotAt(CoordinateSerialization.fromJson(body));
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
    public void onShotResult(@NonNull ShotResult shotResult) {
        send(SHOOT_RESULT + ShotResultSerialization.toJson(shotResult).toString());
    }

    @Override
    public void onShotAt(@NonNull Coordinate aim) {
        send(SHOOT + CoordinateSerialization.toJson(aim).toString());
    }

    @Override
    public void go() {
        send(String.valueOf(GO));
    }

    @Override
    public void onEnemyBid(int bid) {
        send(String.valueOf(BID) + bid);
    }

    @NonNull
    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void onLost(@NonNull Board board) {
        send(WIN + BoardSerialization.toJson(board).toString());
    }

    @Override
    public void setOpponentVersion(int ver) {
        send(VERSION + String.valueOf(ver));
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        send(MESSAGE + text);
    }

    @Override
    public String toString() {
        String name = getName();
        return (name == null ? "still_unnamed" : name) + "#" + (hashCode() % 1000);
    }

}
