package com.qdd.narutofix.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.Chakra;

import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;

public class PacketSyncChakra implements IMessage {
    private double amount;
    private double max;

    public PacketSyncChakra() {
    }

    public PacketSyncChakra(double amount, double max) {
        this.amount = amount;
        this.max = max;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.amount = buf.readDouble();
        this.max = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.amount);
        buf.writeDouble(this.max);
    }

    public static void sync(EntityPlayerMP player) {
        Chakra.Pathway<?> pathway = Chakra.pathway(player);
        if (pathway != null) {
            PACKET_HANDLER.sendTo(new PacketSyncChakra(pathway.getAmount(), pathway.getMax()), player);
        }
    }

    public static class Handler implements IMessageHandler<PacketSyncChakra, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(PacketSyncChakra message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player == null) {
                    return;
                }
                Chakra.Pathway<?> pathway = Chakra.pathway(player);
                if (pathway == null) {
                    return;
                }
                pathway.setMax(message.max);
                pathway.consume(pathway.getAmount() - message.amount);
            });
            return null;
        }
    }
}
