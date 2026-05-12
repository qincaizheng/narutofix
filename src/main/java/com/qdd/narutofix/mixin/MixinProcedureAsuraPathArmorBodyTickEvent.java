package com.qdd.narutofix.mixin;

import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.narutomod.procedure.ProcedureAsuraPathArmorBodyTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureAsuraPathArmorBodyTickEvent.class)
public abstract class MixinProcedureAsuraPathArmorBodyTickEvent {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$redirectForSixTomoe(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        ItemStack effectiveEye = DojutsuEyeHelper.getEffectiveEye(player);
        if (effectiveEye.isEmpty() || effectiveEye.getItem() != ModItems.SIX_TOMOE_RINNEGAN) {
            return;
        }

        // Six-tomoe counts — apply the same potion effects as the original procedure
        ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
        World world = (World) dependencies.get("world");
        double ticks_used = (itemstack.hasTagCompound() ? itemstack.getTagCompound().getDouble("ticks_used") : -1) + 1;
        itemstack.getTagCompound().setDouble("ticks_used", ticks_used);
        if (!world.isRemote && (ticks_used % 40) == 1) {
            if (entity instanceof EntityLivingBase) {
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 41, 24, false, false));
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SPEED, 41, 16, false, false));
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HASTE, 41, 5, false, false));
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 41, 5, false, false));
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SATURATION, 41, 0, false, false));
            }
        }
        ci.cancel();
    }
}
