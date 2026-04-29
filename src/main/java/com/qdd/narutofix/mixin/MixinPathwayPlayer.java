package com.qdd.narutofix.mixin;

import net.narutomod.Chakra;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Chakra.PathwayPlayer.class)
public class MixinPathwayPlayer {
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE",
            target = "Lnet/narutomod/Chakra$PathwayPlayer;consume(F)V", ordinal = 0), remap = false)
    private void narutofix$skipSleepChakraRegen(Chakra.PathwayPlayer pathway, float amount) {
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE",
            target = "Lnet/narutomod/Chakra$PathwayPlayer;consume(F)V", ordinal = 1), remap = false)
    private void narutofix$skipIdleChakraRegen(Chakra.PathwayPlayer pathway, float amount) {
    }
}
