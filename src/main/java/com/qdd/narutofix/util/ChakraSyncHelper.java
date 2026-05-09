package com.qdd.narutofix.util;

import com.qdd.narutofix.network.PacketSyncChakra;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.narutomod.Chakra;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 能量变化后同步查克拉的辅助类。<br>
 * 使用脏标记 + server tick 末尾统一发送模式，保证一个 tick 内至多一次发包。
 * <p>
 * 调用方仍调用 {@link #refresh(EntityPlayerMP)}，感知不到合并行为；
 * 关键路径（登录、切维度等）可用 {@link #flushNow(EntityPlayerMP)} 立即发送。
 * </p>
 */
public final class ChakraSyncHelper {
    private static final Set<UUID> DIRTY_PLAYERS = new HashSet<>();

    private ChakraSyncHelper() {}

    /**
     * 标记该玩家查克拉需要同步。实际发送推迟到 {@link #flushDirtyPlayers()}。<br>
     * 调用方不感知合并行为，与之前语义相同，只是不再立即发包。
     *
     * @param player 需要同步查克拉的服务端玩家
     */
    public static void refresh(EntityPlayerMP player) {
        if (player == null || player.world.isRemote) {
            return;
        }
        DIRTY_PLAYERS.add(player.getUniqueID());
    }

    /**
     * 立即同步该玩家查克拉，不等待 tick 末 flush。<br>
     * 用于加入服务器、切维度等需要立即同步的关键路径。
     *
     * @param player 需要立即同步的玩家
     */
    public static void flushNow(EntityPlayerMP player) {
        if (player == null || player.world.isRemote) {
            return;
        }
        DIRTY_PLAYERS.remove(player.getUniqueID());
        doSync(player);
    }

    /**
     * 将所有脏标记玩家一次性同步并清空脏集合。<br>
     * 由 {@link com.qdd.narutofix.handler.ChakraSyncTickHandler}
     * 在 {@link net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent Phase.END} 调用。
     */
    public static void flushDirtyPlayers() {
        if (DIRTY_PLAYERS.isEmpty()) {
            return;
        }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) {
            return;
        }
        for (UUID uuid : DIRTY_PLAYERS) {
            EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(uuid);
            if (player != null) {
                doSync(player);
            }
        }
        DIRTY_PLAYERS.clear();
    }

    /**
     * 玩家下线时移除脏标记，避免内存泄漏。
     *
     * @param uuid 下线玩家的 UUID
     */
    public static void removePlayer(UUID uuid) {
        if (uuid != null) {
            DIRTY_PLAYERS.remove(uuid);
        }
    }

    private static void doSync(EntityPlayerMP player) {
        Chakra.Pathway pathway = Chakra.pathway(player);
        if (pathway == null) {
            return;
        }
        PacketSyncChakra.sync(player);
    }
}
