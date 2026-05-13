package com.qdd.narutofix.event;

import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.entity.susanoo.SusanooEntityBase;
import com.qdd.narutofix.network.PacketNarutofixSusanooFire;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SusanooMouseFireHandler {

    private int rightClickCooldown;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.rightClickCooldown > 0) {
            --this.rightClickCooldown;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.player;
        if (player == null || minecraft.currentScreen != null || !minecraft.inGameHasFocus) {
            return;
        }
        if (!(player.getRidingEntity() instanceof SusanooEntityBase)) {
            return;
        }

        if (this.rightClickCooldown > 0 || !minecraft.gameSettings.keyBindUseItem.isKeyDown()) {
            return;
        }

        this.rightClickCooldown = 4;
        NarutoFix.PACKET_HANDLER.sendToServer(new PacketNarutofixSusanooFire());
        player.swingArm(EnumHand.MAIN_HAND);
    }
}
