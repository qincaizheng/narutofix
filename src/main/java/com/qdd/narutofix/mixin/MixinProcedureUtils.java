package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemSharingan;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ProcedureUtils.class)
public abstract class MixinProcedureUtils {
    @Inject(method = "hasItemInInventory", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$hasItemInInventory(EntityPlayer player, Item item, CallbackInfoReturnable<Boolean> cir) {
        if (item instanceof ItemDojutsu.Base && DojutsuEyeHelper.hasEitherEye(player, item)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasAnyItemOfSubtype", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$hasAnyItemOfSubtype(EntityPlayer player, Class<? extends Item> itemType, CallbackInfoReturnable<Boolean> cir) {
        if (DojutsuEyeHelper.hasEitherEyeOfType(player, itemType)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getAllItemsOfSubType", at = @At("RETURN"), cancellable = true, remap = false)
    private static void narutofix$getAllItemsOfSubType(EntityPlayer player, Class<? extends Item> itemType, CallbackInfoReturnable<List<ItemStack>> cir) {
        ItemStack virtualEye = DojutsuEyeHelper.getVirtualEye(player);
        if (virtualEye.isEmpty() || !itemType.isAssignableFrom(virtualEye.getItem().getClass())) {
            return;
        }

        List<ItemStack> items = new ArrayList<ItemStack>(cir.getReturnValue());
        if (!items.contains(virtualEye)) {
            items.add(virtualEye);
            cir.setReturnValue(items);
        }
    }

    @Inject(method = "isWearingAnySharingan", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$isWearingAnySharingan(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (DojutsuEyeHelper.hasEitherEye(entity, ItemSharingan.helmet, ItemMangekyoSharingan.helmet,
                ItemMangekyoSharinganObito.helmet, ItemMangekyoSharinganEternal.helmet)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isWearingMangekyo", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$isWearingMangekyo(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (DojutsuEyeHelper.hasEitherEye(entity, ItemMangekyoSharingan.helmet,
                ItemMangekyoSharinganObito.helmet, ItemMangekyoSharinganEternal.helmet)) {
            cir.setReturnValue(true);
        }
    }
}