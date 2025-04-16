package com.qdd.narutofix.command;

import com.qdd.narutofix.world.modWorldProvider;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class ToNinjaRealm extends CommandBase {

    @Override
    public String getName() {
        return "toninjarealm";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "toninjarealm";
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException {
        if(iCommandSender.getCommandSenderEntity() instanceof EntityPlayer){
            Entity player = iCommandSender.getCommandSenderEntity();
            WorldServer targetWorld = player.getServer().getWorld(modWorldProvider.DIMID);
            if(player.dimension!= modWorldProvider.DIMID){
                player.changeDimension(modWorldProvider.DIMID, new SimpleTeleporter(targetWorld) );
            } else {
                player.changeDimension(0, new SimpleTeleporter(player.getServer().getWorld(0)) );
            }
        }
    }
    @Override
    public int getRequiredPermissionLevel() {
        return 4; // 0 代表任何人都能用
    }

    /** 使用最简单的传送逻辑（不处理出生点） */
    private static class SimpleTeleporter extends Teleporter {
        public SimpleTeleporter(WorldServer world) {
            super(world);
        }
        @Override
        public void placeInPortal(Entity entity, float rotationYaw) {

        }

    }

}