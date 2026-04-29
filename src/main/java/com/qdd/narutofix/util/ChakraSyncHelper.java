package com.qdd.narutofix.util;

import com.qdd.narutofix.network.PacketSyncChakra;
import net.minecraft.entity.player.EntityPlayerMP;
import net.narutomod.Chakra;

/**
 * 能量变化后同步查克拉的辅助类。
 * 当灵魂能量、肉体能量或忍者经验变化时，
 * 确保 narutomod 的查克拉系统立即刷新，
 * 避免 HUD 数值滞后。
 */
public final class ChakraSyncHelper {
    private ChakraSyncHelper() {}

    /**
     * 刷新指定玩家的查克拉客户端状态。
     * 服务端调用，通过 narutomod 的同步机制将最新查克拉数值推送到客户端。
     *
     * @param player 需要同步查克拉的服务端玩家
     */
    public static void refresh(EntityPlayerMP player) {
        if (player == null || player.world.isRemote) {
            return;
        }
        Chakra.Pathway pathway = Chakra.pathway(player);
        if (pathway == null) {
            return;
        }
        PacketSyncChakra.sync(player);
    }
}
