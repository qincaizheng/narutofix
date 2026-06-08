package com.qdd.narutofix.util;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.cap.body.BodyEnergyDataProvider;
import com.qdd.narutofix.cap.body.IBodyEnergyData;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import com.qdd.narutofix.network.PacketSyncBodyEnergy;
import com.qdd.narutofix.network.PacketSyncSoulEnergy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

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
        return 100.0D * soulLogMultiplier(player) * Configs.soul.chakraGrowthMultiplierPerSoul;
    }

    public static double jutsuXpMultiplier(EntityPlayer player) {
        return soulJutsuXpMultiplier(player);
    }

    public static double soulJutsuXpMultiplier(EntityPlayer player) {
        ISoulEnergyData data = SoulEnergyDataProvider.get(player);
        if (data == null) {
            return 1.0D;
        }
        double current = Math.max(1.0D, data.getCurrent());
        double logResult = Math.log(current / 100.0D) / Math.log(Configs.soul.jutsuXpLogBase);
        return Math.max(1.0D, Math.min(Configs.soul.jutsuXpMaxMultiplier, logResult));
    }

    public static double jutsuChargeMultiplier(EntityPlayer player) {
        return 100.0D * soulLogMultiplier(player) * Configs.soul.jutsuChargeMultiplierPerSoul;
    }

    public static double soulLogMultiplier(EntityPlayer player) {
        ISoulEnergyData data = SoulEnergyDataProvider.get(player);
        if (data == null) {
            return 0.0D;
        }
        double current = Math.max(1.0D, data.getCurrent());
        double logResult = Math.log(current / 100.0D) / Math.log(Configs.soul.jutsuXpLogBase);
        return Math.max(0.0D, Math.min(Configs.soul.jutsuXpMaxMultiplier, logResult));
    }

    public static double bodyMultiplier(EntityPlayer player) {
        IBodyEnergyData data = BodyEnergyDataProvider.get(player);
        if (data == null) {
            return 1.0D;
        }
        double max = Math.max(100.0D, data.getMax());
        double logResult = Math.log(max / 100.0D) / Math.log(Configs.body.bodyLogBase);
        return Math.max(0.0D, Math.min(Configs.body.bodyLogMaxMultiplier, logResult));
    }

    /**
     * 按比例增加玩家的灵魂能量和肉体能量的当前值和上限，并同步到客户端。
     * 这是"增加查克拉"的实际执行方法——查克拉 = 灵魂能量（阴）+ 肉体能量（阳）。
     * 调用方负责确保仅在服务端调用。
     *
     * @param player     玩家（必须是 EntityPlayerMP 才能同步）
     * @param soulAmount 灵魂能量增加量（当前值和上限同时增加）
     * @param bodyAmount 肉体能量增加量（当前值和上限同时增加）
     */
    public static void addBodyAndSoul(EntityPlayerMP player, double soulAmount, double bodyAmount) {
        ISoulEnergyData soulData = SoulEnergyDataProvider.get(player);
        IBodyEnergyData bodyData = BodyEnergyDataProvider.get(player);
        if (soulData != null) {
            soulData.addCurrent(soulAmount);
            soulData.addMax(soulAmount);
        }
        if (bodyData != null) {
            bodyData.addCurrent(bodyAmount);
            bodyData.addMax(bodyAmount);
        }
        PacketSyncSoulEnergy.sync(player);
        PacketSyncBodyEnergy.sync(player);
        ChakraSyncHelper.refresh(player);
    }
}
