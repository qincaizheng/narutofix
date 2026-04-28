package com.qdd.narutofix.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public final class AdvancementHelper {
    private AdvancementHelper() {
    }

    public static boolean grant(EntityPlayerMP player, String advancementId) {
        Advancement advancement = getAdvancement(player, advancementId);
        if (advancement == null) {
            return false;
        }

        AdvancementProgress progress = player.getAdvancements().getProgress(advancement);
        if (progress.isDone()) {
            return false;
        }

        for (String criterion : progress.getRemaningCriteria()) {
            player.getAdvancements().grantCriterion(advancement, criterion);
        }
        return true;
    }

    public static boolean revoke(EntityPlayerMP player, String advancementId) {
        Advancement advancement = getAdvancement(player, advancementId);
        if (advancement == null) {
            return false;
        }

        AdvancementProgress progress = player.getAdvancements().getProgress(advancement);
        if (!progress.isDone()) {
            return false;
        }

        for (String criterion : progress.getCompletedCriteria()) {
            player.getAdvancements().revokeCriterion(advancement, criterion);
        }
        return true;
    }

    public static boolean has(EntityPlayerMP player, String advancementId) {
        Advancement advancement = getAdvancement(player, advancementId);
        return advancement != null && player.getAdvancements().getProgress(advancement).isDone();
    }

    private static Advancement getAdvancement(EntityPlayerMP player, String advancementId) {
        MinecraftServer server = player.getServer();
        return server == null ? null : server.getAdvancementManager().getAdvancement(new ResourceLocation(advancementId));
    }
}