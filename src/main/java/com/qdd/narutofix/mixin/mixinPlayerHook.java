package com.qdd.narutofix.mixin;

import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemSharingan.PlayerHook.class)
public class mixinPlayerHook{
    @Inject(method ="onMouseEvent" ,at = @At("HEAD"),cancellable = true,remap = false)
    public void onMouseEvent(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onAttacked",at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;setPositionAndUpdate(DDD)V"),@At(value = "INVOKE", target = "Lnet/narutomod/item/ItemSharingan$PlayerHook;lockOnTarget(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/EntityLivingBase;I)V")}, remap = false, cancellable = true)
    public void mixinonAttackEvent(CallbackInfo ci) {
        ci.cancel();
    }


}
