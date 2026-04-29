package com.qdd.narutofix.event;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.cap.body.IBodyEnergyData;
import com.qdd.narutofix.cap.body.BodyEnergyDataProvider;
import com.qdd.narutofix.network.PacketSyncBodyEnergy;
import com.qdd.narutofix.util.ChakraSyncHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class BodyEnergyEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().world.isRemote) {
            return;
        }

        EntityLivingBase victim = event.getEntityLiving();
        EntityLivingBase attacker = event.getSource().getTrueSource() instanceof EntityLivingBase
            ? (EntityLivingBase) event.getSource().getTrueSource() : null;

        if (attacker instanceof EntityPlayerMP && attacker != victim) {
            EntityPlayerMP player = (EntityPlayerMP) attacker;
            IBodyEnergyData data = BodyEnergyDataProvider.get(player);
            if (data != null) {
                data.addMax(Configs.body.bodyMaxGainOnHit);
                data.addCurrent(-Configs.body.bodyCostOnHit);
                PacketSyncBodyEnergy.sync(player);
                ChakraSyncHelper.refresh(player);
            }
        }

        if (victim instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) victim;
            IBodyEnergyData data = BodyEnergyDataProvider.get(player);
            if (data != null) {
                data.addMax(Configs.body.bodyMaxGainOnHurt);
                data.addCurrent(-Configs.body.bodyCostOnHurt);
                PacketSyncBodyEnergy.sync(player);
                ChakraSyncHelper.refresh(player);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote) {
            return;
        }

        EntityPlayer player = event.getPlayer();
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            IBodyEnergyData data = BodyEnergyDataProvider.get(playerMP);
            if (data != null) {
                data.addMax(Configs.body.bodyMaxGainOnMine);
                data.addCurrent(-Configs.body.bodyCostOnMine);
                PacketSyncBodyEnergy.sync(playerMP);
                ChakraSyncHelper.refresh(playerMP);
            }
        }
    }
}
