package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemUchiha;
import net.narutomod.procedure.ProcedureUchihaBodyTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureUchihaBodyTickEvent.class)
public abstract class MixinProcedureUchihaBodyTickEvent {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$useVirtualEye(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        Object worldObject = dependencies.get("world");
        if (!(entity instanceof EntityPlayer) || !(worldObject instanceof World)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        ItemStack virtualEye = DojutsuEyeHelper.getVirtualEye(player);
        if (virtualEye.isEmpty() || !isUchihaEye(virtualEye) || isUchihaEye(DojutsuEyeHelper.getHeadEye(player))) {
            return;
        }

        if (player.inventory.armorInventory.get(1).getItem() == ItemUchiha.legs
                && player.inventory.armorInventory.get(0).getItem() == ItemUchiha.boots
                && !((World) worldObject).isRemote) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 1, 1, false, false));
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HASTE, 1, 2, false, false));
        }
        ci.cancel();
    }

    private static boolean isUchihaEye(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() == ItemSharingan.helmet
                || stack.getItem() == ItemMangekyoSharingan.helmet
                || stack.getItem() == ItemMangekyoSharinganObito.helmet);
    }
}