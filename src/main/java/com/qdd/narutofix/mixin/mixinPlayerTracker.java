package com.qdd.narutofix.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.narutomod.PlayerTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.narutomod.NarutomodModVariables.BATTLEXP;

@Mixin(PlayerTracker.class)
public abstract class mixinPlayerTracker {

    @Shadow
    public static double getBattleXp(EntityPlayer player) {
        return 0;
    }

    @Inject(method = "addBattleXp(Lnet/minecraft/entity/player/EntityPlayer;DZ)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setDouble(Ljava/lang/String;D)V",shift = At.Shift.AFTER),remap = false)
    private static void addBattleXp(EntityPlayer entity, double xp, boolean sendMessage, CallbackInfo ci) {
        entity.getEntityData().setDouble(BATTLEXP, getBattleXp(entity) + xp);
    }
}

