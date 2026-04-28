package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.narutomod.item.ItemTenseigan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemTenseigan.class)
public abstract class MixinItemTenseigan {
    @Inject(method = "isWearing", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$isWearing(EntityLivingBase player, CallbackInfoReturnable<Boolean> cir) {
        if (DojutsuEyeHelper.hasEitherEye(player, ItemTenseigan.helmet)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isWearingFullArmor", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$isWearingFullArmor(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (DojutsuEyeHelper.hasEitherEye(entity, ItemTenseigan.helmet)
                && entity.getItemStackFromSlot(net.minecraft.inventory.EntityEquipmentSlot.CHEST).getItem() == ItemTenseigan.body
                && entity.getItemStackFromSlot(net.minecraft.inventory.EntityEquipmentSlot.LEGS).getItem() == ItemTenseigan.legs) {
            cir.setReturnValue(true);
        }
    }
}