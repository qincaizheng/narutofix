package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.procedure.ProcedureAmaterasu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureAmaterasu.class)
public abstract class MixinProcedureAmaterasu {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$checkVirtualBlindness(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityPlayer)) {
            return;
        }

        ItemStack eye = DojutsuEyeHelper.getMatchingEye((EntityPlayer) entity,
                ItemMangekyoSharingan.helmet, ItemMangekyoSharinganEternal.helmet);
        if (!eye.isEmpty() && eye.hasTagCompound() && eye.getTagCompound().getBoolean("sharingan_blinded")) {
            entity.getEntityData().setBoolean("amaterasu_active", false);
            ci.cancel();
        }
    }
}