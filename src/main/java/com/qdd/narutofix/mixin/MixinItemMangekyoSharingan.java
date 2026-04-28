package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemMangekyoSharingan.class)
public abstract class MixinItemMangekyoSharingan {
    private static final double AMATERASU_CHAKRA_USAGE = 100.0D;
    private static final double INVALID_USAGE = Double.MAX_VALUE * 0.001D;

    @Inject(method = "getAmaterasuChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getAmaterasuUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        ItemStack stack = DojutsuEyeHelper.getMatchingEye(entity, ItemMangekyoSharingan.helmet, ItemMangekyoSharinganEternal.helmet);
        if (stack.isEmpty()) {
            cir.setReturnValue(INVALID_USAGE);
            return;
        }

        boolean isOwner = stack.getItem() instanceof ItemDojutsu.Base && ((ItemDojutsu.Base) stack.getItem()).isOwner(stack, entity);
        cir.setReturnValue(isOwner ? AMATERASU_CHAKRA_USAGE : AMATERASU_CHAKRA_USAGE * 3.0D);
    }
}