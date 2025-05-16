package com.qdd.narutofix.network;

import com.qdd.narutofix.cap.IJutsuInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;

import static com.qdd.narutofix.cap.JutsuInventoryCapability.Jutsu_INV;

public class PacketSwitcheres  implements IMessage{
    public void fromBytes(ByteBuf buf)
    {

    }

    @Override
    public void toBytes(ByteBuf buf)
    {

    }
    public static class Handler implements IMessageHandler<PacketSwitcheres, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSwitcheres message, MessageContext ctx)
        {
            ctx.getServerHandler().player.server.addScheduledTask(() -> {
                EntityPlayer player =ctx.getServerHandler().player;
                ItemStack eyes=player.inventory.armorInventory.get(3);
                if(eyes.getItem() == ItemMangekyoSharinganObito.helmet || eyes.getItem() == ItemRinnegan.helmet|| eyes.getItem()== ItemMangekyoSharinganEternal.helmet
                        ||eyes.getItem()== ItemTenseigan.helmet){
                    if(eyes.getItem() == ItemMangekyoSharinganObito.helmet ||eyes.getItem()== ItemMangekyoSharinganEternal.helmet){
                        for(ItemStack stack:player.inventory.mainInventory){
                            if(stack.getItem()==ItemRinnegan.helmet){
                                if (!player.inventory.addItemStackToInventory(eyes)) {
                                    player.dropItem(eyes, false);
                                }
                                player.inventory.armorInventory.set(3, stack.copy());
                                stack.shrink(1);
                                player.inventory.markDirty();
                                return;
                            }
                        }
                    } else if (eyes.getItem() == ItemRinnegan.helmet) {
                        for(ItemStack stack:player.inventory.mainInventory){
                            if(stack.getItem()==ItemTenseigan.helmet){
                                if (!player.inventory.addItemStackToInventory(eyes)) {
                                    player.dropItem(eyes, false);
                                }
                                player.inventory.armorInventory.set(3, stack.copy());
                                stack.shrink(1);
                                player.inventory.markDirty();
                                return;
                            }
                        }
                    }else{
                        for(ItemStack stack:player.inventory.mainInventory){
                            if(stack.getItem()== ItemMangekyoSharinganObito.helmet||stack.getItem()== ItemMangekyoSharinganEternal.helmet){
                                if (!player.inventory.addItemStackToInventory(eyes)) {
                                    player.dropItem(eyes, false);
                                }
                                player.inventory.armorInventory.set(3, stack.copy());
                                stack.shrink(1);
                                player.inventory.markDirty();
                                return;
                            }
                        }
                    }
                }



            });
            return null;
        }
    }
}
