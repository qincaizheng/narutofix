package com.qdd.narutofix.network;

import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;

public class PacketSyncSoulEnergy implements IMessage {
    private double current;
    private double max;

    public PacketSyncSoulEnergy() {}

    public PacketSyncSoulEnergy(double current, double max) {
        this.current = current;
        this.max = max;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.current = buf.readDouble();
        this.max = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.current);
        buf.writeDouble(this.max);
    }

    public static void sync(EntityPlayerMP player) {
        ISoulEnergyData data = SoulEnergyDataProvider.get(player);
        if (data != null) {
            PACKET_HANDLER.sendTo(
                new PacketSyncSoulEnergy(data.getCurrent(), data.getMax()), player);
        }
    }

    public static class Handler implements IMessageHandler<PacketSyncSoulEnergy, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(PacketSyncSoulEnergy message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player != null) {
                    ISoulEnergyData data = SoulEnergyDataProvider.get(player);
                    if (data != null) {
                        data.setCurrent(message.current);
                        data.setMax(message.max);
                    }
                }
            });
            return null;
        }
    }
}
