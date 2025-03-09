package com.qdd.narutofix.container;


import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemSharingan;
import net.narutomod.procedure.ProcedureUtils;

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
}
