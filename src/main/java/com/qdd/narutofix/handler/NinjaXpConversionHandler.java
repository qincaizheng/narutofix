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
    private static final String LAST_CONVERT_SOURCE = "narutofixLastNinjaXpConvertSource";
    private static final String CONVERT_SOURCE_INITIALIZED = "narutofixNinjaXpConvertSourceInitialized";

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
        if (!player.getEntityData().getBoolean(CONVERT_SOURCE_INITIALIZED)) {
            player.getEntityData().setBoolean(CONVERT_SOURCE_INITIALIZED, true);
            player.getEntityData().setDouble(LAST_CONVERT_SOURCE, source);
            return;
        }

        double previousSource = player.getEntityData().getDouble(LAST_CONVERT_SOURCE);
        player.getEntityData().setDouble(LAST_CONVERT_SOURCE, source);
        double growth = source - previousSource;
        if (growth <= 0.0D) {
            return;
        }

        double converted = growth * Configs.xpConversion.ninjaXpConversionRate;
        if (converted <= 0.0D) {
            return;
        }

        NinjaXpHelper.add(player, converted, false);
        PacketSyncSoulEnergy.sync(player);
        PacketSyncBodyEnergy.sync(player);
    }
}
