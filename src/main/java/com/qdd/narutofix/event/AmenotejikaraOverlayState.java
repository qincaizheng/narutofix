package com.qdd.narutofix.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client-only state holder for the short Amenotejikara visual overlay.
 */
@SideOnly(Side.CLIENT)
public final class AmenotejikaraOverlayState {
    private static long activeUntil;

    private AmenotejikaraOverlayState() {
    }

    public static void activate(int durationTicks) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.world == null) {
            activeUntil = 0L;
            return;
        }
        activeUntil = minecraft.world.getTotalWorldTime() + Math.max(1, durationTicks);
    }

    public static void deactivate() {
        activeUntil = 0L;
    }

    public static boolean isActive() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.world == null || activeUntil <= 0L) {
            return false;
        }
        if (minecraft.world.getTotalWorldTime() >= activeUntil) {
            activeUntil = 0L;
            return false;
        }
        return true;
    }
}
