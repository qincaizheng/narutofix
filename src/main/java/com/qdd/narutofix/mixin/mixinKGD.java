package com.qdd.narutofix.mixin;

import com.qdd.narutofix.items.Sharingan1;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.narutomod.item.ItemSharingan;
import net.narutomod.procedure.ProcedureKGDistribution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProcedureKGDistribution.class)
public class mixinKGD {

    @Redirect(
        method = "executeProcedure",
        at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V"),
        remap = false
    )
    private static void narutofix$replaceSharinganWithOneTomoe(EntityPlayer player, ItemStack stack) {
        if (stack != null && stack.getItem() == ItemSharingan.helmet) {
            ItemStack oneTomoe = new ItemStack(Sharingan1.helmet, 1);
            if (stack.hasTagCompound()) {
                oneTomoe.setTagCompound(stack.getTagCompound().copy());
            }
            ItemHandlerHelper.giveItemToPlayer(player, oneTomoe);
        } else {
            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
    }
}
