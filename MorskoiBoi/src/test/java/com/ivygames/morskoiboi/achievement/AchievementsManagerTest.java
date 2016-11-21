package com.ivygames.morskoiboi.achievement;

import android.support.annotation.NonNull;

import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.battleship.ship.Ship;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static com.ivygames.morskoiboi.achievement.AchievementsManager.AIRCRAFTSMAN;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.BRAVERY_AND_COURAGE;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.CRUISER_COMMANDER;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.DESTROYER;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.EXTRA_BRAVERY_AND_COURAGE;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.FLYING_DUTCHMAN;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.LIFE_SAVING;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.MILITARY_ACHIEVEMENTS;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.NAVAL_MERIT;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.ORDER_OF_HONOUR;
import static com.ivygames.morskoiboi.achievement.AchievementsManager.STEALTH;
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
    public void IfBraveryAndCourageIsNotUnlocked__49xShellsDoNotUnlockIt() {
        setAchievementUnlocked(BRAVERY_AND_COURAGE, false);

        am.processShellsLeft(49);

        verifyNotUnlocked(BRAVERY_AND_COURAGE);
        verifyNotRevealed(EXTRA_BRAVERY_AND_COURAGE);
    }

    @Test
    public void IfBraveryAndCourageIsNotUnlocked__50ShellsUnlockItAndRevealExtraBraveryAndCourage() {
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

    @Test
    public void IfFlyingDutchmanNotUnlocked__80xSecondsUnlockIt() {
        setAchievementUnlocked(FLYING_DUTCHMAN, false);

        am.processTimeSpent(80000);

        verifyUnlocked(FLYING_DUTCHMAN);
    }

    @Test
    public void IfFlyingDutchmanNotUnlocked__81xSecondsDoNotUnlockIt() {
        setAchievementUnlocked(FLYING_DUTCHMAN, false);

        am.processTimeSpent(81000);

        verifyNotUnlocked(FLYING_DUTCHMAN);
    }

    @Test
    public void IfFlyingDutchmanIsUnlocked__80xSecondsDoNotUnlockIt() {
        setAchievementUnlocked(FLYING_DUTCHMAN, true);

        am.processTimeSpent(80000);

        verifyNotUnlocked(FLYING_DUTCHMAN);
    }

    @Test
    public void IfStealthIsNotUnlocked__2xAliveShipsDoNotUnlock() {
        setAchievementUnlocked(STEALTH, false);
        Collection<Ship> ships = getAliveShips(2);

        am.processShipsLeft(ships);

        verifyNotUnlocked(STEALTH);
    }

    @Test
    public void IfStealthIsNotUnlocked__3xAliveShipsUnlocksItAndRevealLifeSaving() {
        setAchievementUnlocked(STEALTH, false);
        Collection<Ship> ships = getAliveShips(3);

        am.processShipsLeft(ships);

        verifyUnlocked(STEALTH);
        verifyRevealed(LIFE_SAVING);
    }

    @Test
    public void IfStealthIsNotUnlocked__4xAliveShipsUnlocksItAndRevealLifeSaving() {
        setAchievementUnlocked(STEALTH, false);
        Collection<Ship> ships = getAliveShips(4);

        am.processShipsLeft(ships);

        verifyUnlocked(STEALTH);
        verifyRevealed(LIFE_SAVING);
    }

    @Test
    public void IfStealthIsUnlocked__3xAliveShipsNotRevealLifeSaving() {
        setAchievementUnlocked(STEALTH, true);
        Collection<Ship> ships = getAliveShips(3);

        am.processShipsLeft(ships);

        verifyNotUnlocked(LIFE_SAVING);
    }

    @Test
    public void IfStealthIsUnlocked__4xAliveShipsRevealLifeSaving() {
        setAchievementUnlocked(STEALTH, true);
        Collection<Ship> ships = getAliveShips(4);

        am.processShipsLeft(ships);

        verifyUnlocked(LIFE_SAVING);
    }

    @Test
    public void If4xShipLeftAlive__AircraftsmanUnlocked() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(4));

        am.processShipsLeft(ships);

        verifyUnlocked(AIRCRAFTSMAN);
    }

    @Test
    public void If4xShipNotAlive__AircraftsmanNotUnlocked() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(deadShip(4));

        am.processShipsLeft(ships);

        verifyNotUnlocked(AIRCRAFTSMAN);
    }

    @Test
    public void If2x3xShipsLeftAlive__CruiserCommanderUnlocked() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(3));
        ships.add(new Ship(3));

        am.processShipsLeft(ships);

        verifyUnlocked(CRUISER_COMMANDER);
    }

    @Test
    public void If1x3xShipLeftAlive__CruiserCommanderNotUnlocked() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(3));
        ships.add(deadShip(3));

        am.processShipsLeft(ships);

        verifyNotUnlocked(CRUISER_COMMANDER);
    }

    @Test
    public void If3x2xShipsLeftAlive__DestroyerUnlocked() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(2));
        ships.add(new Ship(2));
        ships.add(new Ship(2));

        am.processShipsLeft(ships);

        verifyUnlocked(DESTROYER);
    }

    @Test
    public void If2x2xShipLeftAlive__DestroyerNotUnlocked() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(2));
        ships.add(new Ship(2));
        ships.add(deadShip(2));

        am.processShipsLeft(ships);

        verifyNotUnlocked(DESTROYER);
    }

    @NonNull
    private Ship deadShip(int size) {
        Ship ship = new Ship(size);
        for (int i = 0; i < size; i++) {
            ship.shoot();
        }
        return ship;
    }

    @NonNull
    private Collection<Ship> getAliveShips(int count) {
        Collection<Ship> ships = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ships.add(new Ship(1));
        }
        return ships;
    }

    private void verifyNotRevealed(String achievementId) {
        verify(apiClient, never()).revealAchievement(achievementId);
    }

    private void verifyRevealed(String achievement) {
        verify(apiClient, times(1)).revealAchievement(achievement);
    }

    private void setMilitaryAchievementsUnlocked(boolean value) {
        setAchievementUnlocked(MILITARY_ACHIEVEMENTS, value);
    }

    private void setAchievementUnlocked(String achievement, boolean unlocked) {
        when(settings.isAchievementUnlocked(achievement)).thenReturn(unlocked);
    }

    private void verifyUnlocked(String braveryAndCourage) {
        verify(settings, times(1)).unlockAchievement(braveryAndCourage);
        verify(apiClient, times(1)).unlockAchievement(braveryAndCourage);
    }

    private void verifyNotUnlocked(String achievement) {
        verify(settings, never()).unlockAchievement(achievement);
        verify(apiClient, never()).unlockAchievement(achievement);
    }
}
