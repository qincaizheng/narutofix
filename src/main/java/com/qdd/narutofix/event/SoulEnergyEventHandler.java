package com.qdd.narutofix.event;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import com.qdd.narutofix.network.PacketSyncSoulEnergy;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class SoulEnergyEventHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().world.isRemote) {
            return;
        }

        EntityLivingBase dead = event.getEntityLiving();
        EntityLivingBase killer = event.getSource().getTrueSource() instanceof EntityLivingBase
            ? (EntityLivingBase) event.getSource().getTrueSource() : null;

        // Case 1: Player kills a mob/entity - gain soul current and max
        if (killer instanceof EntityPlayerMP && !(dead instanceof EntityPlayer)) {
            EntityPlayerMP player = (EntityPlayerMP) killer;
            ISoulEnergyData data = SoulEnergyDataProvider.get(player);
            if (data != null) {
                data.addMax(Configs.soul.soulMaxGainOnKill);
                data.addCurrent(Configs.soul.soulGainOnKill);
                PacketSyncSoulEnergy.sync(player);
            }
        }

        // Case 2: Player dies - gain max, lose 10% current
        if (dead instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) dead;
            ISoulEnergyData data = SoulEnergyDataProvider.get(player);
            if (data != null) {
                data.addMax(Configs.soul.soulMaxGainOnDeath);
                data.setCurrent(data.getCurrent() * (1.0 - Configs.soul.soulLossPercentOnDeath));
                PacketSyncSoulEnergy.sync(player);
            }
        }
    }
}
