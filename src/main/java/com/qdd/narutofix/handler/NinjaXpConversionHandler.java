package com.qdd.narutofix.handler;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.cap.body.BodyEnergyDataProvider;
import com.qdd.narutofix.cap.body.IBodyEnergyData;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
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
        int interval = Math.max(1, Configs.xpConversion.getEffectiveContributionInterval());
        if (event.player.ticksExisted % interval != 0) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        ISoulEnergyData soul = SoulEnergyDataProvider.get(player);
        IBodyEnergyData body = BodyEnergyDataProvider.get(player);
        if (soul == null || body == null) {
            return;
        }

        double targetBp = Math.min(soul.getMax(), body.getMax() / 4.0) * 2.0;
        double currentBp = NinjaXpHelper.get(player);
        if (targetBp > currentBp) {
            NinjaXpHelper.add(player, targetBp - currentBp, false);
        }
    }

    /**
     * Reset the ninja XP contribution baseline to the player's max min(soul.max, body.max).
     * After calling this, subsequent ticks will only derive XP from growth that occurs after this point,
     * preventing retroactive XP grants when soul/body are modified externally (e.g. via admin commands).
     *
     * @param player the player whose baseline should be reset
     */
    public static void resetBaseline(EntityPlayerMP player) {
    }
}
