package com.qdd.narutofix.mixin;

import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.procedure.ProcedureAsuraPathArmorBodyTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProcedureAsuraPathArmorBodyTickEvent.class)
public abstract class MixinProcedureAsuraPathArmorBodyTickEvent {
    @Redirect(method = "executeProcedure", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shrink(I)V"), remap = true)
    private void narutofix$preventShrinkForVirtualEye(ItemStack stack, int amount) {
        // no-op: prevent the armor from self-destructing when the player
        // has a virtual six-tomoe rinnegan (which counts as a suitable helmet eye)
    }
}
