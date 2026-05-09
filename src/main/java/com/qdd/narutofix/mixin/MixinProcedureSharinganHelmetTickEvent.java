package com.qdd.narutofix.mixin;

import com.qdd.narutofix.Configs;
import net.minecraft.nbt.NBTTagCompound;
import net.narutomod.NarutomodModVariables;
import net.narutomod.procedure.ProcedureSharinganHelmetTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureSharinganHelmetTickEvent.class)
public abstract class MixinProcedureSharinganHelmetTickEvent {

    /**
     * SOUL mode: cancel the entire vanilla evolution procedure.
     * VANILLA/BOTH: let the procedure run normally.
     */
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$cancelIfSoulOnly(Map<String, Object> dependencies, CallbackInfo ci) {
        if (Configs.sharinganEvolutionSource == Configs.SharinganEvolutionSource.SOUL) {
            ci.cancel();
        }
    }

    /**
     * SOUL mode: if the vanilla procedure somehow was not cancelled at HEAD,
     * still block BATTLEXP-gated evolution as a safety net.
     * VANILLA/BOTH: do not override BATTLEXP reads, so vanilla evolution can fully work.
     */
    @Redirect(
            method = "executeProcedure",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getDouble(Ljava/lang/String;)D", ordinal = 1),
            remap = false)
    private static double narutofix$disableBattleXpMangekyoEvolution(NBTTagCompound compound, String key) {
        if (Configs.sharinganEvolutionSource == Configs.SharinganEvolutionSource.SOUL
                && NarutomodModVariables.BATTLEXP.equals(key)) {
            return 0.0D;
        }
        return compound.getDouble(key);
    }
}
