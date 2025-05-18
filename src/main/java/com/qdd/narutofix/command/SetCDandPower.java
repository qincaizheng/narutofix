package com.qdd.narutofix.command;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.narutomod.entity.EntitySusanooBase;

import java.util.Objects;

import static com.qdd.narutofix.cap.JutsuInventoryCapability.Jutsu_INV;

public class SetCDandPower extends CommandBase {
    @Override
    public String getName() {
        return "narutofixset";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return I18n.format( "narutofix.command.narutofixset");
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException {
        if (strings.length!=3)return;
        Entity player = EntitySelector.matchOneEntity(iCommandSender,strings[0],EntityPlayer.class);
        if(player==null){
             player=minecraftServer.getPlayerList().getPlayerByUsername(strings[0]);
        }
        if(player.hasCapability(Jutsu_INV,null)){
            if(strings[1].equals( "power")){
                player.getCapability(Jutsu_INV,null).setPower(Integer.parseInt(strings[2]));
            } else if (strings[1].equals("cd")) {
                player.getCapability(Jutsu_INV,null).setCd(Integer.parseInt(strings[2]));
            }

        }

    }
    @Override
    public int getRequiredPermissionLevel() {
        return 4; // 0 代表任何人都能用
    }

}
