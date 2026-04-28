package com.qdd.narutofix.container;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import com.qdd.narutofix.items.CopyJutsuScroll;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemSharingan;

import javax.annotation.Nullable;

public class JutsuContainer extends Container {

    private final ItemStackHandler jutsuInventory ;
    public boolean isLocalWorld;
    public final EntityPlayer player;

    public JutsuContainer(EntityPlayer player, boolean localWorld) {
        super();
        IJutsuInventory inv=player.getCapability(JutsuInventoryCapability.Jutsu_INV, null);
        this.jutsuInventory=inv.getItems();
//        System.out.println("jutsuInventory: "+jutsuInventory);
        this.player=player;
        this.isLocalWorld=localWorld;
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
            this.addSlotToContainer(new SlotItemHandler(jutsuInventory, j+i*9, 8 + j * 18, (i>0?24:20)+i * 18){
                @Override
                public int getSlotStackLimit()
                {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack stack)
                {
                    return stack != null &&(stack.getItem() instanceof  ItemJutsu.Base)&& super.isItemValid(stack);
                }
            });
            }
        }
//        this.addSlotToContainer(new SlotItemHandler(jutsuInventory, 27, (176-20)/2+1, -19) {
//            @Override
//            public int getSlotStackLimit()
//            {
//                return 1;
//            }
//
//            @Override
//            public boolean isItemValid(ItemStack stack)
//            {
//                return stack != null &&(stack.getItem() instanceof  ItemSharingan.Base) && ProcedureUtils.isOriginalOwner(player,stack) && super.isItemValid(stack);
//            }
//        });

        if(inv.getIzanagiSize()>0){
            for (int i = 0; i < inv.getIzanagiSize(); ++i){
                this.addSlotToContainer(new SlotItemHandler(jutsuInventory, 28+i, 181, i*20+1) {
                    @Override
                    public int getSlotStackLimit()
                    {
                        return 1;
                    }

                    @Override
                    public boolean isItemValid(ItemStack stack)
                    {
                        return stack != null &&(stack.getItem() instanceof  ItemSharingan.Base)&& super.isItemValid(stack);
                    }
                });
            }
        }

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 82 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 3*18+4+82));
        }

    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        InventoryPlayer inventoryplayer = player.inventory;
        if (inventoryplayer.getItemStack().getItem() instanceof CopyJutsuScroll&&slotId>=0){
            Slot slot = this.inventorySlots.get(slotId);
            if (slot.getStack().getItem() instanceof ItemJutsu.Base){
                ((CopyJutsuScroll)inventoryplayer.getItemStack().getItem()).copy(slot.getStack());
            }else {
                return super.slotClick(slotId, dragType, clickTypeIn, player);
            }
        }else{
            return super.slotClick(slotId, dragType, clickTypeIn, player);
        }
        return ItemStack.EMPTY;
    }
}
