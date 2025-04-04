package com.qdd.narutofix.mixin;

import com.qdd.narutofix.command.SetSusanooColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.entity.EntitySusanooSkeleton;
import net.narutomod.procedure.ProcedureSusanoo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;

@Mixin(value = ProcedureSusanoo.class)
public class mixinProcedureSusanoo {
//    @Inject(method = "changeEntity",at= @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
//    private static void changeEntity(EntityPlayer player, Entity oldSusanoo, Entity newSusanoo, CallbackInfo ci) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
////        SetSusanooColor.setColor(((EntitySusanooBase) newSusanoo),SetSusanooColor.color);
//        ((EntitySusanooBase) newSusanoo).attackEntityRanged(newSusanoo.motionX,newSusanoo.motionY,newSusanoo.motionZ);
//    }
//
//    @Inject(method = "execute",at= @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), cancellable = true)
//    private static void execute(EntityPlayer player, CallbackInfo ci) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
//        World world = player.world;
//        EntitySusanooBase entityCustom = new EntitySusanooSkeleton.EntityCustom(player);
////        SetSusanooColor.setColor(entityCustom,SetSusanooColor.color);
//        entityCustom.attackEntityRanged(entityCustom.motionX,entityCustom.motionY,entityCustom.motionZ);
//        world.spawnEntity(entityCustom);
//        player.getEntityData().setInteger("summonedSusanooID", entityCustom.getEntityId());
//        ci.cancel();
//    }
}
