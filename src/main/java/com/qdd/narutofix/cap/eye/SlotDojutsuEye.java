package com.qdd.narutofix.cap.eye;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotDojutsuEye extends SlotItemHandler {
    private final EntityPlayer player;
    private final boolean requireEquipCheck;
    private final boolean allowInsert;
    private final boolean allowExtract;

    public SlotDojutsuEye(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        this(itemHandler, index, xPosition, yPosition, null, false, true, true);
    }

    public SlotDojutsuEye(IItemHandler itemHandler, int index, int xPosition, int yPosition, EntityPlayer player, boolean requireEquipCheck) {
        this(itemHandler, index, xPosition, yPosition, player, requireEquipCheck, true, true);
    }

    public SlotDojutsuEye(IItemHandler itemHandler, int index, int xPosition, int yPosition, EntityPlayer player,
                          boolean requireEquipCheck, boolean allowInsert, boolean allowExtract) {
        super(itemHandler, index, xPosition, yPosition);
        this.player = player;
        this.requireEquipCheck = requireEquipCheck;
        this.allowInsert = allowInsert;
        this.allowExtract = allowExtract;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (!this.allowInsert) {
            return false;
        }
        return EyeInventoryManager.isEyeItem(stack);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return this.allowExtract;
    }
}