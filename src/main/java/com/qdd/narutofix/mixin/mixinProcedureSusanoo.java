package com.qdd.narutofix.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import com.qdd.narutofix.NarutoFix;
import net.narutomod.procedure.ProcedureSusanoo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProcedureSusanoo.class)
public class mixinProcedureSusanoo {

    @Inject(method = "execute", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private static void mixinexecute(EntityPlayer player, CallbackInfo ci){
        player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ,(SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation(NarutoFix.MODID, "player.susanoo")), SoundCategory.NEUTRAL,1f,1f);
    }
}
