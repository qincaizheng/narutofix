package com.qdd.narutofix.mixin;

import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.narutomod.item.ItemAsuraCanon;
import net.narutomod.item.ItemAsuraPathArmor;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.procedure.ProcedureRinneganHelmetTickEvent;
import net.narutomod.procedure.ProcedureUtils;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureRinneganHelmetTickEvent.class)
public abstract class MixinProcedureRinneganHelmetTickEvent {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$handleSixTomoeAsuraPath(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        ItemStack effectiveEye = DojutsuEyeHelper.getEffectiveEye(player);
        if (effectiveEye.isEmpty() || effectiveEye.getItem() != ModItems.SIX_TOMOE_RINNEGAN) {
            return;
        }

        // Replicate the Asura Path auto-equip (which_path == 1) from the original procedure
        double which_path = effectiveEye.hasTagCompound()
                ? effectiveEye.getTagCompound().getDouble("which_path") : -1;
        if (which_path == 1) {
            if (player.inventory.armorInventory.get(2).getItem() != ItemAsuraPathArmor.body) {
                ProcedureUtils.swapItemToSlot(player, EntityEquipmentSlot.CHEST, new ItemStack(ItemAsuraPathArmor.body));
                ProcedureUtils.swapItemToSlot(player, EntityEquipmentSlot.OFFHAND, new ItemStack(ItemAsuraCanon.block));
            }
        } else {
            player.inventory.clearMatchingItems(ItemAsuraPathArmor.body, -1, -1, null);
            player.inventory.clearMatchingItems(ItemAsuraCanon.block, -1, -1, null);
        }

        // Fall damage immunity (same as original)
        entity.fallDistance = 0.0F;
    }
}
