package com.qdd.narutofix.mixin;

import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.narutomod.item.ItemBlackReceiver;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemBlackReceiver.RangedItem.class)
public abstract class MixinItemBlackReceiverRangedItem {
    @Redirect(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/narutomod/procedure/ProcedureUtils;hasItemInInventory(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)Z",
                    ordinal = 0
            ),
            remap = false
    )
    private boolean narutofix$allowVirtualRinnegan(EntityPlayer player, net.minecraft.item.Item item) {
        return hasRinneganAccess(player, item);
    }

    @Redirect(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/narutomod/procedure/ProcedureUtils;hasItemInInventory(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)Z",
                    ordinal = 1
            ),
            remap = false
    )
    private boolean narutofix$allowVirtualTenseigan(EntityPlayer player, net.minecraft.item.Item item) {
        return hasRinneganAccess(player, item);
    }

    private boolean hasRinneganAccess(EntityPlayer player, net.minecraft.item.Item item) {
        if (DojutsuEyeHelper.hasEitherEye(player, ModItems.SIX_TOMOE_RINNEGAN, ItemRinnegan.helmet, ItemTenseigan.helmet)) {
            return true;
        }
        return ProcedureUtils.hasItemInInventory(player, item);
    }
}