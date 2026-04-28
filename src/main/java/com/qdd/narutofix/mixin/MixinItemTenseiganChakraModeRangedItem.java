package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemTenseiganChakraMode;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemTenseiganChakraMode.RangedItem.class)
public abstract class MixinItemTenseiganChakraModeRangedItem {
    @Redirect(
            method = "onItemRightClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"
            ),
            remap = false
    )
    private ItemStack narutofix$allowVirtualEyeOnRightClick(EntityPlayer player, EntityEquipmentSlot slot) {
        if (slot == EntityEquipmentSlot.HEAD) {
            return DojutsuEyeHelper.getMatchingEye(player, ItemTenseigan.helmet);
        }
        return player.getItemStackFromSlot(slot);
    }

    @Redirect(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/narutomod/procedure/ProcedureUtils;getMatchingItemStack(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)Lnet/minecraft/item/ItemStack;"
            ),
            remap = false
    )
    private ItemStack narutofix$findVirtualEye(EntityPlayer player, net.minecraft.item.Item item) {
        if (item == ItemTenseigan.helmet) {
            ItemStack eyeStack = DojutsuEyeHelper.getMatchingEye(player, ItemTenseigan.helmet);
            return eyeStack.isEmpty() ? null : eyeStack;
        }
        return ProcedureUtils.getMatchingItemStack(player, item);
    }

    @Redirect(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"
            ),
            remap = false
    )
    private ItemStack narutofix$allowVirtualEyeOnUpdate(EntityPlayer player, EntityEquipmentSlot slot) {
        if (slot == EntityEquipmentSlot.HEAD) {
            return DojutsuEyeHelper.getMatchingEye(player, ItemTenseigan.helmet);
        }
        return player.getItemStackFromSlot(slot);
    }
}