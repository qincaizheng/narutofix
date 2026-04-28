package com.qdd.narutofix.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSwitchhatbot implements IMessage{

    public int selected;

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(selected);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        selected = buf.readInt();
    }
    public static class Handler implements IMessageHandler<PacketSwitchhatbot, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSwitchhatbot message, MessageContext ctx) {
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
            EntityPlayer player = ctx.getServerHandler().player;
            IJutsuInventory inv = player.getCapability(JutsuInventoryCapability.Jutsu_INV, null);
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
            mainThread.addScheduledTask(new Runnable(){ public void run() {
                inv.setSelected(message.selected);
            }});
        });
            return null;

        }

    }
}