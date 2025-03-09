package com.qdd.narutofix.network;

import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.narutomod.PlayerTracker;

import static com.qdd.narutofix.handler.GuiElementLoader.GUI_Jutsu;

public class PacketOpenJutsugui implements IMessage, IMessageHandler<PacketOpenJutsugui, IMessage> {

    private int selected;
    public PacketOpenJutsugui() {}


    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(PacketOpenJutsugui message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
        mainThread.addScheduledTask(new Runnable(){ public void run() {
            if(player.hasCapability(JutsuInventoryCapability.Jutsu_INV, null)&& PlayerTracker.isNinja(player)) {
                player.getCapability(JutsuInventoryCapability.Jutsu_INV, null).setIzanagiSize((int) Math.min(PlayerTracker.getBattleXp(player)/2000,9));
            }
            ctx.getServerHandler().player.openGui(NarutoFix.instance, GUI_Jutsu, ctx.getServerHandler().player.world, 0, 0, 0);
        }});
        return null;

    }
}