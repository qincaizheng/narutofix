package com.qdd.narutofix.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.narutomod.PlayerTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTracker.class)
public abstract class mixinPlayerTracker {
    @Inject(method = "addBattleXp(Lnet/minecraft/entity/player/EntityPlayer;DZ)V",at = @At(value = "HEAD"),remap = false, cancellable = true)
    private static void addBattleXp(EntityPlayer entity, double xp, boolean sendMessage, CallbackInfo ci) {
        ci.cancel();
    }
}
