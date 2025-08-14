package com.qdd.narutofix.mixin;

import net.minecraft.client.settings.KeyBinding;
import com.qdd.narutofix.handler.JutsuHandler;
import net.narutomod.keybind.KeyBindingPowerIncrease;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindingPowerIncrease.class)
public class mixinKeyBindingPowerIncrease {

    @Shadow(remap = false)
    private KeyBinding keys;

    @Inject(method = "onKeyInput",at= @At(value = "INVOKE", target = "Lnet/narutomod/keybind/KeyBindingPowerIncrease;processKeyBind()V"),remap = false)
        private void processKeyBind( CallbackInfo ci) {
        if (keys.isPressed()) {
            JutsuHandler.onKeyEvent();
        }
    }
}
