package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.entity.EntitySusanooWinged;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntitySusanooWinged.EntityCustom.class)
public abstract class MixinEntitySusanooWinged {
    @Redirect(
            method = "<init>(Lnet/minecraft/entity/player/EntityPlayer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"
            ),
            remap = false
    )
    private ItemStack narutofix$useMatchingEye(net.minecraft.entity.player.EntityPlayer player, EntityEquipmentSlot slot) {
        if (slot == EntityEquipmentSlot.HEAD) {
            return DojutsuEyeHelper.getCompatibleSusanooEye(player);
        }
        return player.getItemStackFromSlot(slot);
    }
}