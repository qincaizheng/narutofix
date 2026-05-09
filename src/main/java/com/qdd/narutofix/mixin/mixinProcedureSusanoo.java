package com.qdd.narutofix.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.narutomod.procedure.ProcedureSusanoo;
import com.qdd.narutofix.entity.susanoo.SusanooSummonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Redirects narutomod's Susanoo summon/upgrade to narutofix's new
 * low-coupling entity system.
 *
 * <p>This mixin cancels the original {@link ProcedureSusanoo#execute}
 * and {@link ProcedureSusanoo#upgrade}, routing them to
 * {@link SusanooSummonHandler} which spawns
 * our own entity classes instead of narutomod's.</p>
 */
@Mixin(ProcedureSusanoo.class)
public class mixinProcedureSusanoo {

    /**
     * Redirect summon / toggle action to our handler.
     */
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$redirectExecute(EntityPlayer player, CallbackInfo ci) {
        SusanooSummonHandler.summonSusanoo(player);
        ci.cancel();
    }

    /**
     * Redirect upgrade action to our handler.
     */
    @Inject(method = "upgrade", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$redirectUpgrade(EntityPlayer player, CallbackInfo ci) {
        // Only redirect if riding our new entity; fall through to original for old entities.
        if (player.getRidingEntity() instanceof com.qdd.narutofix.entity.susanoo.SusanooEntityBase) {
            SusanooSummonHandler.upgradeSusanoo(player);
            ci.cancel();
        }
    }
}
