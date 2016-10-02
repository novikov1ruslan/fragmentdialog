package com.ivygames.common.multiplayer;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.ivygames.common.googleapi.ApiClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class MultiplayerManagerTest {

    private MultiplayerManager mm;
    @Mock
    private ApiClient apiClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mm = new MultiplayerManager(apiClient);
    }


    @Test
    public void WhenResultOkOnInvitingPlayers__RoomIsCreated() {
        RoomListener roomListener = mock(RoomListener.class);
        RealTimeMessageReceivedListener rtListener = mock(RealTimeMessageReceivedListener.class);
        mm.invitePlayers(1, roomListener, rtListener);
        Intent intent = new Intent();
        ArrayList<String> list = new ArrayList<>(Collections.singletonList("1"));
        intent.putStringArrayListExtra(Games.EXTRA_PLAYER_IDS, list);
        intent.putExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 1);
        intent.putExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 2);

        mm.handleResult(1, Activity.RESULT_OK, intent);

        verify(apiClient, times(1)).createRoom(list, 1, 2, roomListener, rtListener);
    }
}