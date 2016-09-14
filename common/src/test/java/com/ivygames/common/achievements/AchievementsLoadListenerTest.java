package com.ivygames.common.achievements;

import com.google.android.gms.games.achievement.Achievement;
import com.ivygames.common.googleapi.ApiClient;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AchievementsLoadListenerTest {

    private AchievementsLoadListener listener;
    @Mock
    private ApiClient apiClient;
    @Mock
    private AchievementsSettings settings;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        listener = new AchievementsLoadListener(apiClient, settings);
    }

    @Test
    public void IfAchievementRemotelyUnlocked__UnlockLocally() {
        List<GameAchievement> achievements = achievements("id", Achievement.STATE_UNLOCKED);

        listener.onAchievementsLoaded(achievements);

        verify(settings, times(1)).unlockAchievement("id");
    }

    @Test
    public void IfAchievementRemotelyNotUnlocked__DoNotUnlockLocally() {
        List<GameAchievement> achievements = achievements("id", Achievement.STATE_REVEALED);

        listener.onAchievementsLoaded(achievements);

        verify(settings, never()).unlockAchievement(anyString());
    }

    @Test
    public void IfAchievementRemotelyRevealed_AndLocallyUnlocked__UnlockRemotely() {
        List<GameAchievement> achievements = achievements("id", Achievement.STATE_REVEALED);
        when(settings.isAchievementUnlocked("id")).thenReturn(true);

        listener.onAchievementsLoaded(achievements);

        verify(apiClient, times(1)).unlockAchievement("id");
    }

    @Test
    public void IfAchievementRemotelyRevealed_AndLocallyLocked__RevealLocally() {
        List<GameAchievement> achievements = achievements("id", Achievement.STATE_REVEALED);
        when(settings.isAchievementUnlocked("id")).thenReturn(false);

        listener.onAchievementsLoaded(achievements);

        verify(settings, times(1)).revealAchievement("id");
    }

    @Test
    public void IfAchievementHidden_AndLocallyRevealed__RevealRemotely() {
        List<GameAchievement> achievements = achievements("id", Achievement.STATE_HIDDEN);
        when(settings.isAchievementRevealed("id")).thenReturn(true);

        listener.onAchievementsLoaded(achievements);

        verify(apiClient, times(1)).revealAchievement("id");
    }

    @Test
    public void IfAchievementHidden_AndLocallyUnlocked__UnlockRemotely() {
        List<GameAchievement> achievements = achievements("id", Achievement.STATE_HIDDEN);
        when(settings.isAchievementUnlocked("id")).thenReturn(true);

        listener.onAchievementsLoaded(achievements);

        verify(apiClient, times(1)).unlockAchievement("id");
    }

    @Test
    public void IfAchievementHidden__NothingHappens() {
        List<GameAchievement> achievements = achievements("id", Achievement.STATE_HIDDEN);

        listener.onAchievementsLoaded(achievements);

        verify(apiClient, never()).unlockAchievement(anyString());
        verify(apiClient, never()).revealAchievement(anyString());
    }

    private List<GameAchievement> achievements(String id, int state) {
        return Collections.singletonList(new GameAchievement(id, "", state));
    }

}