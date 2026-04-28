package com.qdd.narutofix.command;

import com.qdd.narutofix.awakening.Bloodline;
import com.qdd.narutofix.awakening.BloodlineAdvancementIds;
import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import com.qdd.narutofix.util.AdvancementHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.narutomod.entity.EntitySusanooBase;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandClearDojutsuState extends CommandBase {
    @Override
    public String getName() {
        return "cleardojutsustate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/cleardojutsustate [player]";
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

        // Clear bloodline states
        data.setBloodline(Bloodline.INDRA, false);
        data.setBloodline(Bloodline.ASURA, false);

        data.setRinneganAwakened(false);

//        // Clear equipped eye and stored eyes
//        data.setEquippedEye(ItemStack.EMPTY);
//        for (int i = 0; i < data.getStoredEyeSlotCount(); i++) {
//            data.setStoredEye(i, ItemStack.EMPTY);
//        }

        // Clean up active susanoo
        cleanupSusanoo(player);

        // Clear ability entity data tags
        player.getEntityData().removeTag("amaterasu_active");
        player.getEntityData().removeTag("amaterasu_cd");
        player.getEntityData().removeTag("kamui_teleport");
        player.getEntityData().removeTag("six_tomoe_amenotejikara_cd");
        player.getEntityData().removeTag("six_tomoe_amenotejikara_overlay_until");
        player.getEntityData().removeTag("six_tomoe_amenotejikara_visual_until");
        player.getEntityData().removeTag("six_tomoe_genjutsu_cd");
        player.getEntityData().removeTag("lastWornForeignDojutsu");
        player.getEntityData().removeTag("lastWornForeignDojutsuMost");
        player.getEntityData().removeTag("lastWornForeignDojutsuLeast");

        // Revoke dojutsu-related advancements
        revokeAdvancements(player);

        sender.sendMessage(new TextComponentString("Cleared all dojutsu state for " + player.getName() + "."));
    }

    private static final String[] DOJUTSU_ADVANCEMENTS = {
            BloodlineAdvancementIds.ASURA_REINCARNATION,
            BloodlineAdvancementIds.INDRA_REINCARNATION,
            BloodlineAdvancementIds.GET_ASURA_CHAKRA,
            BloodlineAdvancementIds.GET_INDRA_CHAKRA,
    };

    private static void revokeAdvancements(EntityPlayerMP player) {
        for (String id : DOJUTSU_ADVANCEMENTS) {
            AdvancementHelper.revoke(player, id);
        }
    }

    private static void cleanupSusanoo(EntityPlayerMP player) {
        if (!player.getEntityData().getBoolean("susanoo_activated")) {
            return;
        }

        Entity susanoo = player.world.getEntityByID(
                player.getEntityData().getInteger("summonedSusanooID"));
        player.getEntityData().removeTag("susanoo_activated");
        player.getEntityData().removeTag("susanoo_ticks");
        player.getEntityData().removeTag("summonedSusanooID");

        if (susanoo != null) {
            susanoo.setDead();
        }
        if (player.isRiding() && player.getRidingEntity() instanceof EntitySusanooBase) {
            player.dismountRidingEntity();
        }
    }
}
