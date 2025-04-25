package com.qdd.narutofix.command;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.narutomod.entity.EntitySusanooBase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SetSusanooColor extends CommandBase {

    public static int color=539760701;

    @Override
    public String getName() {
        return "setcolor";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return I18n.format( "narutofix.command.setcolor");
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException{
        if(iCommandSender.getCommandSenderEntity() instanceof EntityPlayer){
            Entity player = iCommandSender.getCommandSenderEntity();
            if(player.getRidingEntity() instanceof EntitySusanooBase) {
                color=Integer.parseInt(strings[0],16);
                ((EntitySusanooBase) player.getRidingEntity()).killBullet();
                ((EntitySusanooBase) player.getRidingEntity()).createBullet(player.getRidingEntity().height);
            }
        }
    }
    @Override
    public int getRequiredPermissionLevel() {
        return 0; // 0 代表任何人都能用
    }

}
