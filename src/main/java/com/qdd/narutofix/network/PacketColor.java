package com.qdd.narutofix.network;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EntityEquipmentSlot;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.item.ItemSharingan;

public class PacketColor implements IMessage{
    public int color;

    public PacketColor() {
    }

    public PacketColor(int color) {
        this.color = color;
    }
    @Override
    public void fromBytes(ByteBuf buf)
    {
        color = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(color);
    }

    public static class Handler implements IMessageHandler<PacketColor, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(PacketColor message, MessageContext ctx) {
            ((ItemSharingan.Base)ItemSharingan.helmet).setColor(Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.HEAD),message.color);
            return null;
        }
    }
}
