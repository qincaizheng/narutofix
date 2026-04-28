package com.qdd.narutofix.command;

import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.cap.eye.EyeInventoryConstants;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandOpenWheelMenu extends CommandBase {
    @Override
    public String getName() {
        return "openwheel";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/openwheel [player]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            throw new WrongUsageException(this.getUsage(sender));
        }

        EntityPlayerMP player = args.length == 1 ? getPlayer(server, sender, args[0]) : getCommandSenderAsPlayer(sender);
        player.openGui(NarutoFix.instance, EyeInventoryConstants.STORAGE_GUI_ID, player.world, 0, 0, 0);
    }
}