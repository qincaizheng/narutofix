package com.qdd.narutofix.mixin;

import net.minecraft.nbt.NBTTagCompound;
import net.narutomod.NarutomodModVariables;
import net.narutomod.procedure.ProcedureSharinganHelmetTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProcedureSharinganHelmetTickEvent.class)
public abstract class MixinProcedureSharinganHelmetTickEvent {
    @Redirect(
            method = "executeProcedure",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getDouble(Ljava/lang/String;)D", ordinal = 1),
            remap = false)
    private static double narutofix$disableBattleXpMangekyoEvolution(NBTTagCompound compound, String key) {
        if (NarutomodModVariables.BATTLEXP.equals(key)) {
            return 0.0D;
        }
        return compound.getDouble(key);
    }
}
