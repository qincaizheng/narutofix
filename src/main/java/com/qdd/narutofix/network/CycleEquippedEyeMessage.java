package com.qdd.narutofix.network;

import com.qdd.narutofix.cap.eye.EyeInventoryManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CycleEquippedEyeMessage implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<CycleEquippedEyeMessage, IMessage> {
        @Override
        public IMessage onMessage(CycleEquippedEyeMessage message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> EyeInventoryManager.cycleEquippedEye(player));
            return null;
        }
    }
}