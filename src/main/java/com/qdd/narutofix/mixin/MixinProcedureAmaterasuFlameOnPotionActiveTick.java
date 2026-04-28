package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.procedure.ProcedureAmaterasuFlameOnPotionActiveTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureAmaterasuFlameOnPotionActiveTick.class)
public abstract class MixinProcedureAmaterasuFlameOnPotionActiveTick {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$respectVirtualSharingan(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }

        if (DojutsuEyeHelper.hasEitherEye((EntityLivingBase) entity, ItemMangekyoSharingan.helmet, ItemMangekyoSharinganEternal.helmet)) {
            ((EntityLivingBase) entity).removePotionEffect(PotionAmaterasuFlame.potion);
            entity.extinguish();
            ci.cancel();
        }
    }
}