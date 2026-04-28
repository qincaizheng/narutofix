package com.qdd.narutofix.command;

import com.qdd.narutofix.awakening.Bloodline;
import com.qdd.narutofix.awakening.PlayerAwakeningActions;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandAwakeKekkeiGenkai extends CommandBase {
    private static final List<String> MODES = Arrays.asList("indra", "asura", "both", "rinnegan", "next");

    @Override
    public String getName() {
        return "awakekekkeigenkai";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/awakekekkeigenkai <indra|asura|both|rinnegan|next> [player]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, MODES);
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1 || args.length > 2) {
            throw new WrongUsageException(this.getUsage(sender));
        }

        String mode = args[0].toLowerCase();
        EntityPlayerMP player = args.length == 2 ? getPlayer(server, sender, args[1]) : getCommandSenderAsPlayer(sender);
        IPlayerAwakeningData data = PlayerAwakeningDataProvider.get(player);
        if (data == null) {
            throw new CommandException("No awakening data found for target player.");
        }

        boolean changed;
        switch (mode) {
            case "indra":
                changed = PlayerAwakeningActions.unlockBloodline(player, data, Bloodline.INDRA, false);
                break;
            case "asura":
                changed = PlayerAwakeningActions.unlockBloodline(player, data, Bloodline.ASURA, false);
                break;
            case "both":
                changed = PlayerAwakeningActions.unlockBloodline(player, data, Bloodline.INDRA, false);
                changed |= PlayerAwakeningActions.unlockBloodline(player, data, Bloodline.ASURA, true);
                break;
            case "rinnegan":
                changed = PlayerAwakeningActions.awakenRinnegan(player, data);
                break;
            case "next":
                changed = awakenNextStage(player, data);
                break;
            default:
                throw new WrongUsageException(this.getUsage(sender));
        }

        sender.sendMessage(new TextComponentString(changed
                ? "Awakening updated for " + player.getName() + "."
                : player.getName() + " already has that state."));
    }

    private static boolean awakenNextStage(EntityPlayerMP player, IPlayerAwakeningData data) {
        if (!data.hasIndra()) {
            return PlayerAwakeningActions.unlockBloodline(player, data, Bloodline.INDRA, false);
        }
        if (!data.hasAsura()) {
            return PlayerAwakeningActions.unlockBloodline(player, data, Bloodline.ASURA, true);
        }
        return PlayerAwakeningActions.awakenRinnegan(player, data);
    }
}