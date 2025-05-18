package com.qdd.narutofix.network;

import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.item.ItemJutsu;

public class PacketSwitchNextJutsu implements IMessage, IMessageHandler<PacketSwitchNextJutsu, IMessage> {
    public PacketSwitchNextJutsu() {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(PacketSwitchNextJutsu message, MessageContext ctx) {
        ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
            EntityPlayer player = ctx.getServerHandler().player;
            IJutsuInventory inv = player.getCapability(JutsuInventoryCapability.Jutsu_INV, null);
            ItemStack stack = inv.getItems().getStackInSlot(inv.getSelected());
            if (!stack.isEmpty()) {
                System.out.println(1);
                ItemJutsu.Base.switchNextJutsu(stack, player);
            }
        });
        return null;

    }
}