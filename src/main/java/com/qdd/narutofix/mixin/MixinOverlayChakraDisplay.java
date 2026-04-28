package com.qdd.narutofix.mixin;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.narutomod.gui.overlay.OverlayChakraDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverlayChakraDisplay.GUIRenderEventClass.class)
public abstract class MixinOverlayChakraDisplay {
    @Inject(method = "eventHandler", at = @At("HEAD"), cancellable = true, remap = false)
    private void narutofix$hideOriginalChakraHud(RenderGameOverlayEvent event, CallbackInfo ci) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HELMET) {
            ci.cancel();
        }
    }
}
