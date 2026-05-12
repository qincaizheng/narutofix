package com.qdd.narutofix.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.items.SixTomoeRinneganLogic;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.procedure.ProcedureAnimalPath;
import net.narutomod.procedure.ProcedureNarakaPath;
import net.narutomod.procedure.ProcedureOuterPath;
import net.narutomod.procedure.ProcedurePretaPath;
import net.narutomod.procedure.ProcedureSpecialJutsu2OnKeyPressed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.HashMap;

@Mixin(ProcedureSpecialJutsu2OnKeyPressed.class)
public abstract class MixinProcedureSpecialJutsu2OnKeyPressed {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$reroutePathsForSixTomoe(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        Object pressed = dependencies.get("is_pressed");
        if (!(entity instanceof EntityPlayer) || !(pressed instanceof Boolean)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        boolean is_pressed = (boolean) pressed;

        // Check if player has six-tomoe rinnegan as their effective eye
        ItemStack effectiveEye = DojutsuEyeHelper.getEffectiveEye(player);
        if (effectiveEye.isEmpty() || effectiveEye.getItem() != ModItems.SIX_TOMOE_RINNEGAN) {
            return;
        }

        // Six-tomoe routing uses its own skill group system (SixTomoeRinneganLogic)
        // For six path skills (Naraka/Preta/Animal/Asura), route directly here when not pressing
        if (!is_pressed) {
            double which_path = effectiveEye.hasTagCompound()
                    ? effectiveEye.getTagCompound().getDouble("which_path") : -1;
            net.minecraft.world.World world = (net.minecraft.world.World) dependencies.get("world");
            int x = (int) dependencies.get("x");
            int y = (int) dependencies.get("y");
            int z = (int) dependencies.get("z");

            if (which_path == 4) {
                // Naraka Path (地狱道/阎王)
                Map<String, Object> pathDeps = new HashMap<>();
                pathDeps.put("entity", entity);
                pathDeps.put("world", world);
                ProcedureNarakaPath.executeProcedure(pathDeps);
            } else if (which_path == 3) {
                // Preta Path (饿鬼道/吸收护盾)
                Map<String, Object> pathDeps = new HashMap<>();
                pathDeps.put("entity", entity);
                pathDeps.put("world", world);
                ProcedurePretaPath.executeProcedure(pathDeps);
            } else if (which_path == 2) {
                // Animal Path (通灵狗)
                Map<String, Object> pathDeps = new HashMap<>();
                pathDeps.put("entity", entity);
                pathDeps.put("world", world);
                ProcedureAnimalPath.executeProcedure(pathDeps);
            } else if (which_path == 5) {
                // Outer Path (外道魔像)
                Map<String, Object> pathDeps = new HashMap<>();
                pathDeps.put("is_pressed", is_pressed);
                pathDeps.put("entity", entity);
                pathDeps.put("world", world);
                pathDeps.put("x", x);
                pathDeps.put("y", y);
                pathDeps.put("z", z);
                ProcedureOuterPath.executeProcedure(pathDeps);
            }
            // which_path == 1 (Asura Path) is handled by ProcedureRinneganHelmetTickEvent
            // which_path == 0 (Chibaku Tensei) is handled by SixTomoeRinneganLogic skill groups
        }
        ci.cancel();
    }

    @Redirect(method = "executeProcedure", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;"))
    private static Object narutofix$getHelmet(NonNullList<ItemStack> inventory, int index, @Local(name="entity") Entity player) {
        if(inventory.get(index).getItem() instanceof ItemDojutsu.Base) {
            return inventory.get(index);
        }
        return DojutsuEyeHelper.getVirtualEye((EntityLivingBase) player);
    }
}
