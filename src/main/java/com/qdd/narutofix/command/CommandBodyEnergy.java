package com.qdd.narutofix.command;

import com.qdd.narutofix.cap.body.BodyEnergyDataProvider;
import com.qdd.narutofix.cap.body.IBodyEnergyData;
import com.qdd.narutofix.handler.NinjaXpConversionHandler;
import com.qdd.narutofix.network.PacketSyncBodyEnergy;
import com.qdd.narutofix.util.ChakraSyncHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandBodyEnergy extends CommandBase {
    private static final List<String> ACTIONS = Arrays.asList("get", "set", "add");
    private static final List<String> FIELDS = Arrays.asList("current", "max");

    @Override
    public String getName() {
        return "bodyenergy";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/bodyenergy <player> <get|set|add> <current|max> [value]";
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
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, ACTIONS);
        }
        if (args.length == 3) {
            return getListOfStringsMatchingLastWord(args, FIELDS);
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3 || args.length > 4) {
            throw new WrongUsageException(this.getUsage(sender));
        }

        EntityPlayerMP player = getPlayer(server, sender, args[0]);
        IBodyEnergyData data = BodyEnergyDataProvider.get(player);
        if (data == null) {
            throw new CommandException("No body energy data found for target player.");
        }

        String action = args[1].toLowerCase();
        String field = args[2].toLowerCase();
        if (!FIELDS.contains(field) || !ACTIONS.contains(action)) {
            throw new WrongUsageException(this.getUsage(sender));
        }

        if ("get".equals(action)) {
            sender.sendMessage(new TextComponentString(player.getName() + " body " + field + " = " + getValue(data, field)));
            return;
        }
        if (args.length != 4) {
            throw new WrongUsageException(this.getUsage(sender));
        }

        double value = parseDouble(args[3]);
        if ("set".equals(action)) {
            setValue(data, field, value);
        } else {
            addValue(data, field, value);
        }
        NinjaXpConversionHandler.resetBaseline(player);
        PacketSyncBodyEnergy.sync(player);
        ChakraSyncHelper.refresh(player);
        sender.sendMessage(new TextComponentString(player.getName() + " body = " + (int) data.getCurrent() + "/" + (int) data.getMax()));
    }

    private static double getValue(IBodyEnergyData data, String field) {
        return "max".equals(field) ? data.getMax() : data.getCurrent();
    }

    private static void setValue(IBodyEnergyData data, String field, double value) {
        if ("max".equals(field)) {
            data.setMax(value);
        } else {
            data.setCurrent(value);
        }
    }

    private static void addValue(IBodyEnergyData data, String field, double value) {
        if ("max".equals(field)) {
            data.addMax(value);
        } else {
            data.addCurrent(value);
        }
    }
}
