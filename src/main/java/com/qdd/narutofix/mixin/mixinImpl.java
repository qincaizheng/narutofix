package com.qdd.narutofix.mixin;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IForgeRegistryEntry.Impl.class)
public abstract class mixinImpl<T  extends IForgeRegistryEntry<T>> implements IForgeRegistryEntry<T> {
    @Shadow(remap = false)
    private ResourceLocation registryName;

    @Inject(method = "setRegistryName(Ljava/lang/String;)Lnet/minecraftforge/registries/IForgeRegistryEntry;",at = @At("HEAD"),remap = false, cancellable = true)
    public final void setRegistryName(String name, CallbackInfoReturnable<T> cir)
    {
        this.registryName = GameData.checkPrefix(name, true);
        cir.setReturnValue((T) this);
    }
}
