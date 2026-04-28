package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.procedure.ProcedureOnPlayerPostTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProcedureOnPlayerPostTick.class)
public abstract class MixinProcedureOnPlayerPostTick {
    @Redirect(
            method = "executeProcedure",
            at = @At(value = "INVOKE", target = "Lnet/narutomod/item/ItemDojutsu;hasAnyDojutsu(Lnet/minecraft/entity/player/EntityPlayer;)Z"),
            remap = false)
    private static boolean narutofix$hasAnyDojutsu(EntityPlayer player) {
        return ItemDojutsu.hasAnyDojutsu(player) || DojutsuEyeHelper.hasEitherEyeOfType(player, ItemDojutsu.Base.class);
    }

    @Redirect(
            method = "executeProcedure",
            at = @At(value = "INVOKE", target = "Lnet/narutomod/procedure/ProcedureUtils;isWearingMangekyo(Lnet/minecraft/entity/EntityLivingBase;)Z"),
            remap = false)
    private static boolean narutofix$isWearingMangekyo(EntityLivingBase entity) {
        return !DojutsuEyeHelper.getCompatibleSusanooEye(entity).isEmpty();
    }
}