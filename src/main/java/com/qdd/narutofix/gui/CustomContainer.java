package com.qdd.narutofix.gui;


import com.google.common.collect.Lists;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemSharingan;
import javax.annotation.Nullable;
import java.util.List;

public class CustomContainer extends ContainerPlayer
{
    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    /** The crafting matrix inventory. */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public InventoryCraftResult craftResult = new InventoryCraftResult();
    /** Determines if inventory manipulation should be handled. */
    public boolean isLocalWorld;
    private final EntityPlayer player;

    public CustomContainer(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer playerIn)
    {
        super(playerInventory,localWorld,playerIn);
        this.inventoryItemStacks.clear();
        this.inventorySlots.clear();
        System.out.println(this.inventorySlots.size());
        this.isLocalWorld = localWorld;
        this.player = playerIn;
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 154, 28));

        for (int i = 0; i < 2; ++i)
        {
            for (int j = 0; j < 2; ++j)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }

        for (int k = 0; k < 4; ++k)
        {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            this.addSlotToContainer(new Slot(playerInventory, 36 + (3 - k), 8, 8 + k * 18)
            {
                /**
                 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
                 * in the case of armor slots)
                 */
                @Override
                public int getSlotStackLimit()
                {
                    return 1;
                }
                /**
                 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace
                 * fuel.
                 */
                @Override
                public boolean isItemValid(ItemStack stack)
                {
                    return stack.getItem().isValidArmor(stack, entityequipmentslot, player);
                }
                /**
                 * Return whether this slot's stack can be taken from this slot.
                 */
                @Override
                public boolean canTakeStack(EntityPlayer playerIn)
                {
                    ItemStack itemstack = this.getStack();
                    return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
                }
                @Override
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            });
        }
        for (int n = 0; n < 2; ++n){
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[0];
            this.addSlotToContainer(new Slot(playerInventory, 40 + (1 - n), -2 + n * 18, -17){
                /**
                 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
                 * in the case of armor slots)
                 */
                @Override
                public int getSlotStackLimit()
                {
                    return 1;
                }
                /**
                 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace
                 * fuel.
                 */
                @Override
                public boolean isItemValid(ItemStack stack)
                {
                    return stack.getItem().isValidArmor(stack, entityequipmentslot, player) && stack.getItem() instanceof ItemSharingan.Base && ((ItemSharingan.Base) stack.getItem()).isOwner(stack, player);
                }
                /**
                 * Return whether this slot's stack can be taken from this slot.
                 */
                @Override
                public boolean canTakeStack(EntityPlayer playerIn)
                {
                    ItemStack itemstack = this.getStack();
                    return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
                }
                @Override
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            });
        }

        for (int l = 0; l < 3; ++l)
        {
            for (int j1 = 0; j1 < 9; ++j1)
            {
                this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
        }

        this.addSlotToContainer(new Slot(playerInventory, 42, 77, 62)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return super.isItemValid(stack);
            }
            @Override
            public String getSlotTexture()
            {
                return "minecraft:items/empty_armor_slot_shield";
            }
        });
        this.onCraftMatrixChanged(this.craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.slotChangedCraftingGrid(this.player.world, this.player, this.craftMatrix, this.craftResult);
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.craftResult.clear();

        if (!playerIn.world.isRemote)
        {
            this.clearContainer(playerIn, playerIn.world, this.craftMatrix);
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        System.out.println(this.inventorySlots.size());

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);

            if (index == 0)
            {
                if (!this.mergeItemStack(itemstack1, 11, 47, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 1 && index < 5)
            {
                if (!this.mergeItemStack(itemstack1, 11, 47, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 5 && index < 9)
            {
                if (!this.mergeItemStack(itemstack1, 11, 47, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 9 && index < 11)
            {
                if (!this.mergeItemStack(itemstack1, 11, 47, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && (!((Slot)this.inventorySlots.get(9)).getHasStack()||!((Slot)this.inventorySlots.get(10)).getHasStack()) && itemstack1.getItem() instanceof ItemSharingan.Base)
            {
                if (!this.mergeItemStack(itemstack1, 9, 11, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !((Slot)this.inventorySlots.get(8 - entityequipmentslot.getIndex())).getHasStack())
            {
                int i = 8 - entityequipmentslot.getIndex();

                if (!this.mergeItemStack(itemstack1, i, i + 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !((Slot)this.inventorySlots.get(45)).getHasStack())
            {
                if (!this.mergeItemStack(itemstack1, 47, 48, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 11 && index < 38)
            {
                if (!this.mergeItemStack(itemstack1, 38, 47, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 38 && index < 47)
            {
                if (!this.mergeItemStack(itemstack1, 11, 38, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 11, 47, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

            if (index == 0)
            {
                playerIn.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
}
/*
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
                 /
                public int getSlotStackLimit()
                {
                    return 1;
                }
                /**
                 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace
                 * fuel.
                 /
                public boolean isItemValid(ItemStack stack)
                {
                    return super.isItemValid(stack) && stack.getItem() instanceof ItemSharingan.Base;
                }
                /**
                 * Return whether this slot's stack can be taken from this slot.
                 /
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
*/