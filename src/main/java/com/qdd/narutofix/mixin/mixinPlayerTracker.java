package com.qdd.narutofix.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.narutomod.PlayerTracker;
import net.narutomod.procedure.ProcedureSync;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.narutomod.NarutomodModVariables.BATTLEXP;

@Mixin(PlayerTracker.class)
public abstract class mixinPlayerTracker {

    @Shadow(remap = false)
    public static double getBattleXp(EntityPlayer player) {
        return player.getEntityData().getDouble(BATTLEXP);
    }

    @Shadow(remap = false)
    private static void sendBattleXPToTracking(EntityPlayerMP player) {
        ProcedureSync.EntityNBTTag.sendToTracking(player, BATTLEXP, getBattleXp(player));
    }

    @Inject(method = "addBattleXp(Lnet/minecraft/entity/player/EntityPlayer;DZ)V",at = @At(value = "HEAD"),remap = false, cancellable = true)
    private static void addBattleXp(EntityPlayer entity, double xp, boolean sendMessage, CallbackInfo ci) {
        entity.getEntityData().setDouble(BATTLEXP, getBattleXp(entity) + xp);
        if (entity instanceof EntityPlayerMP) {
            sendBattleXPToTracking((EntityPlayerMP)entity);
            if (sendMessage) {
                entity.sendStatusMessage(new TextComponentString(I18n.translateToLocal("chattext.ninjaexperience") + String.format("%.1f", getBattleXp(entity))), true);
            }
        }
        ci.cancel();
    }
}

