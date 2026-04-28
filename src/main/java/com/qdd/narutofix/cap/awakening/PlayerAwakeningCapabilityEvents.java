package com.qdd.narutofix.cap.awakening;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerAwakeningCapabilityEvents {
    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(PlayerAwakeningDataProvider.NAME, new PlayerAwakeningDataProvider());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP)) {
            return;
        }

        IPlayerAwakeningData original = PlayerAwakeningDataProvider.get(event.getOriginal());
        IPlayerAwakeningData clone = PlayerAwakeningDataProvider.get(event.getEntityPlayer());
        if (original != null && clone != null) {
            clone.copyFrom(original);
        }
    }
}