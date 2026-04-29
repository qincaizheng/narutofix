package com.qdd.narutofix.handler;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.awakening.Bloodline;
import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import com.qdd.narutofix.cap.body.BodyEnergyDataProvider;
import com.qdd.narutofix.cap.body.IBodyEnergyData;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import com.qdd.narutofix.network.PacketSyncBodyEnergy;
import com.qdd.narutofix.network.PacketSyncChakra;
import com.qdd.narutofix.network.PacketSyncSoulEnergy;
import com.qdd.narutofix.util.ChakraSyncHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.narutomod.Chakra;
import net.narutomod.item.ItemMilitaryRationsPill;
import net.narutomod.item.ItemMilitaryRationsPillGold;

public class EnergyStateHandler {
    private static final int EFFECT_DURATION = 80;
    private static final double STATIONARY_DISTANCE_SQ = 1.0E-4D;
    private static final String LAST_X = "narutofixEnergyLastX";
    private static final String LAST_Y = "narutofixEnergyLastY";
    private static final String LAST_Z = "narutofixEnergyLastZ";
    private static final String POSITION_INITIALIZED = "narutofixEnergyPositionInitialized";
    private static final String STATIONARY_TICKS = "narutofixEnergyStationaryTicks";
    private static final String BODY_FOOD_DEBT = "narutofixBodyFoodDebt";
    private static final String INDRA_INITIAL_APPLIED = "narutofixIndraInitialSoulApplied";
    private static final String ASURA_INITIAL_APPLIED = "narutofixAsuraInitialBodyApplied";

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote || !(event.player instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        ISoulEnergyData soul = SoulEnergyDataProvider.get(player);
        IBodyEnergyData body = BodyEnergyDataProvider.get(player);
        IPlayerAwakeningData awakening = PlayerAwakeningDataProvider.get(player);
        if (soul == null || body == null) {
            return;
        }

        this.applyBloodlineInitialEnergy(player, awakening, soul, body);
        int stationaryTicks = this.updateStationaryTicks(player);
        this.handleLowSoul(player, awakening, soul, stationaryTicks);
        this.handleLowBody(player, awakening, body);
        this.handleLowChakra(player, soul, body);
    }

    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote || !(entity instanceof EntityPlayerMP) || event.getItem().isEmpty()) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) entity;
        Item item = event.getItem().getItem();
        double restore = 0.0D;
        if (item == ItemMilitaryRationsPill.block) {
            restore = Configs.body.militaryRationsBodyRestore;
        } else if (item == ItemMilitaryRationsPillGold.block) {
            restore = Configs.body.goldMilitaryRationsBodyRestore;
        }

        if (restore <= 0.0D) {
            return;
        }

        IBodyEnergyData body = BodyEnergyDataProvider.get(player);
        if (body == null || body.getCurrent() >= body.getMax()) {
            return;
        }

        body.addCurrent(restore);
        PacketSyncBodyEnergy.sync(player);
        ChakraSyncHelper.refresh(player);
    }

    private void applyBloodlineInitialEnergy(EntityPlayerMP player, IPlayerAwakeningData awakening, ISoulEnergyData soul, IBodyEnergyData body) {
        if (awakening == null) {
            return;
        }

        NBTTagCompound persistent = this.getPersistentData(player);
        if (awakening.hasBloodline(Bloodline.INDRA) && !persistent.getBoolean(INDRA_INITIAL_APPLIED)) {
            double targetMax = Configs.soul.soulInitialMax * Configs.soul.indraInitialSoulMultiplier;
            double targetCurrent = Configs.soul.soulInitialCurrent * Configs.soul.indraInitialSoulMultiplier;
            if (soul.getMax() < targetMax) {
                soul.setMax(targetMax);
            }
            if (soul.getCurrent() < targetCurrent) {
                soul.setCurrent(targetCurrent);
            }
            persistent.setBoolean(INDRA_INITIAL_APPLIED, true);
            PacketSyncSoulEnergy.sync(player);
            ChakraSyncHelper.refresh(player);
        }

        if (awakening.hasBloodline(Bloodline.ASURA) && !persistent.getBoolean(ASURA_INITIAL_APPLIED)) {
            double targetMax = Configs.body.bodyInitialMax * Configs.body.asuraInitialBodyMultiplier;
            double targetCurrent = Configs.body.bodyInitialCurrent * Configs.body.asuraInitialBodyMultiplier;
            if (body.getMax() < targetMax) {
                body.setMax(targetMax);
            }
            if (body.getCurrent() < targetCurrent) {
                body.setCurrent(targetCurrent);
            }
            persistent.setBoolean(ASURA_INITIAL_APPLIED, true);
            PacketSyncBodyEnergy.sync(player);
            ChakraSyncHelper.refresh(player);
        }
    }

    private void handleLowSoul(EntityPlayerMP player, IPlayerAwakeningData awakening, ISoulEnergyData soul, int stationaryTicks) {
        if (!this.isBelowRatio(soul.getCurrent(), soul.getMax(), Configs.soul.soulLowThreshold)) {
            return;
        }

        player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, EFFECT_DURATION, 0, false, false));
        double restore = 0.0D;
        if (player.isPlayerSleeping()) {
            restore = Configs.soul.soulSleepRecoveryPerTick;
        } else if (stationaryTicks >= Configs.soul.soulIdleRequiredTicks) {
            restore = Configs.soul.soulIdleRecoveryPerTick;
        }

        if (awakening != null && awakening.hasBloodline(Bloodline.INDRA)) {
            restore *= Configs.soul.indraSoulRecoveryMultiplier;
        }

        if (restore > 0.0D && soul.getCurrent() < soul.getMax()) {
            soul.addCurrent(restore);
            PacketSyncSoulEnergy.sync(player);
            ChakraSyncHelper.refresh(player);
        }
    }

    private void handleLowBody(EntityPlayerMP player, IPlayerAwakeningData awakening, IBodyEnergyData body) {
        if (!this.isBelowRatio(body.getCurrent(), body.getMax(), Configs.body.bodyLowThreshold)) {
            return;
        }

        player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, EFFECT_DURATION, 0, false, false));
        player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, EFFECT_DURATION, 0, false, false));
        player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, EFFECT_DURATION, 0, false, false));

        double multiplier = awakening != null && awakening.hasBloodline(Bloodline.ASURA)
                ? Configs.body.asuraBodyRecoveryMultiplier : 1.0D;
        double conversion = Configs.body.foodToBodyRate * multiplier;
        if (conversion <= 0.0D || body.getCurrent() >= body.getMax()) {
            return;
        }

        double neededFood = (body.getMax() - body.getCurrent()) / conversion;
        double requestedFood = Math.min(Configs.body.bodyFoodCostPerTick, neededFood);
        double consumedFood = this.consumeFood(player, requestedFood);
        if (consumedFood > 0.0D) {
            body.addCurrent(consumedFood * conversion);
            PacketSyncBodyEnergy.sync(player);
            ChakraSyncHelper.refresh(player);
        }
    }

    private void handleLowChakra(EntityPlayerMP player, ISoulEnergyData soul, IBodyEnergyData body) {
        if (!Chakra.isInitialized(player)) {
            return;
        }

        Chakra.Pathway pathway = Chakra.pathway(player);
        if (pathway == null || !this.isBelowRatio(pathway.getAmount(), pathway.getMax(), Configs.chakraEmergency.chakraEmergencyThreshold)) {
            return;
        }

        double rateSum = Configs.chakraEmergency.soulToChakraRate + Configs.chakraEmergency.bodyToChakraRate;
        if (rateSum <= 0.0D || Configs.chakraEmergency.chakraEmergencyEnergyCostPerTick <= 0.0D) {
            return;
        }

        double targetChakra = pathway.getMax() * Configs.chakraEmergency.chakraEmergencyThreshold;
        double neededChakra = Math.max(0.0D, targetChakra - pathway.getAmount());
        double costNeeded = neededChakra / rateSum;
        double energyCost = Math.min(Configs.chakraEmergency.chakraEmergencyEnergyCostPerTick, costNeeded);
        energyCost = Math.min(energyCost, Math.min(soul.getCurrent(), body.getCurrent()));
        if (energyCost <= 0.0D) {
            return;
        }

        soul.addCurrent(-energyCost);
        body.addCurrent(-energyCost);
        pathway.consume(-(energyCost * rateSum));
        PacketSyncSoulEnergy.sync(player);
        PacketSyncBodyEnergy.sync(player);
        PacketSyncChakra.sync(player);
    }

    private int updateStationaryTicks(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.getBoolean(POSITION_INITIALIZED)) {
            data.setBoolean(POSITION_INITIALIZED, true);
            data.setDouble(LAST_X, player.posX);
            data.setDouble(LAST_Y, player.posY);
            data.setDouble(LAST_Z, player.posZ);
            data.setInteger(STATIONARY_TICKS, 0);
            return 0;
        }

        double dx = player.posX - data.getDouble(LAST_X);
        double dy = player.posY - data.getDouble(LAST_Y);
        double dz = player.posZ - data.getDouble(LAST_Z);
        int ticks = dx * dx + dy * dy + dz * dz <= STATIONARY_DISTANCE_SQ
                ? data.getInteger(STATIONARY_TICKS) + 1 : 0;
        data.setDouble(LAST_X, player.posX);
        data.setDouble(LAST_Y, player.posY);
        data.setDouble(LAST_Z, player.posZ);
        data.setInteger(STATIONARY_TICKS, ticks);
        return ticks;
    }

    private double consumeFood(EntityPlayerMP player, double amount) {
        if (amount <= 0.0D || player.capabilities.isCreativeMode) {
            return 0.0D;
        }

        FoodStats stats = player.getFoodStats();
        double consumed = 0.0D;
        double remaining = amount;
        float saturation = stats.getSaturationLevel();
        if (saturation > 0.0F) {
            float saturationCost = (float) Math.min(saturation, remaining);
            stats.setFoodSaturationLevel(saturation - saturationCost);
            consumed += saturationCost;
            remaining -= saturationCost;
        }

        if (remaining > 0.0D) {
            NBTTagCompound data = player.getEntityData();
            double debt = data.getDouble(BODY_FOOD_DEBT) + remaining;
            int food = stats.getFoodLevel();
            int foodCost = Math.min(food, (int) Math.floor(debt));
            if (foodCost > 0) {
                stats.setFoodLevel(food - foodCost);
                debt -= foodCost;
                consumed += foodCost;
            } else if (food <= 0) {
                debt = Math.min(debt, 1.0D);
            }
            data.setDouble(BODY_FOOD_DEBT, debt);
        }

        return consumed;
    }

    private boolean isBelowRatio(double current, double max, double ratio) {
        return ratio > 0.0D && max > 0.0D && current / max < ratio;
    }

    private NBTTagCompound getPersistentData(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }
}
