package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

public abstract class AbstractOnlineOpponent implements Opponent, RtmSender {
    protected Opponent mOpponent;
    private String mName = BattleshipApplication.get().getString(R.string.player);

    protected static final char NAME = 'N';
    protected static final char BID = 'B';
    protected static final char GO = 'G';
    protected static final char SHOOT = 'S';
    protected static final char SHOOT_RESULT = 'R';
    protected static final char WIN = 'W';
    protected static final char VERSION = 'V';
    protected static final char MESSAGE = 'M';

    protected void onRealTimeMessageReceived(String message) {
        char opCode = message.charAt(0);
        String body = message.substring(1);
        switch (opCode) {
            case NAME:
                mName = body;
                Ln.d("opponent name: [" + mName + "]");
                break;
            case BID:
                mOpponent.bid(Integer.parseInt(body));
                break;
            case GO:
                mOpponent.go();
                break;
            case SHOOT:
                Vector2 aim = Vector2.fromJson(body);
                mOpponent.onShotAt(aim);
                break;
            case SHOOT_RESULT:
                PokeResult result = PokeResult.fromJson(body);
                mOpponent.onShotResult(result);
                break;
            case WIN:
                Board board = Board.fromJson(body);
                mOpponent.onLost(board);
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
    public void onShotResult(PokeResult pokeResult) {
        sendRtm(SHOOT_RESULT + pokeResult.toJson().toString());
    }

    @Override
    public void onShotAt(Vector2 aim) {
        sendRtm(SHOOT + aim.toJson().toString());
    }

    @Override
    public void go() {
        sendRtm(String.valueOf(GO));
    }

    @Override
    public void bid(int bid) {
        // player is ready
        sendRtm(String.valueOf(BID) + bid);
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void onLost(Board board) {
        sendRtm(WIN + board.toJson().toString());
    }

    @Override
    public void setOpponentVersion(int ver) {
        sendRtm(VERSION + String.valueOf(ver));
    }

    @Override
    public void onNewMessage(String text) {
        sendRtm(MESSAGE + text);
    }

    @Override
    public String toString() {
        String name = getName();
        return (name == null ? "still_unnamed" : name) + "#" + (hashCode() % 1000);
    }

}
