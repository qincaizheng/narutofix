package com.qdd.narutofix.handler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import com.qdd.narutofix.Configs;
import com.qdd.narutofix.awakening.Bloodline;
import com.qdd.narutofix.awakening.PlayerAwakeningActions;
import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.narutomod.Chakra;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemWhiteZetsuFlesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerAwakeningHandler {
    private static final int CHECK_INTERVAL = 20;
    private static final String KEEP_INVENTORY = "keepInventory";
    private static final String KEEP_NINJA_XP = "keepNinjaXp";
    private final Random random = new Random();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) {
            return;
        }

        EntityPlayer player = event.player;
        if (player.ticksExisted % CHECK_INTERVAL != 0) {
            return;
        }

        IPlayerAwakeningData data = PlayerAwakeningDataProvider.get(player);
        if (data == null) {
            return;
        }

        this.tryAwakenInitialBloodline(player, data);
        this.tryAwakenRinnegan(player, data);
    }

    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote || !(entity instanceof EntityPlayerMP)) {
            return;
        }

        if (event.getItem().isEmpty() || event.getItem().getItem() != ItemWhiteZetsuFlesh.block) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) entity;
        IPlayerAwakeningData data = PlayerAwakeningDataProvider.get(player);
        if (data == null || !data.hasExactlyOneBloodline()) {
            return;
        }

        if (!this.roll(Configs.SecondBloodlineAwakenChance)) {
            return;
        }

        Bloodline missingBloodline = data.hasIndra() ? Bloodline.ASURA : Bloodline.INDRA;
        this.unlockBloodline(player, data, missingBloodline);
    }

    private void tryAwakenInitialBloodline(EntityPlayer player, IPlayerAwakeningData data) {
        if (data.hasAnyBloodline()) {
            return;
        }

        if (this.getCurrentChakra(player) < Configs.requiredChakraForBloodlineAwaken) {
            return;
        }

        List<Bloodline> candidates = new ArrayList<>();
        candidates.add(Bloodline.INDRA);
        candidates.add(Bloodline.ASURA);
        Bloodline bloodline = candidates.get(this.random.nextInt(candidates.size()));
        this.unlockBloodline(player, data, bloodline);
    }

    private void tryAwakenRinnegan(EntityPlayer player, IPlayerAwakeningData data) {
        if (data.hasRinneganAwakened() || !DojutsuEyeHelper.hasEffectiveEye(player, ItemMangekyoSharinganEternal.helmet)||!data.hasBothBloodlines()) {
            return;
        }
        if(!this.roll(getAwakenRinneganChance(player))) {
            return;
        }

        PlayerAwakeningActions.awakenRinnegan(player, data);
    }

    private void unlockBloodline(EntityPlayer player, IPlayerAwakeningData data, Bloodline bloodline) {
        PlayerAwakeningActions.unlockBloodline(player, data, bloodline, !data.hasAnyBloodline());
    }

    private double getCurrentChakra(EntityPlayer player) {
        Chakra.PathwayPlayer pathway = Chakra.pathway(player);
        return pathway != null ? pathway.getMax() : 0.0D;
    }

    private boolean isLowHealth(EntityPlayer player) {
        return player.getHealth() > 0.0F && player.getHealth() / player.getMaxHealth() <= Configs.lowHealthThreshold;
    }

    private double getAwakenRinneganChance(EntityPlayer player) {
        if (this.isLowHealth(player)) {
            return Configs.lowHealthRinneganAwakenChance;
        }

        boolean keepInventory = player.world.getGameRules().getBoolean(KEEP_INVENTORY);
        boolean keepNinjaXp = player.world.getGameRules().getBoolean(KEEP_NINJA_XP);
        return !keepInventory && !keepNinjaXp ? Configs.hardcoreRinneganAwakenChance : Configs.RinneganAwakenChance;
    }

    private boolean roll(double chance) {
        return this.random.nextDouble() < chance;
    }
}