package com.qdd.narutofix.mixin;

import net.narutomod.procedure.ProcedureWhiteZetsuFleshFoodEaten;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ProcedureWhiteZetsuFleshFoodEaten.class)
public class MixinProcedureWhiteZetsuFleshFoodEaten {
    @ModifyConstant(method = "executeProcedure", constant = @Constant(doubleValue = 0.2), remap = false)
    private static double modifyChance(double original) {
        return 0;
    }
}
