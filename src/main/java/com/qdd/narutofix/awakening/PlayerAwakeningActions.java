package com.qdd.narutofix.awakening;

import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.util.AdvancementHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.ItemHandlerHelper;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemRinnegan;
import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;

import java.util.Random;

public final class PlayerAwakeningActions {
    private static final Random RANDOM = new Random();

    private PlayerAwakeningActions() {
    }

    public static boolean unlockBloodline(EntityPlayer player, IPlayerAwakeningData data, Bloodline bloodline, boolean reincarnation) {
        if (data.hasBloodline(bloodline)) {
            return false;
        }

        data.setBloodline(bloodline, true);
        if (player instanceof EntityPlayerMP) {
            AdvancementHelper.grant((EntityPlayerMP) player, BloodlineAdvancementIds.forBloodline(bloodline, reincarnation));
        }

        String translationKey = bloodline == Bloodline.INDRA
                ? reincarnation ? "message.narutofix.indra_reincarnation" : "message.narutofix.indra_awakened"
                : reincarnation ? "message.narutofix.asura_reincarnation" : "message.narutofix.asura_awakened";
        notify(player, translationKey);
        playAwakeningSound(player);
        return true;
    }

    public static boolean awakenRinnegan(EntityPlayer player, IPlayerAwakeningData data) {
        if (data.hasRinneganAwakened()) {
            return false;
        }

        data.setRinneganAwakened(true);
        ItemStack rinnegan = createRinneganReward(player, data);
        ItemHandlerHelper.giveItemToPlayer(player, rinnegan);

        if (player instanceof EntityPlayerMP) {
            AdvancementHelper.grant((EntityPlayerMP) player, "narutomod:rinneganawakened");
        }

        notify(player, rinnegan.getItem() == ModItems.SIX_TOMOE_RINNEGAN
                ? "message.narutofix.rinnegan_awakened_six_tomoe"
                : "message.narutofix.rinnegan_awakened");
        playAwakeningSound(player);
        return true;
    }

    private static ItemStack createRinneganReward(EntityPlayer player, IPlayerAwakeningData data) {
        ItemStack stack = RANDOM.nextInt(3) == 0 && data.hasIndra()
                ? new ItemStack(ModItems.SIX_TOMOE_RINNEGAN)
                : new ItemStack(ItemRinnegan.helmet);
        if (stack.getItem() instanceof ItemDojutsu.Base) {
            ((ItemDojutsu.Base) stack.getItem()).setOwner(stack, player);
        }
        return stack;
    }

    private static void notify(EntityPlayer player, String translationKey) {
        player.sendStatusMessage(new TextComponentTranslation(translationKey), false);
    }

    private static void playAwakeningSound(EntityPlayer player) {
        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }
}