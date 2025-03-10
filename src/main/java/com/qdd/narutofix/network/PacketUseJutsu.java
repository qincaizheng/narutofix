package com.qdd.narutofix.network;

import com.qdd.narutofix.Config;
import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.narutomod.PlayerTracker;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemJutsu;
import org.lwjgl.Sys;

public class PacketUseJutsu implements IMessage, IMessageHandler<PacketUseJutsu, IMessage> {
    public PacketUseJutsu() {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(PacketUseJutsu message, MessageContext ctx) {
        ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
            EntityPlayer player = ctx.getServerHandler().player;
            IJutsuInventory inv = player.getCapability(JutsuInventoryCapability.Jutsu_INV, null);
            ItemStack stack = inv.getItems().getStackInSlot(inv.getSelected());
            if (!stack.isEmpty()) {
                int powertick=72000- Config.powertick;
                System.out.println(powertick);
                stack.getItem().onPlayerStoppedUsing(stack, player.world, player,powertick);
            }
        });
        return null;

    }
}
