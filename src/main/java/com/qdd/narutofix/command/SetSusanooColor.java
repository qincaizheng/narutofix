package com.qdd.narutofix.command;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.server.MinecraftServer;
import net.narutomod.item.ItemSharingan;

public class SetSusanooColor extends CommandBase {

    public static int color=539760701;

    @Override
    public String getName() {
        return "setcolor";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "/setcolor <color>";
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException{
        if(iCommandSender.getCommandSenderEntity() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) iCommandSender.getCommandSenderEntity();
            if(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemSharingan.Base) {
                color=Integer.parseInt(strings[0],16);
                ((ItemSharingan.Base)ItemSharingan.helmet).setColor(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD),color);
            }
        }
    }
    @Override
    public int getRequiredPermissionLevel() {
        return 0; // 0 代表任何人都能用
    }

}
