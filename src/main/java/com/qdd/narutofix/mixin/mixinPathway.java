package com.qdd.narutofix.mixin;

import com.qdd.narutofix.potion.PotionLoader;
import net.minecraft.entity.EntityLivingBase;
import net.narutomod.Chakra;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chakra.Pathway.class)
public class mixinPathway<T extends EntityLivingBase> {
    @Shadow(remap = false) @Final protected T user;

    @Inject(method = "onUpdate",at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;addPotionEffect(Lnet/minecraft/potion/PotionEffect;)V"),cancellable = true)
    private void onUpdate(CallbackInfo ci) {
        if(this.user.isPotionActive(PotionLoader.PotionIzanagi)){
            ci.cancel();
        }
    }
}
