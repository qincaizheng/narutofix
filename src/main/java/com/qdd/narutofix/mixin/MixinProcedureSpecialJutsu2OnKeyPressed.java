package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.narutomod.item.ItemAsuraCanon;
import net.narutomod.item.ItemAsuraPathArmor;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.procedure.ProcedureAnimalPath;
import net.narutomod.procedure.ProcedureChibakuTenseiOnKeyPressed;
import net.narutomod.procedure.ProcedureNarakaPath;
import net.narutomod.procedure.ProcedureOuterPath;
import net.narutomod.procedure.ProcedurePretaPath;
import net.narutomod.procedure.ProcedureSpecialJutsu2OnKeyPressed;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.HashMap;

@Mixin(ProcedureSpecialJutsu2OnKeyPressed.class)
public abstract class MixinProcedureSpecialJutsu2OnKeyPressed {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$routeForVirtualEye(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        Object pressed = dependencies.get("is_pressed");
        if (!(entity instanceof EntityPlayer) || !(pressed instanceof Boolean)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        boolean is_pressed = (boolean) pressed;

        ItemStack virtualEye = DojutsuEyeHelper.getVirtualEye(player);
        if (virtualEye.isEmpty()) return;
        if (virtualEye.getItem() != ItemRinnegan.helmet && virtualEye.getItem() != ItemTenseigan.helmet) return;

        if (is_pressed) {
            ci.cancel();
            return;
        }

        double which_path = virtualEye.hasTagCompound()
                ? virtualEye.getTagCompound().getDouble("which_path") : -1;
        World world = (World) dependencies.get("world");
        int x = (int) dependencies.get("x");
        int y = (int) dependencies.get("y");
        int z = (int) dependencies.get("z");

        // Handle Asura Path (which_path == 1) — equip/clear armor
        if (which_path == 1) {
            if (player.inventory.armorInventory.get(2).getItem() != ItemAsuraPathArmor.body) {
                ProcedureUtils.swapItemToSlot(player, EntityEquipmentSlot.CHEST, new ItemStack(ItemAsuraPathArmor.body));
                ProcedureUtils.swapItemToSlot(player, EntityEquipmentSlot.OFFHAND, new ItemStack(ItemAsuraCanon.block));
            }
        } else {
            player.inventory.clearMatchingItems(ItemAsuraPathArmor.body, -1, -1, null);
            player.inventory.clearMatchingItems(ItemAsuraCanon.block, -1, -1, null);
        }

        Map<String, Object> pathDeps;
        switch ((int) which_path) {
            case 4: // Naraka Path
                pathDeps = new HashMap<>();
                pathDeps.put("entity", entity);
                pathDeps.put("world", world);
                ProcedureNarakaPath.executeProcedure(pathDeps);
                break;
            case 3: // Preta Path
                pathDeps = new HashMap<>();
                pathDeps.put("entity", entity);
                pathDeps.put("world", world);
                ProcedurePretaPath.executeProcedure(pathDeps);
                break;
            case 2: // Animal Path
                pathDeps = new HashMap<>();
                pathDeps.put("entity", entity);
                pathDeps.put("world", world);
                ProcedureAnimalPath.executeProcedure(pathDeps);
                break;
            case 5: // Outer Path
                pathDeps = new HashMap<>();
                pathDeps.put("is_pressed", false);
                pathDeps.put("entity", entity);
                pathDeps.put("world", world);
                pathDeps.put("x", x);
                pathDeps.put("y", y);
                pathDeps.put("z", z);
                ProcedureOuterPath.executeProcedure(pathDeps);
                break;
            case 0: // Chibaku Tensei
                pathDeps = new HashMap<>();
                pathDeps.put("is_pressed", false);
                pathDeps.put("entity", entity);
                pathDeps.put("world", world);
                pathDeps.put("x", x);
                pathDeps.put("y", y);
                pathDeps.put("z", z);
                ProcedureChibakuTenseiOnKeyPressed.executeProcedure(pathDeps);
                break;
        }
        ci.cancel();
    }
}
