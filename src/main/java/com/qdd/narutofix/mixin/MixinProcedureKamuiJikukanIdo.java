package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.narutomod.gui.overlay.OverlayByakuganView;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.procedure.ProcedureKamuiJikukanIdo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureKamuiJikukanIdo.class)
public abstract class MixinProcedureKamuiJikukanIdo {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$checkVirtualBlindness(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityPlayer)) {
            return;
        }

        ItemStack eye = DojutsuEyeHelper.getMatchingEye((EntityPlayer) entity,
                ItemMangekyoSharinganObito.helmet, ItemMangekyoSharinganEternal.helmet);
        if (!eye.isEmpty() && eye.hasTagCompound() && eye.getTagCompound().getBoolean("sharingan_blinded")) {
            if (entity.getEntityData().getBoolean("kamui_teleport")) {
                OverlayByakuganView.sendCustomData(entity, false, 70);
                entity.getEntityData().setBoolean("kamui_teleport", false);
            }
            ci.cancel();
        }
    }
}
