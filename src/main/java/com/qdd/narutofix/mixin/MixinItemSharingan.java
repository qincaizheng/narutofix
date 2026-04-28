package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemSharingan.class)
public abstract class MixinItemSharingan {
    @Inject(method = "wearingAny", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$wearingAny(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (DojutsuEyeHelper.hasEitherEyeOfType(entity, ItemSharingan.Base.class)) {
            cir.setReturnValue(true);
        }
    }
}