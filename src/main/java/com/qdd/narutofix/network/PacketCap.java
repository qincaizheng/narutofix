package com.qdd.narutofix.network;

import com.qdd.narutofix.cap.IJutsuInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import static com.qdd.narutofix.cap.JutsuInventoryCapability.Jutsu_INV;

public class PacketCap implements IMessage
{
    public NBTTagCompound nbt;

    @Override
    public void fromBytes(ByteBuf buf)
    {
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, nbt);
    }
    public static class Handler implements IMessageHandler<PacketCap, IMessage>
    {
        @Override
        public IMessage onMessage(PacketCap message, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                final NBTBase nbt = message.nbt;
                Minecraft.getMinecraft().addScheduledTask(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        EntityPlayer player = Minecraft.getMinecraft().player;
                        if (player.hasCapability(Jutsu_INV, null))
                        {
//                            System.out.println(123);
                            IJutsuInventory inv = player.getCapability(Jutsu_INV, null);
                            Capability.IStorage<IJutsuInventory> storage = Jutsu_INV.getStorage();
                            storage.readNBT(Jutsu_INV, player.getCapability(Jutsu_INV, null), null, nbt);
                        }
                    }
                });
            }
            return null;
        }
    }
}