package com.qdd.narutofix.network;

import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFly implements IMessage {
    public boolean allowFlying;

    @Override
    public void fromBytes(ByteBuf buf)
    {
        allowFlying = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(allowFlying);
    }

    public static class Handler implements IMessageHandler<PacketFly, IMessage>
    {
        @Override
        public IMessage onMessage(PacketFly message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                player.capabilities.allowFlying=message.allowFlying;
            });
            return null;
        }
    }
}
