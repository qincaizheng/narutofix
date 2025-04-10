package com.qdd.narutofix.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.narutomod.procedure.ProcedureSusanoo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.qdd.narutofix.handler.ModSoundHandler;

@Mixin(ProcedureSusanoo.class)
public class mixinProcedureSusanoo {

    @Inject(method = "execute", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private static void mixinexecute(EntityPlayer player, CallbackInfo ci){
        player.playSound(ModSoundHandler.SUSANOOSOUND,1f,1f);
    }
}
