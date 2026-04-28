package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.narutomod.item.ItemDojutsu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemDojutsu.class)
public abstract class MixinItemDojutsu {
    @Inject(method = "wearingAnyDojutsu", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$wearingAnyDojutsu(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (DojutsuEyeHelper.hasEitherEyeOfType(entity, ItemDojutsu.Base.class)) {
            cir.setReturnValue(true);
        }
    }
}