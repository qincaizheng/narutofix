package com.qdd.narutofix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import com.qdd.narutofix.handler.TailsHandler;
import net.narutomod.entity.EntityBijuManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;


@Mixin(EntityBijuManager.class)
public abstract class mixinEntityBijuManager {
    @Shadow(remap = false)
    @Final
    private int tails;

    @Shadow(remap = false) @Nullable public abstract EntityPlayer getJinchurikiPlayer();

    @Inject(method = "setVesselEntity(Lnet/minecraft/entity/Entity;Z)V",at= @At(value = "HEAD"),remap = false)
    public void setVesselEntity(Entity entityIn, boolean dirty, CallbackInfo ci) {
        if(entityIn==null) {
            EntityPlayer player=getJinchurikiPlayer();
            if(player==null)return;
            switch (this.tails)
            {case 7 :
                TailsHandler.SevenTails(player, false);
                break;
            }
        }else{
            switch (this.tails)
            {case 7 :
                TailsHandler.SevenTails((EntityPlayer) entityIn, true);
                break;
            }
        }
    }
}
