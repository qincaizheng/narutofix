package com.qdd.narutofix.network;

import com.qdd.narutofix.util.ChakraSyncHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

@Mod.EventBusSubscriber
public class EnergySyncHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        syncAll(event.player instanceof EntityPlayerMP ? (EntityPlayerMP) event.player : null);
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            syncAll((EntityPlayerMP) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerChangedDimensionEvent event) {
        syncAll(event.player instanceof EntityPlayerMP ? (EntityPlayerMP) event.player : null);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerRespawnEvent event) {
        syncAll(event.player instanceof EntityPlayerMP ? (EntityPlayerMP) event.player : null);
    }

    private static void syncAll(EntityPlayerMP player) {
        if (player == null || player.world.isRemote) {
            return;
        }
        PacketSyncSoulEnergy.sync(player);
        PacketSyncBodyEnergy.sync(player);
        ChakraSyncHelper.flushNow(player);
    }
}
