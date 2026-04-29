package com.qdd.narutofix.mixin;

import net.narutomod.Chakra;
import net.narutomod.procedure.ProcedureChakraRegenerationOnPotionActiveTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProcedureChakraRegenerationOnPotionActiveTick.class)
public class MixinProcedureChakraRegenerationOnPotionActiveTick {
    @Redirect(method = "executeProcedure", at = @At(value = "INVOKE",
            target = "Lnet/narutomod/Chakra$PathwayPlayer;consume(FZ)V"), remap = false)
    private static void narutofix$skipChakraRegenPotion(Chakra.PathwayPlayer pathway, float amount, boolean ignoreMax) {
    }
}
