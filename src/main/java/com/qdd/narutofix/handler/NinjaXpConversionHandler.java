package com.qdd.narutofix.handler;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.cap.body.BodyEnergyDataProvider;
import com.qdd.narutofix.cap.body.IBodyEnergyData;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import com.qdd.narutofix.network.PacketSyncBodyEnergy;
import com.qdd.narutofix.network.PacketSyncSoulEnergy;
import com.qdd.narutofix.util.NinjaXpHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NinjaXpConversionHandler {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote || !(event.player instanceof EntityPlayerMP)) {
            return;
        }
        int interval = Math.max(1, Configs.xpConversion.ninjaXpConversionIntervalTicks);
        if (event.player.ticksExisted % interval != 0) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        ISoulEnergyData soul = SoulEnergyDataProvider.get(player);
        IBodyEnergyData body = BodyEnergyDataProvider.get(player);
        if (soul == null || body == null) {
            return;
        }

        double source = Math.min(soul.getCurrent(), body.getCurrent());
        double converted = source * Configs.xpConversion.ninjaXpConversionRate;
        if (converted <= 0.0D) {
            return;
        }

        soul.addCurrent(-converted);
        body.addCurrent(-converted);
        NinjaXpHelper.add(player, converted, false);
        PacketSyncSoulEnergy.sync(player);
        PacketSyncBodyEnergy.sync(player);
    }
}
