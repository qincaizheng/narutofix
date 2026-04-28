package com.qdd.narutofix.cap.eye;

import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class StoredEyesItemHandler implements IItemHandlerModifiable {
    private final EntityPlayer player;

    public StoredEyesItemHandler(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public int getSlots() {
        return EyeInventoryConstants.STORAGE_SLOT_COUNT;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        IPlayerAwakeningData data = EyeInventoryManager.getData(this.player);
        return data != null && slot >= 0 && slot < data.getStoredEyes().size() ? data.getStoredEyes().get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        IPlayerAwakeningData data = EyeInventoryManager.getData(this.player);
        if (data == null || stack.isEmpty() || !EyeInventoryManager.isEyeItem(stack) || slot < 0 || slot >= this.getSlots()) {
            return stack;
        }

        int insertSlot = EyeInventoryManager.findFirstEmptyStoredSlot(data);
        if (insertSlot < 0) {
            return stack;
        }

        if (!simulate) {
            ItemStack single = stack.copy();
            single.setCount(1);
            EyeInventoryManager.claimEyeOwnership(this.player, single);
            data.setStoredEye(insertSlot, single);
        }

        ItemStack remainder = stack.copy();
        remainder.shrink(1);
        return remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        IPlayerAwakeningData data = EyeInventoryManager.getData(this.player);
        if (data == null || amount <= 0 || slot < 0 || slot >= this.getSlots()) {
            return ItemStack.EMPTY;
        }

        EyeInventoryManager.compactStoredEyes(data);
        ItemStack stack = this.getStackInSlot(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = stack.copy();
        if (!simulate) {
            EyeInventoryManager.removeStoredEyeAt(data, slot);
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
        if (data != null && slot >= 0 && slot < data.getStoredEyes().size()) {
            ItemStack single = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            if (!single.isEmpty()) {
                single.setCount(1);
                EyeInventoryManager.claimEyeOwnership(this.player, single);
            }
            data.setStoredEye(slot, single);
        }
    }
}