package com.qdd.narutofix.command;

import com.qdd.narutofix.util.EnergyMath;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandAddChakra extends CommandBase {

    @Override
    public String getName() {
        return "addchakra";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/addchakra <player> <amount> [soulRatio]";
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
        if (args.length < 2 || args.length > 3) {
            throw new WrongUsageException(this.getUsage(sender));
        }

        EntityPlayerMP player = getPlayer(server, sender, args[0]);
        double amount = parseDouble(args[1]);
        if (amount <= 0.0D) {
            throw new CommandException("commands.addchakra.positive", amount);
        }

        double soulRatio = 0.25D;
        if (args.length == 3) {
            soulRatio = parseDouble(args[2]);
            if (soulRatio < 0.0D || soulRatio > 1.0D) {
                throw new CommandException("commands.addchakra.ratio", soulRatio);
            }
        }

        double soulAmount = amount * soulRatio;
        double bodyAmount = amount - soulAmount;

        EnergyMath.addBodyAndSoul(player, soulAmount, bodyAmount);

        sender.sendMessage(new TextComponentTranslation("narutofix.command.addchakra.done",
                String.format("%.1f", soulAmount),
                String.format("%.1f", bodyAmount)));
    }
}
