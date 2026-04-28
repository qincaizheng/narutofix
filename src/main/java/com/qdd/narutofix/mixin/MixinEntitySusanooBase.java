package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.entity.EntitySusanooBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntitySusanooBase.class)
public abstract class MixinEntitySusanooBase {
    @Redirect(
            method = "<init>(Lnet/minecraft/entity/EntityLivingBase;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"
            ),
            remap = false
    )
    private ItemStack narutofix$useMatchingEye(EntityLivingBase player, EntityEquipmentSlot slot) {
        if (slot == EntityEquipmentSlot.HEAD) {
            return DojutsuEyeHelper.getCompatibleSusanooEye(player);
        }
        return player.getItemStackFromSlot(slot);
    }
}