package com.qdd.narutofix.cap.soul;

import com.qdd.narutofix.Configs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SoulEnergyCapabilityHandler {
    @CapabilityInject(ISoulEnergyData.class)
    public static Capability<ISoulEnergyData> SOUL_ENERGY_CAP = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(ISoulEnergyData.class, new SoulEnergyDataStorage(), SoulEnergyData::new);
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(SoulEnergyDataProvider.NAME, new SoulEnergyDataProvider());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP)) {
            return;
        }

        ISoulEnergyData original = SoulEnergyDataProvider.get(event.getOriginal());
        ISoulEnergyData clone = SoulEnergyDataProvider.get(event.getEntityPlayer());
        if (original != null && clone != null) {
            clone.setMax(original.getMax());
            if (event.isWasDeath()) {
                clone.setCurrent(original.getCurrent() * (1.0 - Configs.soul.soulLossPercentOnDeath));
            } else {
                clone.setCurrent(original.getCurrent());
            }
        }
    }
}
