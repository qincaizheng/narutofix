package com.qdd.narutofix.command;

import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandGetDojutsuState extends CommandBase {
    @Override
    public String getName() {
        return "getdojutsustate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/getdojutsustate [player]";
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
        IPlayerAwakeningData data = PlayerAwakeningDataProvider.get(player);
        if (data == null) {
            throw new CommandException("No awakening data found for target player.");
        }

        sender.sendMessage(new TextComponentString("Player: " + player.getName()));
        sender.sendMessage(new TextComponentString("Indra: " + data.hasIndra()));
        sender.sendMessage(new TextComponentString("Asura: " + data.hasAsura()));
        sender.sendMessage(new TextComponentString("Rinnegan awakened: " + data.hasRinneganAwakened()));
        sender.sendMessage(new TextComponentString("Equipped eye: " + data.getEquippedEye().getDisplayName()));
        sender.sendMessage(new TextComponentString("Stored eyes: " + countStoredEyes(data) + "/" + data.getStoredEyeSlotCount()));
    }

    private static int countStoredEyes(IPlayerAwakeningData data) {
        int count = 0;
        for (int index = 0; index < data.getStoredEyeSlotCount(); index++) {
            if (!data.getStoredEyes().get(index).isEmpty()) {
                count++;
            }
        }
        return count;
    }
}