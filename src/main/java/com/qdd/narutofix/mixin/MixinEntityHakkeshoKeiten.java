package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.entity.EntityHakkeshoKeiten;
import net.narutomod.item.ItemByakugan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityHakkeshoKeiten.EntityCustom.class)
public abstract class MixinEntityHakkeshoKeiten {
    @Redirect(
            method = "setDead",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"
            ),
            remap = false
    )
    private ItemStack narutofix$writeCooldownToMatchingEye(EntityLivingBase summoner, EntityEquipmentSlot slot) {
        if (slot == EntityEquipmentSlot.HEAD) {
            return DojutsuEyeHelper.getMatchingEye(summoner, ItemByakugan.helmet);
        }
        return summoner.getItemStackFromSlot(slot);
    }
}