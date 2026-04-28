package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharinganObito;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemMangekyoSharinganObito.class)
public abstract class MixinItemMangekyoSharinganObito {
    private static final double INTANGIBLE_CHAKRA_USAGE = 1.0D;
    private static final double TELEPORT_CHAKRA_USAGE = 20.0D;
    private static final double INVALID_USAGE = Double.MAX_VALUE * 0.001D;

    @Inject(method = "getIntangibleChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getIntangibleUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        narutofix$overrideUsage(entity, cir, INTANGIBLE_CHAKRA_USAGE);
    }

    @Inject(method = "getTeleportChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getTeleportUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        narutofix$overrideUsage(entity, cir, TELEPORT_CHAKRA_USAGE);
    }

    @Unique
    private static void narutofix$overrideUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir, double baseUsage) {
        ItemStack stack = DojutsuEyeHelper.getMatchingEye(entity, ItemMangekyoSharinganObito.helmet, ItemMangekyoSharinganEternal.helmet);
        if (stack.isEmpty()) {
            cir.setReturnValue(INVALID_USAGE);
            return;
        }

        boolean isOwner = stack.getItem() instanceof ItemDojutsu.Base && ((ItemDojutsu.Base) stack.getItem()).isOwner(stack, entity);
        cir.setReturnValue(isOwner ? baseUsage : baseUsage * 3.0D);
    }
}