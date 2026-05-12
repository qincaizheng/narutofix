package com.qdd.narutofix.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.entity.susanoo.SusanooEntityBase;
import com.qdd.narutofix.handler.JutsuHandler;
import com.qdd.narutofix.network.PacketNarutofixSusanooUpgrade;
import net.narutomod.keybind.KeyBindingPowerIncrease;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts the PowerIncrease key (UP arrow) at the key binding level
 * to handle narutofix Susanoo upgrade, bypassing narutomod's procedure chain.
 */
@Mixin(KeyBindingPowerIncrease.class)
public class mixinKeyBindingPowerIncrease {

    @Shadow(remap = false)
    private KeyBinding keys;

    @Shadow(remap = false)
    private boolean wasKeyDown;

    @Inject(method = "processKeyBind", at = @At("HEAD"), remap = false, cancellable = true)
    private void narutofix$upgradeOurSusanoo(CallbackInfo ci) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;
        if (!(player.getRidingEntity() instanceof SusanooEntityBase)) return;

        // Key released (was down, now up) = upgrade trigger
        boolean isKeyDown = this.keys.isKeyDown();
        if (!isKeyDown && this.wasKeyDown) {
            NarutoFix.PACKET_HANDLER.sendToServer(new PacketNarutofixSusanooUpgrade());
        }

        // Cancel original processKeyBind to prevent narutomod triggering other actions
        ci.cancel();
    }

    @Inject(method = "onKeyInput",at= @At(value = "INVOKE", target = "Lnet/narutomod/keybind/KeyBindingPowerIncrease;processKeyBind()V"),remap = false)
    private void processKeyBind( CallbackInfo ci) {
        if (keys.isPressed()) {
            JutsuHandler.onKeyEvent();
        }
    }
}
