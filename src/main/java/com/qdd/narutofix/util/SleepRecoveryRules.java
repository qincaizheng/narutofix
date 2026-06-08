package com.qdd.narutofix.util;

public final class SleepRecoveryRules {
    private SleepRecoveryRules() {
    }

    public static boolean shouldApplyRecovery(boolean startedAtNight, boolean currentDaytime,
                                              long startWorldTime, long currentWorldTime) {
        return startedAtNight && currentDaytime && currentWorldTime > startWorldTime;
    }

    public static boolean isVanillaDaytime(long worldTime) {
        long timeOfDay = worldTime % 24000L;
        if (timeOfDay < 0L) {
            timeOfDay += 24000L;
        }
        return timeOfDay < 12542L || timeOfDay > 23458L;
    }

    public static int effectiveSleepTicks(long startWorldTime, long currentWorldTime, int maxRecoveryTicks) {
        if (maxRecoveryTicks <= 0 || currentWorldTime <= startWorldTime) {
            return 0;
        }
        long elapsed = currentWorldTime - startWorldTime;
        return (int) Math.min(elapsed, (long) maxRecoveryTicks);
    }

    public static double calculateRecovery(double max, double current, double percentPerTick, int effectiveTicks) {
        if (max <= 0.0D || percentPerTick <= 0.0D || effectiveTicks <= 0) {
            return 0.0D;
        }
        double room = Math.max(0.0D, max - current);
        double rawRecovery = max * percentPerTick * effectiveTicks;
        return Math.min(rawRecovery, room);
    }
}
