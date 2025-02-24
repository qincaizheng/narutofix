package com.qdd.narutofix.network;

import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.gui.CustomContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenCustomInventory implements IMessage, IMessageHandler<PacketOpenCustomInventory, IMessage> {
    public PacketOpenCustomInventory() {

    }

    @Override
    public void toBytes(ByteBuf buffer) {}

    @Override
    public void fromBytes(ByteBuf buffer) {}

    @Override
    public IMessage onMessage(PacketOpenCustomInventory message, MessageContext ctx) {
        IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
        mainThread.addScheduledTask(new Runnable(){ public void run() {
            ctx.getServerHandler().player.openGui(NarutoFix.instance, 1, ctx.getServerHandler().player.world, 0, 0, 0);
        }});
        return null;
    }

}
