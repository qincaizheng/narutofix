package com.qdd.narutofix.mixin;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.entity.EntitySusanooSkeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySusanooSkeleton.EntityCustom.class)
public abstract class mixinSkeleton extends EntitySusanooBase {

    public mixinSkeleton(World world) {
        super(world);
        this.setSize(2.4F, 2.4F);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/player/EntityPlayer;Z)V",at= @At(value = "TAIL"),remap = false)
    public void mixinEntityCustom(EntityPlayer player, boolean full, CallbackInfo ci){
        if(full){
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("susanoo.maxhealth", (double)1F, 2));
        }
    }
}
