package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.entity.EntitySusanooClothed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntitySusanooClothed.EntityCustom.class)
public abstract class MixinEntitySusanooClothed {
    @Redirect(
            method = "collideWithEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"
            ),
            remap = false
    )
    private ItemStack narutofix$useMatchingEye(EntityLivingBase owner, EntityEquipmentSlot slot) {
        if (slot == EntityEquipmentSlot.HEAD) {
            return DojutsuEyeHelper.getCompatibleSusanooEye(owner);
        }
        return owner.getItemStackFromSlot(slot);
    }
}