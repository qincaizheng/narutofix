package com.qdd.narutofix.mixin;

import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRinnegan.class)
public abstract class MixinItemRinnegan {
    @Inject(method = "getShinratenseiChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getShinraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        overrideUsage(entity, cir, 10.0D);
    }

    @Inject(method = "getChibaukutenseiChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getChibakuUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        overrideUsage(entity, cir, 5000.0D);
    }

    @Inject(method = "getNarakaPathChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getNarakaUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        overrideUsage(entity, cir, 100.0D);
    }

    @Inject(method = "getPretaPathChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getPretaUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        overrideUsage(entity, cir, 10.0D);
    }

    @Inject(method = "getAnimalPathChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getAnimalUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        overrideUsage(entity, cir, 200.0D);
    }

    @Inject(method = "getOuterPathChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getOuterUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        overrideUsage(entity, cir, 2000.0D);
    }

    @Inject(method = "getTengaishinseiChakraUsage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$getMeteorUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        overrideUsage(entity, cir, 5000.0D);
    }

    @Inject(method = "wearingRinnegan", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$wearingRinnegan(EntityLivingBase player, CallbackInfoReturnable<Boolean> cir) {
        if (DojutsuEyeHelper.hasEitherEye(player, ModItems.SIX_TOMOE_RINNEGAN, ItemRinnegan.helmet)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "wearingRinnesharingan", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$wearingRinnesharingan(EntityLivingBase player, CallbackInfoReturnable<Boolean> cir) {
        if (!(player instanceof EntityPlayer)) {
            return;
        }

        ItemStack stack = DojutsuEyeHelper.getMatchingEye(player, ModItems.SIX_TOMOE_RINNEGAN, ItemRinnegan.helmet, ItemTenseigan.helmet);
        if (!stack.isEmpty() && ItemRinnegan.isRinnesharinganActivated(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasRinnesharingan", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$hasRinnesharingan(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = DojutsuEyeHelper.getMatchingEye(player, ModItems.SIX_TOMOE_RINNEGAN, ItemRinnegan.helmet, ItemTenseigan.helmet);
        if (!stack.isEmpty() && ItemRinnegan.isRinnesharinganActivated(stack)) {
            cir.setReturnValue(true);
        }
    }

    private static void overrideUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir, double baseUsage) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }

        ItemStack stack = DojutsuEyeHelper.getMatchingEye(entity, ModItems.SIX_TOMOE_RINNEGAN, ItemRinnegan.helmet, ItemTenseigan.helmet);
        if (stack.isEmpty()) {
            return;
        }

        double usage = stack.getItem() instanceof ItemDojutsu.Base && ((ItemDojutsu.Base) stack.getItem()).isOwner(stack, entity) ? baseUsage : baseUsage * 2.0D;
        cir.setReturnValue(usage);
    }
}