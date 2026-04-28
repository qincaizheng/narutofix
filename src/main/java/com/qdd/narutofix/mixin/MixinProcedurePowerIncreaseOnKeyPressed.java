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
import net.minecraft.world.World;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedurePowerIncreaseOnKeyPressed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedurePowerIncreaseOnKeyPressed.class)
public abstract class MixinProcedurePowerIncreaseOnKeyPressed {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$useVirtualTomoeRinnegan(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        Object pressed = dependencies.get("is_pressed");
        Object world = dependencies.get("world");
        if (!(entity instanceof EntityPlayer) || !(pressed instanceof Boolean) || world == null || ((World) world).isRemote) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        if (player.getHeldItemMainhand().getItem() instanceof ItemJutsu.Base
                || player.getHeldItemOffhand().getItem() instanceof ItemJutsu.Base) {
            return;
        }

        if (!DojutsuEyeHelper.hasEitherEye(player, ModItems.SIX_TOMOE_RINNEGAN)) {
            return;
        }

        ItemStack stack = DojutsuEyeHelper.getMatchingEye(player, ModItems.SIX_TOMOE_RINNEGAN);
        if (stack.isEmpty()) {
            return;
        }
        SixTomoeRinneganLogic.onSwitchJutsuKey((Boolean) pressed, stack, player);
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