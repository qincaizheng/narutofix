package com.qdd.narutofix.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.item.ItemJutsu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemJutsu.Base.class)
public class ItemJutsuBase {
    @ModifyArg(method = "executeJutsu",at= @At(value = "INVOKE", target = "Lnet/narutomod/item/ItemJutsu$IJutsuCallback;createJutsu(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;F)Z"),index = 1,remap = false)
    protected EntityLivingBase executeJutsu(EntityLivingBase entityLivingBase){
        if(entityLivingBase.getRidingEntity() instanceof EntitySusanooBase){
            return (EntityLivingBase) entityLivingBase.getRidingEntity();
        }
        return entityLivingBase;
    }
}
