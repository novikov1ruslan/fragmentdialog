package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;

import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.ivygames.common.AndroidDevice;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.invitations.GameInvitation;
import com.ivygames.common.googleapi.InvitationLoadListener;
import com.ivygames.common.multiplayer.MultiplayerManager;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.progress.ProgressManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScreenTestRule extends ActivityTestRule<BattleshipActivity> {

    private ApiClient apiClient;
    private AndroidDevice device;
    private GameSettings settings;

    protected InvitationApiClient invitationApiClient = new InvitationApiClient();

    public ScreenTestRule() {
        super(BattleshipActivity.class);
        settings = mock(GameSettings.class);
        Dependencies.inject(settings);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        apiClient = mock(ApiClient.class);
        when(apiClient.isConnected()).thenReturn(true);
        Dependencies.inject(apiClient);
        Dependencies.inject(mock(MultiplayerManager.class));
        Dependencies.inject(mock(AchievementsManager.class));
        Dependencies.inject(mock(ProgressManager.class));

        device = mock(AndroidDevice.class);
        when(device.isTablet()).thenReturn(isTablet());
        Dependencies.inject(device);
    }

    private boolean isTablet() {
//        return getActivity().getResources().getBoolean(R.bool.is_tablet);
        return false;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public AndroidDevice getDevice() {
        return device;
    }

    public GameSettings settings() {
        return settings;
    }

    public InvitationApiClient getInvitationApiClient() {
        return invitationApiClient;
    }

    public static class InvitationApiClient extends DummyApiClient {

        private OnInvitationReceivedListener listener;
        private Set<String> invitations = new HashSet<>();
        private InvitationLoadListener loadListener;

        @Override
        public void registerInvitationListener(@NonNull OnInvitationReceivedListener listener) {
            this.listener = listener;
        }

        @Override
        public void loadInvitations(@NonNull InvitationLoadListener listener) {
            loadListener = listener;
            listener.onLoaded(createGameInvitations(invitations));
        }

        @Override
        public boolean isConnected() {
            return true;
        }

        public void sendInvitation(String displayName, String invitationId) {
            listener.onInvitationReceived(createInvitation(displayName, invitationId));
        }

        public void setInvitations(Set<String> invitations) {
            this.invitations = invitations;
            loadListener.onLoaded(createGameInvitations(invitations));
        }

    }

    @NonNull
    private static Collection<GameInvitation> createGameInvitations(Set<String> invitations) {
        Collection<GameInvitation> invitationsCopy = new HashSet<>();
        for (String id : invitations) {
            invitationsCopy.add(new GameInvitation("Sagi " + id, id));
        }
        return invitationsCopy;
    }

    @NonNull
    private static Invitation createInvitation(String displayName, String invitationId) {
        Invitation invitation = mock(Invitation.class);
        Participant inviter = mock(Participant.class);
        when(invitation.getInviter()).thenReturn(inviter);
        when(inviter.getDisplayName()).thenReturn(displayName);
        when(invitation.getInvitationId()).thenReturn(invitationId);
        return invitation;
    }
}
