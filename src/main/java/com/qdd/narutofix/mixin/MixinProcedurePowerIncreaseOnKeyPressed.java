package com.qdd.narutofix.mixin;

import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.items.SixTomoeRinneganLogic;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.procedure.ProcedurePowerIncreaseOnKeyPressed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedurePowerIncreaseOnKeyPressed.class)
public abstract class MixinProcedurePowerIncreaseOnKeyPressed {

    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$handleKey(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        Object pressed = dependencies.get("is_pressed");
        Object world = dependencies.get("world");
        if (!(entity instanceof EntityPlayer) || !(pressed instanceof Boolean) || world == null || ((World) world).isRemote) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        boolean isPressed = (Boolean) pressed;

        // Six-Tomoe Rinnegan logic (existing)
        if (!(player.getHeldItemMainhand().getItem() instanceof ItemJutsu.Base
                || player.getHeldItemOffhand().getItem() instanceof ItemJutsu.Base)) {
            if (DojutsuEyeHelper.hasEitherEye(player, ModItems.SIX_TOMOE_RINNEGAN)) {
                ItemStack stack = DojutsuEyeHelper.getMatchingEye(player, ModItems.SIX_TOMOE_RINNEGAN);
                if (!stack.isEmpty()) {
                    SixTomoeRinneganLogic.onSwitchJutsuKey(isPressed, stack, player);
                    ci.cancel();
                    return;
                }
            }
        }

        // Only intercept for virtual Rinnegan/Tenseigan when head slot is empty or non-do-jutsu
        ItemStack headSlot = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (headSlot.getItem() instanceof ItemDojutsu.Base) {
            return; // head slot has a dojutsu — let original procedure handle it
        }

        ItemStack virtualEye = DojutsuEyeHelper.getVirtualEye(player);
        if (virtualEye.isEmpty()) return;
        if (virtualEye.getItem() != ItemRinnegan.helmet && virtualEye.getItem() != ItemTenseigan.helmet) return;

        if (player.getHeldItemMainhand().getItem() instanceof ItemJutsu.Base
                || player.getHeldItemOffhand().getItem() instanceof ItemJutsu.Base) return;

        if (!isPressed) {
            if (!virtualEye.hasTagCompound()) {
                virtualEye.setTagCompound(new NBTTagCompound());
            }
            double which_path = (virtualEye.getTagCompound().getDouble("which_path") + 1) % 6;
            virtualEye.getTagCompound().setDouble("which_path", which_path);
            if (!player.world.isRemote) {
                player.sendStatusMessage(new TextComponentString(
                        net.minecraft.util.text.translation.I18n.translateToLocal(
                                String.format("chattext.rinnegan.path%d", (int) which_path))), true);
            }
        }
        ci.cancel();
    }
}
