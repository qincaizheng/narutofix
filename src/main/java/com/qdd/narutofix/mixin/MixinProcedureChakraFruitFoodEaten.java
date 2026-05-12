package com.qdd.narutofix.mixin;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.util.EnergyMath;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.narutomod.procedure.ProcedureChakraFruitFoodEaten;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureChakraFruitFoodEaten.class)
public abstract class MixinProcedureChakraFruitFoodEaten {
    @Inject(method = "executeProcedure", at = @At("TAIL"), remap = false)
    private static void narutofix$addBodyAndSoul(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) entity;
        EnergyMath.addBodyAndSoul(player,
                Configs.chakraFruit.soulAmount,
                Configs.chakraFruit.bodyAmount);
    }
}
