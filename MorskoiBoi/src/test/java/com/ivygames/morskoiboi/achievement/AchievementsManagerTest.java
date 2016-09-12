package com.ivygames.morskoiboi.achievement;

import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.morskoiboi.GameSettings;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.ivygames.morskoiboi.achievement.AchievementsManager.MILITARY_ACHIEVEMENTS;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.NAVAL_MERIT;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.ORDER_OF_HONOUR;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AchievementsManagerTest {

    private AchievementsManager am;
    @Mock
    private ApiClient apiClient;
    @Mock
    private GameSettings settings;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        am = new AchievementsManager(apiClient, settings);
        when(apiClient.isConnected()).thenReturn(true);
    }

    @Test
    public void IfMilitaryAchievementsNotYetUnlocked_When15000pointsScored__TheAchievementIncremented() {
        setMilitaryAchievementsUnlocked(false);

        am.processScores(15000);

        verify(apiClient, times(1)).increment(MILITARY_ACHIEVEMENTS, 1);
    }

    @Test
    public void IfMilitaryAchievementsUnlocked__TheAchievementNotIncremented() {
        setMilitaryAchievementsUnlocked(true);

        am.processScores(15000);

        verify(apiClient, never()).increment(anyString(), anyInt());
    }

    @Test
    public void IfLessTHan15000pointsScored__TheAchievementNotIncremented() {
        setMilitaryAchievementsUnlocked(false);

        am.processScores(1499);

        verify(apiClient, never()).increment(anyString(), anyInt());
    }

    @Test
    public void IfApiClientNotConnected__TheAchievementNotIncremented() {
        setMilitaryAchievementsUnlocked(false);
        when(apiClient.isConnected()).thenReturn(true);

        am.processScores(15000);

        verify(apiClient, times(1)).increment(MILITARY_ACHIEVEMENTS, 1);
    }

    @Test
    public void IfNavalMeritIsNotUnlocked__ComboUnlocksItAndRevealsOrderOfHonour() {
        when(settings.isAchievementUnlocked(NAVAL_MERIT)).thenReturn(false);

        am.processCombo(1);

        verify(settings, times(1)).unlockAchievement(NAVAL_MERIT);
        verify(apiClient, times(1)).unlock(NAVAL_MERIT);
        verify(apiClient, times(1)).reveal(ORDER_OF_HONOUR);
    }

    @Test
    public void IfNavalMeritIsNotUnlocked__2xComboUnlocksItAndRevealsOrderOfHonour() {
        when(settings.isAchievementUnlocked(NAVAL_MERIT)).thenReturn(false);

        am.processCombo(2);

        verify(settings, times(1)).unlockAchievement(NAVAL_MERIT);
        verify(apiClient, times(1)).unlock(NAVAL_MERIT);
        verify(apiClient, times(1)).reveal(ORDER_OF_HONOUR);
    }

    @Test
    public void IfNavalMeritIsUnlocked__ComboHasNoEffect() {
        when(settings.isAchievementUnlocked(NAVAL_MERIT)).thenReturn(true);

        am.processCombo(1);

        verify(settings, never()).unlockAchievement(NAVAL_MERIT);
        verify(apiClient, never()).unlock(NAVAL_MERIT);
        verify(apiClient, never()).reveal(ORDER_OF_HONOUR);
    }

    @Test
    public void IfNavalMeritIsUnlocked__2xComboUnlocksOrderOfHonour() {
        when(settings.isAchievementUnlocked(NAVAL_MERIT)).thenReturn(false);

        am.processCombo(2);

        verify(settings, never()).unlockAchievement(ORDER_OF_HONOUR);
        verify(apiClient, never()).unlock(ORDER_OF_HONOUR);
    }

    @Test
    public void IfOrderOfHonourIsUnlocked__20xComboHasNoEffect() {
        when(settings.isAchievementUnlocked(ORDER_OF_HONOUR)).thenReturn(false);

        am.processCombo(20);

        verify(settings, never()).unlockAchievement(ORDER_OF_HONOUR);
        verify(apiClient, never()).unlock(ORDER_OF_HONOUR);
    }

    private void setMilitaryAchievementsUnlocked(boolean value) {
        when(settings.isAchievementUnlocked(MILITARY_ACHIEVEMENTS)).thenReturn(value);
    }
}
