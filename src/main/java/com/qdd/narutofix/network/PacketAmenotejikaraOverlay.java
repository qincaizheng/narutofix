package com.qdd.narutofix.network;

import com.qdd.narutofix.event.AmenotejikaraOverlayState;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;

public class PacketAmenotejikaraOverlay implements IMessage {
    private boolean active;
    private int durationTicks;

    public PacketAmenotejikaraOverlay() {
    }

    private PacketAmenotejikaraOverlay(boolean active, int durationTicks) {
        this.active = active;
        this.durationTicks = durationTicks;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.active = buf.readBoolean();
        this.durationTicks = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.active);
        buf.writeInt(this.durationTicks);
    }

    public static void activate(EntityPlayerMP player, int durationTicks) {
        PACKET_HANDLER.sendTo(new PacketAmenotejikaraOverlay(true, durationTicks), player);
    }

    public static void deactivate(EntityPlayerMP player) {
        PACKET_HANDLER.sendTo(new PacketAmenotejikaraOverlay(false, 0), player);
    }

    public static class Handler implements IMessageHandler<PacketAmenotejikaraOverlay, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(PacketAmenotejikaraOverlay message, MessageContext ctx) {
            handleClient(message);
            return null;
        }

        @SideOnly(Side.CLIENT)
        private static void handleClient(PacketAmenotejikaraOverlay message) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (message.active) {
                    AmenotejikaraOverlayState.activate(message.durationTicks);
                } else {
                    AmenotejikaraOverlayState.deactivate();
                }
            });
        }
    }
}
