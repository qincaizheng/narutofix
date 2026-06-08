package com.qdd.narutofix.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SleepRecoveryRulesTest {

    @Test
    public void rejectsWakeBeforeDaytime() {
        assertFalse(SleepRecoveryRules.shouldApplyRecovery(true, false, 13000L, 13500L));
        assertFalse(SleepRecoveryRules.shouldApplyRecovery(false, true, 13000L, 24000L));
        assertFalse(SleepRecoveryRules.shouldApplyRecovery(true, true, 13000L, 12000L));
    }

    @Test
    public void acceptsNightSleepThatReachedDaytime() {
        assertTrue(SleepRecoveryRules.shouldApplyRecovery(true, true, 13000L, 23400L));
        assertTrue(SleepRecoveryRules.shouldApplyRecovery(true, true, 13000L, 24000L));
    }

    @Test
    public void detectsDaytimeFromWorldTimeWithoutSkylightState() {
        assertFalse(SleepRecoveryRules.isVanillaDaytime(13000L));
        assertFalse(SleepRecoveryRules.isVanillaDaytime(23458L));
        assertTrue(SleepRecoveryRules.isVanillaDaytime(23459L));
        assertTrue(SleepRecoveryRules.isVanillaDaytime(24000L));
        assertTrue(SleepRecoveryRules.isVanillaDaytime(36000L));
    }

    @Test
    public void capsEffectiveSleepTicks() {
        assertEquals(40, SleepRecoveryRules.effectiveSleepTicks(1000L, 1040L, 100));
        assertEquals(100, SleepRecoveryRules.effectiveSleepTicks(1000L, 9000L, 100));
        assertEquals(0, SleepRecoveryRules.effectiveSleepTicks(1000L, 1040L, 0));
        assertEquals(0, SleepRecoveryRules.effectiveSleepTicks(1040L, 1000L, 100));
    }

    @Test
    public void calculatesRecoveryByTickAndRoom() {
        assertEquals(30.0D, SleepRecoveryRules.calculateRecovery(100.0D, 50.0D, 0.003D, 100), 0.0001D);
        assertEquals(10.0D, SleepRecoveryRules.calculateRecovery(100.0D, 90.0D, 0.003D, 100), 0.0001D);
        assertEquals(0.0D, SleepRecoveryRules.calculateRecovery(100.0D, 100.0D, 0.003D, 100), 0.0001D);
    }
}
