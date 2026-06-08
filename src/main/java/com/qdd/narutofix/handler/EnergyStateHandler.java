package com.qdd.narutofix.handler;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import com.qdd.narutofix.cap.body.BodyEnergyDataProvider;
import com.qdd.narutofix.cap.body.IBodyEnergyData;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import com.qdd.narutofix.network.PacketSyncBodyEnergy;
import com.qdd.narutofix.network.PacketSyncSoulEnergy;
import com.qdd.narutofix.util.BloodlineEnergyBonusApplier;
import com.qdd.narutofix.util.ChakraSyncHelper;
import com.qdd.narutofix.util.EnergyRecoveryCalculator;
import com.qdd.narutofix.util.EnergyRecoverySnapshot;
import com.qdd.narutofix.util.SleepRecoveryRules;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
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
    private static final String SLEEP_RECOVERY_ACTIVE = "narutofixSleepRecoveryActive";
    private static final String SLEEP_RECOVERY_STARTED_AT_NIGHT = "narutofixSleepRecoveryStartedAtNight";
    private static final String SLEEP_RECOVERY_START_WORLD_TIME = "narutofixSleepRecoveryStartWorldTime";

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

        this.applyBloodlineInitialEnergy(player);
        int stationaryTicks = this.updateStationaryTicks(player);
        this.trackSleepRecoverySession(player);
        Chakra.Pathway<?> pathway = Chakra.pathway(player);
        EnergyRecoverySnapshot snapshot = EnergyRecoveryCalculator.calculate(player, awakening, soul, body, pathway, stationaryTicks);

        this.handleLowSoul(player, soul, snapshot);
        this.handleLowBody(player, body, snapshot);
        this.handleLowChakra(player, soul, body, pathway, snapshot);
        // P1: Crouch stationary exchange — execute if conditions met
        this.handleCrouchExchange(player, soul, body, pathway, snapshot);
        // P3: Sleep recovery detection
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

    /**
     * 血脉初始能量兜底对齐。
     */
    private void applyBloodlineInitialEnergy(EntityPlayerMP player) {
        BloodlineEnergyBonusApplier.applyFallback(player);
    }

    private void handleLowSoul(EntityPlayerMP player, ISoulEnergyData soul, EnergyRecoverySnapshot snapshot) {
        if (snapshot.getSoul().isLow()) {
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, EFFECT_DURATION, 0, false, false));
            double restore = snapshot.getSoul().getRecoveryPerTick();
            if (restore > 0.0D) {
                soul.addCurrent(restore);
                PacketSyncSoulEnergy.sync(player);
                ChakraSyncHelper.refresh(player);
            }
        }
    }

    private void handleLowBody(EntityPlayerMP player, IBodyEnergyData body, EnergyRecoverySnapshot snapshot) {
        if (snapshot.getBody().isLow()) {
            player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, EFFECT_DURATION, 0, false, false));
            player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, EFFECT_DURATION, 0, false, false));
            player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, EFFECT_DURATION, 0, false, false));
        }

        double restore = snapshot.getBody().getRecoveryPerTick();
        if (restore > 0.0D) {
            this.applyFoodConsumption(player, snapshot);
            body.addCurrent(restore);
            PacketSyncBodyEnergy.sync(player);
            ChakraSyncHelper.refresh(player);
        }

        // P2: Always write back food debt even when restore=0, so debt doesn't accumulate invisibly
        if (!player.capabilities.isCreativeMode) {
            player.getEntityData().setDouble(EnergyRecoveryCalculator.BODY_FOOD_DEBT, snapshot.getBodyFoodDebtAfter());
        }
    }

    private void handleLowChakra(EntityPlayerMP player, ISoulEnergyData soul, IBodyEnergyData body,
                                 Chakra.Pathway<?> pathway, EnergyRecoverySnapshot snapshot) {
        if (pathway == null) {
            return;
        }

        // P0: Emergency recovery only (chakra low) — consume soul/body
        if (snapshot.getChakra().isLow()) {
            double energyCost = snapshot.getChakraEnergyCostPerTick();
            // P0: Use dedicated emergency recovery field, NOT getChakra().getRecoveryPerTick()
            // which may include crouch exchange display values.
            double emergencyRecovery = snapshot.getChakraEmergencyRecoveryPerTick();
            if (energyCost > 0.0D) {
                soul.addCurrent(-energyCost);
                body.addCurrent(-energyCost);
                pathway.consume(-emergencyRecovery);
                PacketSyncSoulEnergy.sync(player);
                PacketSyncBodyEnergy.sync(player);
                ChakraSyncHelper.refresh(player);
            }
        }
        // P0: No free stationary natural recovery. The crouch exchange (P1) replaces it.
    }

    /**
     * P1: Execute crouch stationary chakra exchange.
     */
    private void handleCrouchExchange(EntityPlayerMP player, ISoulEnergyData soul, IBodyEnergyData body,
                                      Chakra.Pathway<?> pathway, EnergyRecoverySnapshot snapshot) {
        if (pathway == null) {
            return;
        }
        // Use triggered (not eligible) — only execute on interval ticks
        if (!snapshot.isCrouchExchangeTriggered()) {
            return;
        }

        double soulCost = snapshot.getCrouchExchangeSoulCost();
        double bodyCost = snapshot.getCrouchExchangeBodyCost();
        double chakraGain = snapshot.getCrouchExchangeChakraGain();

        if (soulCost <= 0.0D && bodyCost <= 0.0D) {
            return;
        }

        // Verify we still have enough energy at execution time
        double availableSoul = soul != null ? soul.getCurrent() : 0.0D;
        double availableBody = body != null ? body.getCurrent() : 0.0D;
        if (availableSoul < soulCost || availableBody < bodyCost) {
            return;
        }

        if (soul != null) soul.addCurrent(-soulCost);
        if (body != null) body.addCurrent(-bodyCost);
        pathway.consume(-chakraGain);

        PacketSyncSoulEnergy.sync(player);
        PacketSyncBodyEnergy.sync(player);
        ChakraSyncHelper.refresh(player);
    }

    @SubscribeEvent
    public void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote || !(player instanceof EntityPlayerMP)) {
            return;
        }

        if (!player.world.isDaytime()) {
            this.recordSleepRecoverySession(player, true);
        }
    }

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote || !(player instanceof EntityPlayerMP)) {
            return;
        }

        NBTTagCompound data = player.getEntityData();
        boolean hadSleepSession = data.getBoolean(SLEEP_RECOVERY_ACTIVE);
        boolean startedAtNight = data.getBoolean(SLEEP_RECOVERY_STARTED_AT_NIGHT);
        long startWorldTime = data.getLong(SLEEP_RECOVERY_START_WORLD_TIME);
        this.clearSleepRecoverySession(data);

        long currentWorldTime = player.world.getWorldTime();
        if (!hadSleepSession || !SleepRecoveryRules.shouldApplyRecovery(startedAtNight,
                SleepRecoveryRules.isVanillaDaytime(currentWorldTime),
                startWorldTime, currentWorldTime)) {
            return;
        }

        int effectiveSleepTicks = SleepRecoveryRules.effectiveSleepTicks(startWorldTime, currentWorldTime,
                Configs.sleep.maxRecoveryTicks);
        if (effectiveSleepTicks <= 0) {
            return;
        }

        EntityPlayerMP mp = (EntityPlayerMP) player;
        ISoulEnergyData soul = SoulEnergyDataProvider.get(mp);
        IBodyEnergyData body = BodyEnergyDataProvider.get(mp);

        double soulRestore = 0.0D;
        double bodyRestore = 0.0D;

        if (Configs.sleep.soulRecoveryPercentPerTick > 0.0D && soul != null && soul.getMax() > 0.0D) {
            soulRestore = SleepRecoveryRules.calculateRecovery(soul.getMax(), soul.getCurrent(),
                    Configs.sleep.soulRecoveryPercentPerTick, effectiveSleepTicks);
            if (soulRestore > 0.0D) {
                soul.addCurrent(soulRestore);
                PacketSyncSoulEnergy.sync(mp);
            }
        }

        if (Configs.sleep.bodyRecoveryPercentPerTick > 0.0D && body != null && body.getMax() > 0.0D) {
            bodyRestore = SleepRecoveryRules.calculateRecovery(body.getMax(), body.getCurrent(),
                    Configs.sleep.bodyRecoveryPercentPerTick, effectiveSleepTicks);
            if (bodyRestore > 0.0D) {
                body.addCurrent(bodyRestore);
                PacketSyncBodyEnergy.sync(mp);
            }
        }

        if (soulRestore > 0.0D || bodyRestore > 0.0D) {
            ChakraSyncHelper.refresh(mp);
        }
    }

    private void trackSleepRecoverySession(EntityPlayerMP player) {
        NBTTagCompound data = player.getEntityData();
        if (!player.isPlayerSleeping()) {
            if (data.getBoolean(SLEEP_RECOVERY_ACTIVE)) {
                this.clearSleepRecoverySession(data);
            }
            return;
        }

        if (data.getBoolean(SLEEP_RECOVERY_ACTIVE)) {
            return;
        }

        this.recordSleepRecoverySession(player, !player.world.isDaytime());
    }

    private void recordSleepRecoverySession(EntityPlayer player, boolean startedAtNight) {
        NBTTagCompound data = player.getEntityData();
        data.setBoolean(SLEEP_RECOVERY_ACTIVE, true);
        data.setBoolean(SLEEP_RECOVERY_STARTED_AT_NIGHT, startedAtNight);
        data.setLong(SLEEP_RECOVERY_START_WORLD_TIME, player.world.getWorldTime());
    }

    private void clearSleepRecoverySession(NBTTagCompound data) {
        data.removeTag(SLEEP_RECOVERY_ACTIVE);
        data.removeTag(SLEEP_RECOVERY_STARTED_AT_NIGHT);
        data.removeTag(SLEEP_RECOVERY_START_WORLD_TIME);
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

    private void applyFoodConsumption(EntityPlayerMP player, EnergyRecoverySnapshot snapshot) {
        double requested = snapshot.getBodyFoodRequestedPerTick();
        if (requested <= 0.0D || player.capabilities.isCreativeMode) {
            return;
        }

        double minFoodLevel = snapshot.getBody().isLow()
                ? Configs.body.minFoodLevelForLowBody
                : 18.0D;

        FoodStats stats = player.getFoodStats();
        double remaining = requested;

        // Always consume saturation first
        float saturation = stats.getSaturationLevel();
        if (saturation > 0.0F) {
            float saturationCost = (float) Math.min(saturation, remaining);
            this.setServerFoodSaturationLevel(stats, saturation - saturationCost);
            remaining -= saturationCost;
        }

        if (remaining > 0.0D) {
            double debtBefore = snapshot.getBodyFoodDebtBefore();
            double totalDebt = debtBefore + remaining;
            int foodLevel = stats.getFoodLevel();
            int availableFood = Math.max(0, (int)(foodLevel - minFoodLevel));
            int foodCost = Math.min(availableFood, (int) Math.floor(totalDebt));
            if (foodCost > 0) {
                stats.setFoodLevel(foodLevel - foodCost);
            }
        }
    }

    private void setServerFoodSaturationLevel(FoodStats stats, float saturation) {
        NBTTagCompound foodData = new NBTTagCompound();
        stats.writeNBT(foodData);
        foodData.setFloat("foodSaturationLevel", Math.max(0.0F, saturation));
        stats.readNBT(foodData);
    }

}
