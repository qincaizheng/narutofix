package com.qdd.narutofix.mixin;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.*;


@Mixin(InventoryPlayer.class)
public class mixinInventoryPlayer {
    public NonNullList<ItemStack> armorInventory=NonNullList.<ItemStack>withSize(6, ItemStack.EMPTY);

}
