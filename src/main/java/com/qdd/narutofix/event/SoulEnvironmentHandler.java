package com.qdd.narutofix.event;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import com.qdd.narutofix.network.PacketSyncSoulEnergy;
import com.qdd.narutofix.util.ChakraSyncHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod.EventBusSubscriber(modid = NarutoFix.MODID)
public class SoulEnvironmentHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (event.player.world.isRemote) {
            return;
        }
        if (!(event.player instanceof EntityPlayerMP)) {
            return;
        }

        Configs.SoulEnvironmentConfig cfg = Configs.soulEnvironment;
        if (!cfg.enabled) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.player;

        if (player.ticksExisted % cfg.checkIntervalTicks != 0) {
            return;
        }

        ISoulEnergyData soul = SoulEnergyDataProvider.get(player);
        if (soul == null || soul.getCurrent() >= soul.getMax()) {
            return;
        }

        Block blockBelow = player.world.getBlockState(player.getPosition().down()).getBlock();
        if (!isRecoveryBlock(blockBelow, cfg.recoveryBlocks)) {
            return;
        }

        double restore = soul.getMax() * cfg.recoveryPercentPerSecond
                       * ((double) cfg.checkIntervalTicks / 20.0);
        double clamped = Math.min(restore, soul.getMax() - soul.getCurrent());
        if (clamped <= 0.0) {
            return;
        }

        soul.addCurrent(clamped);
        PacketSyncSoulEnergy.sync(player);
        ChakraSyncHelper.refresh(player);
    }

    private static boolean isRecoveryBlock(Block block, String[] blockNames) {
        for (String name : blockNames) {
            Block target = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
            if (target != null && target == block) {
                return true;
            }
        }
        return false;
    }
}
