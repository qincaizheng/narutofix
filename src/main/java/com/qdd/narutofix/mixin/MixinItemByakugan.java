package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemByakugan;
import net.narutomod.item.ItemDojutsu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemByakugan.class)
public abstract class MixinItemByakugan {
    private static final double BYAKUGAN_CHAKRA_USAGE = 10.0D;
    private static final double ROKUJUYONSHO_CHAKRA_USAGE = 100.0D;
    private static final double KAITEN_CHAKRA_USAGE = 5.0D;
    private static final double KUSHO_CHAKRA_USAGE = 0.5D;
    private static final double INVALID_USAGE = Double.MAX_VALUE * 0.001D;

    @Inject(method = "getByakuganChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getByakuganUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        overrideUsage(entity, cir, BYAKUGAN_CHAKRA_USAGE, true);
    }

    @Inject(method = "getRokujuyonshoChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getRokujuyonshoUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        overrideUsage(entity, cir, ROKUJUYONSHO_CHAKRA_USAGE, false);
    }

    @Inject(method = "getKaitenChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getKaitenUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        overrideUsage(entity, cir, KAITEN_CHAKRA_USAGE, false);
    }

    @Inject(method = "getKushoChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getKushoUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        overrideUsage(entity, cir, KUSHO_CHAKRA_USAGE, false);
    }

    @Inject(method = "wearingAny", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$wearingAny(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (DojutsuEyeHelper.hasEitherEye(entity, ItemByakugan.helmet)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "wearingRinnesharingan", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$wearingRinnesharingan(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = DojutsuEyeHelper.getMatchingEye(player, ItemByakugan.helmet);
        if (!stack.isEmpty() && ItemByakugan.isRinnesharinganActivated(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasRinnesharingan", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$hasRinnesharingan(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = DojutsuEyeHelper.getMatchingEye(player, ItemByakugan.helmet);
        if (!stack.isEmpty() && ItemByakugan.isRinnesharinganActivated(stack)) {
            cir.setReturnValue(true);
        }
    }

    private static void overrideUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir, double baseUsage, boolean allowForeignOwnerPenalty) {
        ItemStack stack = DojutsuEyeHelper.getMatchingEye(entity, ItemByakugan.helmet);
        if (stack.isEmpty()) {
            cir.setReturnValue(INVALID_USAGE);
            return;
        }

        boolean isOwner = stack.getItem() instanceof ItemDojutsu.Base && ((ItemDojutsu.Base) stack.getItem()).isOwner(stack, entity);
        if (allowForeignOwnerPenalty) {
            cir.setReturnValue(isOwner ? baseUsage : baseUsage * 2.0D);
            return;
        }

        cir.setReturnValue(isOwner ? baseUsage : INVALID_USAGE);
    }
}