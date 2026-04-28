package com.qdd.narutofix.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.narutomod.procedure.ProcedureSync;

import static net.narutomod.NarutomodModVariables.BATTLEXP;

public final class NinjaXpHelper {
    private NinjaXpHelper() {
    }

    public static double get(EntityPlayer player) {
        return player.getEntityData().getDouble(BATTLEXP);
    }

    public static void set(EntityPlayer player, double value, boolean sendMessage) {
        player.getEntityData().setDouble(BATTLEXP, Math.min(Math.max(0.0D, value), 100000.0D));
        if (player instanceof EntityPlayerMP) {
            ProcedureSync.EntityNBTTag.sendToTracking((EntityPlayerMP) player, BATTLEXP, get(player));
            if (sendMessage) {
                player.sendStatusMessage(new TextComponentString(
                        I18n.translateToLocal("chattext.ninjaexperience") + String.format("%.1f", get(player))), true);
            }
        }
    }

    public static void add(EntityPlayer player, double value, boolean sendMessage) {
        if (value == 0.0D) {
            return;
        }
        set(player, get(player) + value, sendMessage);
    }
}
