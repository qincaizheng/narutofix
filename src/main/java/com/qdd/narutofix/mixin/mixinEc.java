package com.qdd.narutofix.mixin;

import net.narutomod.entity.EntityBuddha1000;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBuddha1000.EC.class)
public class mixinEc {
    @Inject(method = "onUpdate",at = @At(value = "INVOKE", target = "Lnet/narutomod/entity/EntityBuddha1000$EC;setDead()V",ordinal =1), cancellable = true,remap = false)
    public void onUpdate(CallbackInfo ci){
        ci.cancel();
    }
}
