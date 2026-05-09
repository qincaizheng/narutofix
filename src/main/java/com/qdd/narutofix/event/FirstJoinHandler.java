package com.qdd.narutofix.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.narutomod.NarutomodModVariables;
import net.narutomod.procedure.ProcedureUtils;

public class FirstJoinHandler {
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player.world.isRemote) {
            return;
        }

        double battleXp = event.player.getEntityData().getDouble(NarutomodModVariables.BATTLEXP);
        if (battleXp > 0.0d) {
            return;
        }

        event.player.getEntityData().setDouble(NarutomodModVariables.BATTLEXP, 1.0d);

        ProcedureUtils.grantAdvancement((EntityPlayerMP) event.player, "narutomod:ninjaachievement", true);
    }
}
