package com.qdd.narutofix.gui;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.narutomod.item.ItemSharingan;
import javax.annotation.Nullable;

public class CustomContainer extends ContainerPlayer{
    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    private ItemStackHandler items = new ItemStackHandler(2);

    public CustomContainer(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer playerIn) {
        super(playerInventory,localWorld,playerIn);
        final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[0];
        for (int i = 0; i < 2; ++i){
            this.addSlotToContainer(new SlotItemHandler(items, i, -2 + i * 18, -18){
                /**
                 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
                 * in the case of armor slots)
                 */
                public int getSlotStackLimit()
                {
                    return 1;
                }
                /**
                 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace
                 * fuel.
                 */
                public boolean isItemValid(ItemStack stack)
                {
                    return super.isItemValid(stack) && stack.getItem() instanceof ItemSharingan.Base;
                }
                /**
                 * Return whether this slot's stack can be taken from this slot.
                 */
                public boolean canTakeStack(EntityPlayer playerIn)
                {
                    return super.canTakeStack(playerIn);
                }
                @Nullable
                @SideOnly(Side.CLIENT)
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            });
        }
    }
}
