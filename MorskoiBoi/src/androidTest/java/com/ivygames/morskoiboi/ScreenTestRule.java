package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;

import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.invitations.InvitationLoadListener;
import com.ivygames.morskoiboi.invitations.InvitationManager;
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
        GameConstants.IS_TEST_MODE = false;
        settings = mock(GameSettings.class);
        Dependencies.inject(settings);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        apiClient = mock(ApiClient.class);
        Dependencies.inject(apiClient);
        Dependencies.inject(mock(InvitationManager.class));
        Dependencies.inject(mock(AchievementsManager.class));
        Dependencies.inject(mock(ProgressManager.class));

        device = mock(AndroidDevice.class);
        when(device.isTablet()).thenReturn(isTablet());
        Dependencies.inject(device);
        Dependencies.inject(new InvitationManager(invitationApiClient));
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
        public void loadInvitations(InvitationLoadListener listener) {
            loadListener = listener;
            listener.onResult(createInvitations(invitations));
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
            loadListener.onResult(createInvitations(invitations));
        }

    }

    @NonNull
    public static Collection<Invitation> createInvitations(Set<String> invitations) {
        Collection<Invitation> invitationsCopy = new HashSet<>();
        for (String id : invitations) {
            invitationsCopy.add(createInvitation("Sagi " + id, id));
        }
        return invitationsCopy;
    }

    @NonNull
    public static Invitation createInvitation(String displayName, String invitationId) {
        Invitation invitation = mock(Invitation.class);
        Participant inviter = mock(Participant.class);
        when(invitation.getInviter()).thenReturn(inviter);
        when(inviter.getDisplayName()).thenReturn(displayName);
        when(invitation.getInvitationId()).thenReturn(invitationId);
        return invitation;
    }
}
