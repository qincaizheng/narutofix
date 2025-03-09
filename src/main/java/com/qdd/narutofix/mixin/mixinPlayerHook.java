package com.qdd.narutofix.mixin;

import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemSharingan.PlayerHook.class)
public class mixinPlayerHook{
    @Inject(method ="onMouseEvent" ,at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setBoolean(Ljava/lang/String;Z)V"),cancellable = true)
    public void onMouseEvent(CallbackInfo ci) {
        ci.cancel();
        System.out.println("[Mixin] 鼠标事件已拦截");
    }

    @Inject(method = "onAttacked",at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;setPositionAndUpdate(DDD)V")},  cancellable = true)
    public void mixinonAttackEvent(CallbackInfo ci) {
        ci.cancel();
        System.out.println("[Mixin] 位移事件已拦截");
    }
}
