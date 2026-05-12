package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.narutomod.Chakra;
import net.narutomod.entity.EntityKingOfHell;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.procedure.ProcedureNarakaPath;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(ProcedureNarakaPath.class)
public abstract class MixinProcedureNarakaPath {
    @Redirect(method = "executeProcedure", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"), remap = true)
    private static ItemStack narutofix$redirectHeadSlot(EntityLivingBase entity, net.minecraft.inventory.EntityEquipmentSlot slot) {
        if (slot != net.minecraft.inventory.EntityEquipmentSlot.HEAD || !(entity instanceof EntityPlayer)) {
            return entity.getItemStackFromSlot(slot);
        }
        EntityPlayer player = (EntityPlayer) entity;
        ItemStack headStack = entity.getItemStackFromSlot(slot);
        if (headStack.getItem() instanceof net.narutomod.item.ItemDojutsu.Base) {
            return headStack;
        }
        ItemStack virtual = DojutsuEyeHelper.getVirtualEye(player);
        if (!virtual.isEmpty() && (virtual.getItem() == ItemRinnegan.helmet || virtual.getItem() == net.narutomod.item.ItemTenseigan.helmet)) {
            return virtual;
        }
        return headStack;
    }
}
