package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.narutomod.item.ItemAsuraCanon;
import net.narutomod.item.ItemAsuraPathArmor;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharinganObito;
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

        // If head slot has a dojutsu, let original procedure handle everything
        ItemStack headSlot = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (headSlot.getItem() instanceof ItemDojutsu.Base) {
            return;
        }

        ItemStack virtualEye = DojutsuEyeHelper.getVirtualEye(player);
        if (virtualEye.isEmpty()) return;

        // Six-tomoe has its own key bind system (R/V/X/H via EyeKeyHandler),
        // so skill 2 key should NOT trigger anything for six-tomoe
        if (virtualEye.getItem() == com.qdd.narutofix.items.ModItems.SIX_TOMOE_RINNEGAN) {
            ci.cancel();
            return;
        }

        // Virtual Rinnegan / Tenseigan -> six-path routing
        if (virtualEye.getItem() == ItemRinnegan.helmet || virtualEye.getItem() == ItemTenseigan.helmet) {
            if (!is_pressed) {
                double which_path = virtualEye.hasTagCompound()
                        ? virtualEye.getTagCompound().getDouble("which_path") : -1;
                World world = (World) dependencies.get("world");
                int x = (int) dependencies.get("x");
                int y = (int) dependencies.get("y");
                int z = (int) dependencies.get("z");

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
                    case 4:
                        pathDeps = new HashMap<>();
                        pathDeps.put("entity", entity);
                        pathDeps.put("world", world);
                        ProcedureNarakaPath.executeProcedure(pathDeps);
                        break;
                    case 3:
                        pathDeps = new HashMap<>();
                        pathDeps.put("entity", entity);
                        pathDeps.put("world", world);
                        ProcedurePretaPath.executeProcedure(pathDeps);
                        break;
                    case 2:
                        pathDeps = new HashMap<>();
                        pathDeps.put("entity", entity);
                        pathDeps.put("world", world);
                        ProcedureAnimalPath.executeProcedure(pathDeps);
                        break;
                    case 5:
                        pathDeps = new HashMap<>();
                        pathDeps.put("is_pressed", false);
                        pathDeps.put("entity", entity);
                        pathDeps.put("world", world);
                        pathDeps.put("x", x);
                        pathDeps.put("y", y);
                        pathDeps.put("z", z);
                        ProcedureOuterPath.executeProcedure(pathDeps);
                        break;
                    case 0:
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
            }
            ci.cancel();
            return;
        }

        // Virtual Mangekyo / Eternal / Obito -> summon susanoo
        if (virtualEye.getItem() == ItemMangekyoSharingan.helmet
                || virtualEye.getItem() == ItemMangekyoSharinganObito.helmet
                || virtualEye.getItem() == ItemMangekyoSharinganEternal.helmet) {
            if (!is_pressed) {
                com.qdd.narutofix.entity.susanoo.SusanooSummonHandler.summonSusanoo(player);
            }
            ci.cancel();
            return;
        }
    }
}
