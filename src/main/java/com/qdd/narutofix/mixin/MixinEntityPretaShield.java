package com.qdd.narutofix.mixin;

import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.entity.EntityPretaShield;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPretaShield.EntityCustom.class)
public abstract class MixinEntityPretaShield {
    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"), remap = false)
    private ItemStack narutofix$readEffectiveEye(EntityLivingBase summoner, EntityEquipmentSlot slot) {
        if (slot == EntityEquipmentSlot.HEAD && summoner instanceof net.minecraft.entity.player.EntityPlayer
                && DojutsuEyeHelper.hasVirtualEye((net.minecraft.entity.player.EntityPlayer) summoner, ModItems.SIX_TOMOE_RINNEGAN)) {
            return DojutsuEyeHelper.getCapabilityEye((net.minecraft.entity.player.EntityPlayer) summoner);
        }
        return summoner.getItemStackFromSlot(slot);
    }
}