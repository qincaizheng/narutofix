package com.qdd.narutofix.cap.eye;

import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class EquippedEyeItemHandler implements IItemHandlerModifiable {
    private final EntityPlayer player;

    public EquippedEyeItemHandler(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        IPlayerAwakeningData data = EyeInventoryManager.getData(this.player);
        return slot == 0 && data != null ? data.getEquippedEye() : ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        IPlayerAwakeningData data = EyeInventoryManager.getData(this.player);
        if (slot != 0 || data == null || stack.isEmpty() || !EyeInventoryManager.isEyeItem(stack) || !data.getEquippedEye().isEmpty()) {
            return stack;
        }

        if (!simulate) {
            ItemStack single = stack.copy();
            single.setCount(1);
            EyeInventoryManager.claimEyeOwnership(this.player, single);
            data.setEquippedEye(single);
        }

        ItemStack remainder = stack.copy();
        remainder.shrink(1);
        return remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        IPlayerAwakeningData data = EyeInventoryManager.getData(this.player);
        if (slot != 0 || amount <= 0 || data == null || data.getEquippedEye().isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = data.getEquippedEye().copy();
        if (!simulate) {
            data.setEquippedEye(ItemStack.EMPTY);
        }
        return result;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        IPlayerAwakeningData data = EyeInventoryManager.getData(this.player);
        if (slot == 0 && data != null) {
            ItemStack single = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            if (!single.isEmpty()) {
                single.setCount(1);
                EyeInventoryManager.claimEyeOwnership(this.player, single);
            }
            data.setEquippedEye(single);
        }
    }
}