package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemAsuraCanon;
import net.narutomod.item.ItemAsuraPathArmor;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.procedure.ProcedureRinneganHelmetTickEvent;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureRinneganHelmetTickEvent.class)
public abstract class MixinProcedureRinneganHelmetTickEvent {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$handleVirtualEye(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;

        // Only intercept if head slot is empty or non-do-jutsu
        ItemStack headSlot = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (headSlot.getItem() instanceof ItemDojutsu.Base) {
            return;
        }

        ItemStack virtualEye = DojutsuEyeHelper.getVirtualEye(player);
        if (virtualEye.isEmpty()) {
            return;
        }

        double which_path = virtualEye.hasTagCompound()
                ? virtualEye.getTagCompound().getDouble("which_path") : -1;

        if (which_path == 1) {
            if (player.inventory.armorInventory.get(2).getItem() != ItemAsuraPathArmor.body) {
                ProcedureUtils.swapItemToSlot(player, EntityEquipmentSlot.CHEST, new ItemStack(ItemAsuraPathArmor.body));
                ProcedureUtils.swapItemToSlot(player, EntityEquipmentSlot.OFFHAND, new ItemStack(ItemAsuraCanon.block));
            }
        } else {
            player.inventory.clearMatchingItems(ItemAsuraPathArmor.body, -1, -1, null);
            player.inventory.clearMatchingItems(ItemAsuraCanon.block, -1, -1, null);
        }

        entity.fallDistance = 0.0F;
    }
}
