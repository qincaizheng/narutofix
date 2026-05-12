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
    private boolean wasKeyDown;
    private int chargeTicks;

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
            this.wasKeyDown = false;
            this.chargeTicks = 0;
            return;
        }
        if (!(player.getRidingEntity() instanceof SusanooEntityBase)) {
            this.wasKeyDown = false;
            this.chargeTicks = 0;
            return;
        }

        boolean isKeyDown = minecraft.gameSettings.keyBindUseItem.isKeyDown();

        // Key just pressed start charging
        if (isKeyDown && !this.wasKeyDown) {
            this.chargeTicks = 0;
        }

        if (isKeyDown) {
            ++this.chargeTicks;
        }

        // Key released: fire with accumulated charge ticks
        if (!isKeyDown && this.wasKeyDown) {
            if (this.rightClickCooldown <= 0) {
                this.rightClickCooldown = 4;
                NarutoFix.PACKET_HANDLER.sendToServer(new PacketNarutofixSusanooFire(this.chargeTicks));
                player.swingArm(EnumHand.MAIN_HAND);
            }
            this.chargeTicks = 0;
        }

        this.wasKeyDown = isKeyDown;
    }
}
