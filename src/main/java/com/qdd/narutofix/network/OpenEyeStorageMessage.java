package com.qdd.narutofix.network;

import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.cap.eye.EyeInventoryConstants;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OpenEyeStorageMessage implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<OpenEyeStorageMessage, IMessage> {
        @Override
        public IMessage onMessage(OpenEyeStorageMessage message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> player.openGui(NarutoFix.instance, EyeInventoryConstants.STORAGE_GUI_ID, player.world, 0, 0, 0));
            return null;
        }
    }
}