package com.qdd.narutofix.gui;

import com.qdd.narutofix.cap.eye.EyeInventoryConstants;
import com.qdd.narutofix.cap.eye.StoredEyesItemHandler;
import com.qdd.narutofix.cap.eye.SlotDojutsuEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerEyeStorage extends Container {
    private final EntityPlayer player;

    public ContainerEyeStorage(InventoryPlayer playerInventory) {
        this.player = playerInventory.player;
        StoredEyesItemHandler storedEyes = new StoredEyesItemHandler(this.player);

        for (int index = 0; index < EyeInventoryConstants.STORAGE_SLOT_COUNT; index++) {
            this.addSlotToContainer(new SlotDojutsuEye(storedEyes, index, 44 + index * 18, 20));
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlotToContainer(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 51 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlotToContainer(new Slot(playerInventory, column, 8 + column * 18, 109));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn == this.player;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()){
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < EyeInventoryConstants.STORAGE_SLOT_COUNT){
                if (!this.mergeItemStack(itemstack1, EyeInventoryConstants.STORAGE_SLOT_COUNT, this.inventorySlots.size(), true)){
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, EyeInventoryConstants.STORAGE_SLOT_COUNT, false)){
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()){
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}