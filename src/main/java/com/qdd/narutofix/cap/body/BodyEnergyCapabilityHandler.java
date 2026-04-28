package com.qdd.narutofix.cap.body;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BodyEnergyCapabilityHandler {
    @CapabilityInject(IBodyEnergyData.class)
    public static Capability<IBodyEnergyData> BODY_ENERGY_CAP = null;
    
    public static void register() {
        CapabilityManager.INSTANCE.register(IBodyEnergyData.class, new BodyEnergyDataStorage(), BodyEnergyData::new);
    }
    
    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(BodyEnergyDataProvider.NAME, new BodyEnergyDataProvider());
        }
    }
    
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP)) {
            return;
        }
        
        IBodyEnergyData original = BodyEnergyDataProvider.get(event.getOriginal());
        IBodyEnergyData clone = BodyEnergyDataProvider.get(event.getEntityPlayer());
        if (original != null && clone != null) {
            // Body energy fully preserved across death (no decay)
            clone.copyFrom(original);
        }
    }
}
