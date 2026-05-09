package com.qdd.narutofix.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.qdd.narutofix.entity.susanoo.SusanooSummonHandler;

public class PacketNarutofixSusanooUpgrade implements IMessage, IMessageHandler<PacketNarutofixSusanooUpgrade, IMessage> {

    public PacketNarutofixSusanooUpgrade() {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(PacketNarutofixSusanooUpgrade message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        player.getServerWorld().addScheduledTask(() ->
                SusanooSummonHandler.upgradeSusanoo(player));
        return null;
    }
}
