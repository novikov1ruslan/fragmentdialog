package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.common.multiplayer.MultiplayerRoom;
import com.ivygames.morskoiboi.ai.AndroidGame;
import com.ivygames.morskoiboi.bluetooth.BluetoothConnection;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.Opponent;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.morskoiboi.rt.InternetGame;
import com.ivygames.morskoiboi.screen.BattleshipScreen;

import org.hamcrest.Matcher;
import org.junit.Before;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class OnlineScreen_ extends ScreenTest {

    protected static final String OPPONENT_NAME = "Sagi";

    protected TestableGame game;
    protected PlayerOpponent player;
    protected Opponent opponent;
    protected Session session;

    @Before
    public void setup() {
        super.setup();
        opponent = mock(Opponent.class);
        when(opponent.getName()).thenReturn(OPPONENT_NAME);

        player = mockPlayer();

        session = new Session(player, opponent);
        Session.bindOpponents(player, opponent);
        setGameType(Type.VS_ANDROID);
    }

    public PlayerOpponent mockPlayer() {
        PlayerOpponent player = mock(PlayerOpponent.class);
        when(player.getBoard()).thenReturn(new Board());
        when(player.getEnemyBoard()).thenReturn(new Board());

        return player;
    }

    @Override
    public BattleshipScreen newScreen() {
        return null;
    }

    @NonNull
    protected Matcher<View> wantToLeaveDialog() {
        return withText(getString(R.string.want_to_leave_room, OPPONENT_NAME));
    }

    protected final void setGameType(Type type) {
        switch (type) {
            case VS_ANDROID:
                game = new TestableGame(new AndroidGame());
                break;
            case BLUETOOTH:
                BluetoothConnection connection = mock(BluetoothConnection.class);
                game = new TestableGame(new BluetoothGame(connection));
                break;
            case INTERNET:
                MultiplayerRoom room = mock(MultiplayerRoom.class);
                game = new TestableGame(new InternetGame(room));
                break;
        }
    }

    // TODO: inline
    protected final void backToSelectGame() {
        verifyGameFinished();
        checkDisplayed(SELECT_GAME_LAYOUT);
    }

    protected Matcher<View> continueButton() {
        return withText(R.string.continue_str);
    }

    private void verifyGameFinished() {
        assertThat(game.hasFinished(), is(true));
    }

    protected final Matcher<View> opponentLeftDialog() {
        return withText(R.string.opponent_left);
    }

    protected final Matcher<View> connectionLostDialog() {
        return withText(R.string.connection_lost);
    }

    public enum Type {
        VS_ANDROID, BLUETOOTH, INTERNET
    }
}
