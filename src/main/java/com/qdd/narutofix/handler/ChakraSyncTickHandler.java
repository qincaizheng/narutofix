package com.qdd.narutofix.handler;

import com.qdd.narutofix.util.ChakraSyncHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * 负责 server tick 末尾统一 flush 脏标记查克拉同步，并清理玩家下线后的脏标记。
 */
public class ChakraSyncTickHandler {

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ChakraSyncHelper.flushDirtyPlayers();
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player != null) {
            ChakraSyncHelper.removePlayer(event.player.getUniqueID());
        }
    }
}
