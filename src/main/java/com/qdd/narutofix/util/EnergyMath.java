package com.qdd.narutofix.util;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import net.minecraft.entity.player.EntityPlayer;

public final class EnergyMath {
    private EnergyMath() {
    }

    public static double soulMultiplier(EntityPlayer player, double multiplierPerSoul) {
        ISoulEnergyData data = SoulEnergyDataProvider.get(player);
        if (data == null) {
            return 1.0D;
        }
        return Math.max(0.0D, 1.0D + data.getCurrent() * multiplierPerSoul);
    }

    public static double chakraGrowthMultiplier(EntityPlayer player) {
        return soulMultiplier(player, Configs.soul.chakraGrowthMultiplierPerSoul);
    }

    public static double jutsuXpMultiplier(EntityPlayer player) {
        return soulMultiplier(player, Configs.soul.jutsuXpMultiplierPerSoul);
    }

    public static double jutsuChargeMultiplier(EntityPlayer player) {
        return soulMultiplier(player, Configs.soul.jutsuChargeMultiplierPerSoul);
    }
}
