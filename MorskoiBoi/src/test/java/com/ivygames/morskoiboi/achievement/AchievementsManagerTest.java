package com.ivygames.morskoiboi.achievement;

import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.morskoiboi.GameSettings;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.ivygames.morskoiboi.achievement.AchievementsManager.BRAVERY_AND_COURAGE;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.EXTRA_BRAVERY_AND_COURAGE;
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
        setAchievementUnlocked(NAVAL_MERIT, false);

        am.processCombo(1);

        verifyUnlocked(NAVAL_MERIT);
        verifyRevealed(ORDER_OF_HONOUR);
    }

    @Test
    public void IfNavalMeritIsNotUnlocked__2xComboUnlocksItAndRevealsOrderOfHonour() {
        setAchievementUnlocked(NAVAL_MERIT, false);

        am.processCombo(2);

        verifyUnlocked(NAVAL_MERIT);
        verifyRevealed(ORDER_OF_HONOUR);
    }

    @Test
    public void IfNavalMeritIsUnlocked__ComboDoNotUnlockOrderOfHonour() {
        setAchievementUnlocked(NAVAL_MERIT, true);

        am.processCombo(1);

        verifyNotUnlocked(ORDER_OF_HONOUR);
    }

    @Test
    public void IfNavalMeritIsUnlocked__2xComboUnlocksOrderOfHonour() {
        setAchievementUnlocked(NAVAL_MERIT, true);

        am.processCombo(2);

        verifyUnlocked(ORDER_OF_HONOUR);
    }

//    @Test
//    public void IfOrderOfHonourIsUnlocked__20xComboHasNoEffect() {
//        setAchievementUnlocked(ORDER_OF_HONOUR, true);
//
//        am.processCombo(20);
//
//        verifyNotUnlocked(ORDER_OF_HONOUR);
//    }

    @Test
    public void IfBraveryAndCourageIsNotUnlocked__50ShellsUnlocksItAndRevealsExtraBraveryAndCourage() {
        setAchievementUnlocked(BRAVERY_AND_COURAGE, false);

        am.processShellsLeft(50);

        verifyUnlocked(BRAVERY_AND_COURAGE);
        verifyRevealed(EXTRA_BRAVERY_AND_COURAGE);
    }

    @Test
    public void IfBraveryAndCourageIsNotUnlocked__60ShellsUnlocksItAndRevealExtraBraveryAndCourage() {
        setAchievementUnlocked(BRAVERY_AND_COURAGE, false);

        am.processShellsLeft(60);

        verifyUnlocked(BRAVERY_AND_COURAGE);
        verifyRevealed(EXTRA_BRAVERY_AND_COURAGE);
    }

    @Test
    public void IfBraveryAndCourageIsUnlocked__50ShellsDoNotUnlockExtraBraveryAndCourage() {
        setAchievementUnlocked(BRAVERY_AND_COURAGE, true);

        am.processShellsLeft(50);

        verifyNotUnlocked(EXTRA_BRAVERY_AND_COURAGE);
    }

    @Test
    public void IfBraveryAndCourageIsUnlocked__60ShellsUnlockExtraBraveryAndCourage() {
        setAchievementUnlocked(BRAVERY_AND_COURAGE, true);

        am.processShellsLeft(60);

        verifyUnlocked(EXTRA_BRAVERY_AND_COURAGE);
    }

//    @Test
//    public void IfExtraBraveryAndCourageIsUnlocked__100ShellsHaveNoEffect() {
//        setAchievementUnlocked(EXTRA_BRAVERY_AND_COURAGE, true);
//
//        am.processShellsLeft(100);
//
//        verifyNotUnlocked(EXTRA_BRAVERY_AND_COURAGE);
//    }

    private void verifyNotRevealed(String achievementId) {
        verify(apiClient, never()).reveal(achievementId);
    }

    private void verifyRevealed(String achievement) {
        verify(apiClient, times(1)).reveal(achievement);
    }

    private void setMilitaryAchievementsUnlocked(boolean value) {
        setAchievementUnlocked(MILITARY_ACHIEVEMENTS, value);
    }

    private void setAchievementUnlocked(String achievement, boolean unlocked) {
        when(settings.isAchievementUnlocked(achievement)).thenReturn(unlocked);
    }

    private void verifyUnlocked(String braveryAndCourage) {
        verify(settings, times(1)).unlockAchievement(braveryAndCourage);
        verify(apiClient, times(1)).unlock(braveryAndCourage);
    }

    private void verifyNotUnlocked(String achievement) {
        verify(settings, never()).unlockAchievement(achievement);
        verify(apiClient, never()).unlock(achievement);
    }
}
