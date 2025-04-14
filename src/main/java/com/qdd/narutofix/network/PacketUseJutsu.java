package com.qdd.narutofix.network;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.narutomod.PlayerTracker;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemJutsu;
import org.lwjgl.Sys;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
                int powertick= 72000- Configs.powertick;
                Class<?> clazz = ItemJutsu.Base.class;
                try {
                    Method method = clazz.getDeclaredMethod("getCurrentJutsu", ItemStack.class);
                    method.setAccessible(true);
                    Long cd =stack.getTagCompound().getLong("JutsuCDMapKey" + ((ItemJutsu.JutsuEnum) method.invoke(stack.getItem(),stack)).index);
//                    System.out.println(Configs.powertick);
                    if (cd<player.world.getTotalWorldTime()) {
                        stack.getItem().onPlayerStoppedUsing(stack, player.world, player,powertick);
                        if(Configs.cooldown<0){
                            ((ItemJutsu.Base) stack.getItem()).setCurrentJutsuCooldown(stack, Configs.powertick);
                        }else{
                            ((ItemJutsu.Base) stack.getItem()).setCurrentJutsuCooldown(stack, Configs.cooldown);
                        }
                    } else {
                        player.sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted", new Object[]{(cd - player.world.getTotalWorldTime()) / 20L}), true);
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return null;

    }
}
