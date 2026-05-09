package com.qdd.narutofix.util;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.awakening.Bloodline;
import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.body.BodyEnergyDataProvider;
import com.qdd.narutofix.cap.body.IBodyEnergyData;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.narutomod.Chakra;

public final class EnergyRecoveryCalculator {
    public static final String BODY_FOOD_DEBT = "narutofixBodyFoodDebt";

    private EnergyRecoveryCalculator() {
    }

    @SuppressWarnings("unused")
    public static EnergyRecoverySnapshot calculate(EntityPlayer player, IPlayerAwakeningData awakening, int stationaryTicks) {
        ISoulEnergyData soul = SoulEnergyDataProvider.get(player);
        IBodyEnergyData body = BodyEnergyDataProvider.get(player);
        Chakra.Pathway<?> pathway = player != null ? Chakra.pathway(player) : null;
        return calculate(player, awakening, soul, body, pathway, stationaryTicks);
    }

    public static EnergyRecoverySnapshot calculate(EntityPlayer player, IPlayerAwakeningData awakening,
                                                   ISoulEnergyData soul, IBodyEnergyData body,
                                                   Chakra.Pathway<?> pathway, int stationaryTicks) {
        double soulCurrent = soul == null ? 0.0D : soul.getCurrent();
        double soulMax = soul == null ? 0.0D : soul.getMax();
        double bodyCurrent = body == null ? 0.0D : body.getCurrent();
        double bodyMax = body == null ? 0.0D : body.getMax();
        double chakraCurrent = pathway == null ? 0.0D : pathway.getAmount();
        double chakraMax = pathway == null ? 0.0D : pathway.getMax();

        boolean hasIndra = awakening != null && awakening.hasBloodline(Bloodline.INDRA);
        boolean hasAsura = awakening != null && awakening.hasBloodline(Bloodline.ASURA);
        boolean soulLow = isBelowRatio(soulCurrent, soulMax, Configs.soul.soulLowThreshold);
        boolean bodyLow = isBelowRatio(bodyCurrent, bodyMax, Configs.body.bodyLowThreshold);
        boolean chakraLow = isBelowRatio(chakraCurrent, chakraMax, Configs.chakraEmergency.chakraEmergencyThreshold);

        double soulMultiplier = hasIndra ? Configs.soul.indraSoulRecoveryMultiplier : 1.0D;
        double bodyMultiplier = hasAsura ? Configs.body.asuraBodyRecoveryMultiplier : 1.0D;

        SoulCalculation soulCalculation = calculateSoulRecovery(player, soulCurrent, soulMax, stationaryTicks, soulLow, soulMultiplier);
        double soulAfterRecovery = clampToMax(soulCurrent + soulCalculation.recoveryPerTick, soulMax);

        BodyCalculation bodyCalculation = calculateBodyRecovery(player, bodyCurrent, bodyMax, bodyLow, bodyMultiplier);
        double bodyAfterRecovery = clampToMax(bodyCurrent + bodyCalculation.recoveryPerTick, bodyMax);

        // P0: Emergency chakra recovery only (computed separately from crouch exchange)
        ChakraCalculation chakraCalculation = calculateChakraRecovery(pathway, soulAfterRecovery, bodyAfterRecovery,
                chakraCurrent, chakraMax, chakraLow);

        // P1: Crouch stationary exchange — separate eligible (display) from triggered (execution)
        CrouchExchangeCalculation crouchCalc = calculateCrouchExchange(player, soulCurrent, bodyCurrent, chakraCurrent, chakraMax, stationaryTicks);

        // P3: Sleep recovery — calculated here for HUD display; execution happens in PlayerWakeUpEvent
        SleepRecoveryCalculation sleepCalc = calculateSleepRecovery(soulCurrent, soulMax, bodyCurrent, bodyMax);

        // --- Snapshot field assembly ---

        // P1: Per-tick equivalent for display — use eligible (always on when conditions met)
        double crouchChakraPerTick = 0.0D;
        double crouchSoulPerTick = 0.0D;
        double crouchBodyPerTick = 0.0D;
        if (crouchCalc.eligible && Configs.chakraCrouchExchange.triggerIntervalTicks > 0) {
            double interval = Configs.chakraCrouchExchange.triggerIntervalTicks;
            crouchChakraPerTick = crouchCalc.chakraGain / interval;
            crouchSoulPerTick = crouchCalc.soulCost / interval;
            crouchBodyPerTick = crouchCalc.bodyCost / interval;
        }

        // Soul Entry: drain = emergency chakra cost + crouch soul cost (per-tick)
        // recovering = per-tick recovery actively happening
        EnergyRecoverySnapshot.Entry soulEntry = new EnergyRecoverySnapshot.Entry(soulCurrent, soulMax,
                soulCalculation.recoveryPerTick, (chakraCalculation.energyCostPerTick + crouchSoulPerTick), soulLow,
                soulCalculation.recoveryPerTick > 0.0D);

        EnergyRecoverySnapshot.Entry bodyEntry = new EnergyRecoverySnapshot.Entry(bodyCurrent, bodyMax,
                bodyCalculation.recoveryPerTick, (chakraCalculation.energyCostPerTick + crouchBodyPerTick), bodyLow,
                bodyCalculation.recoveryPerTick > 0.0D);

        // Chakra Entry: recoveryPerTick = emergency + crouch per-tick (total for HUD)
        // recovering = true when either emergency or crouch eligible
        EnergyRecoverySnapshot.Entry chakraEntry = new EnergyRecoverySnapshot.Entry(chakraCurrent, chakraMax,
                chakraCalculation.recoveryPerTick + crouchChakraPerTick, 0.0D, chakraLow,
                chakraCalculation.recoveryPerTick > 0.0D || crouchCalc.eligible);

        return new EnergyRecoverySnapshot(soulEntry, bodyEntry, chakraEntry,
                hasIndra, hasAsura, soulMultiplier, bodyMultiplier,
                soulCalculation.sleepingRecovery, soulCalculation.idleRecovery,
                bodyCalculation.requestedFoodPerTick, bodyCalculation.consumedFoodPerTick,
                bodyCalculation.foodDebtBefore, bodyCalculation.foodDebtAfter,
                chakraCalculation.energyCostPerTick,
                chakraCalculation.recoveryPerTick, // P0: emergency recovery for handleLowChakra
                crouchCalc.eligible, crouchCalc.triggerThisTick,
                crouchCalc.chakraGain, crouchCalc.soulCost, crouchCalc.bodyCost,
                sleepCalc.available, sleepCalc.soulRecovery, sleepCalc.bodyRecovery);
    }

    public static FoodPreview previewFoodConsumption(EntityPlayer player, double amount,
                                                     double minFoodLevel) {
        if (player == null || amount <= 0.0D || player.capabilities.isCreativeMode) {
            double debt = getBodyFoodDebt(player);
            return new FoodPreview(0.0D, 0.0D, debt, debt);
        }

        FoodStats stats = player.getFoodStats();
        double consumed = 0.0D;
        double remaining = amount;
        float saturation = stats.getSaturationLevel();
        if (saturation > 0.0F) {
            double saturationCost = Math.min(saturation, remaining);
            consumed += saturationCost;
            remaining -= saturationCost;
        }

        double debtBefore = getBodyFoodDebt(player);

        double debtAfter = debtBefore + remaining;
        int food = stats.getFoodLevel();
        int foodCost = Math.min(Math.max(0, food - (int) Math.floor(minFoodLevel)), (int) Math.floor(debtAfter));
        if (foodCost > 0) {
            debtAfter -= foodCost;
            consumed += foodCost;
        } else if (food <= 0) {
            debtAfter = Math.min(debtAfter, 1.0D);
        }
        return new FoodPreview(consumed, remaining, debtBefore, debtAfter);
    }

    private static SoulCalculation calculateSoulRecovery(EntityPlayer player, double current, double max,
                                                         int stationaryTicks, boolean low, double multiplier) {
        if (player == null || !low || current >= max) {
            return new SoulCalculation(0.0D, false, false);
        }

        double restore = 0.0D;
        boolean sleeping = false;
        boolean idle = false;
        if (player.isPlayerSleeping()) {
            restore = Configs.soul.soulSleepRecoveryPerTick;
            sleeping = restore > 0.0D;
        } else if (stationaryTicks >= Configs.soul.soulIdleRequiredTicks) {
            restore = Configs.soul.soulIdleRecoveryPerTick;
            idle = restore > 0.0D;
        }

        restore *= multiplier;
        restore = Math.min(Math.max(0.0D, restore), Math.max(0.0D, max - current));
        return new SoulCalculation(restore, sleeping && restore > 0.0D, idle && restore > 0.0D);
    }

    private static BodyCalculation calculateBodyRecovery(EntityPlayer player, double current, double max,
                                                         boolean low, double multiplier) {
        double debtBefore = getBodyFoodDebt(player);

        // Creative mode: independent body recovery, no food consumption
        if (player != null && player.capabilities.isCreativeMode && current < max) {
            double recovery = Math.min(Configs.creativeBody.perTick, max - current);
            return new BodyCalculation(recovery, 0.0D, 0.0D, debtBefore, debtBefore);
        }

        if (player == null || current >= max) {
            return new BodyCalculation(0.0D, 0.0D, 0.0D, debtBefore, debtBefore);
        }

        double conversion = Configs.body.foodToBodyRate * multiplier;
        if (conversion <= 0.0D) {
            return new BodyCalculation(0.0D, 0.0D, 0.0D, debtBefore, debtBefore);
        }

        double neededFood = (max - current) / conversion;
        double requestedFood = Math.min(Configs.body.bodyFoodCostPerTick, neededFood);
        double minFoodLevel = low ? Configs.body.minFoodLevelForLowBody : 18.0D;
        FoodPreview foodPreview = previewFoodConsumption(player, requestedFood, minFoodLevel);
        double recovery = Math.min(max - current, foodPreview.getConsumedFood() * conversion);
        return new BodyCalculation(recovery, requestedFood, foodPreview.getConsumedFood(),
                foodPreview.getDebtBefore(), foodPreview.getDebtAfter());
    }

    /**
     * P0: Emergency chakra recovery only. Free stationary natural recovery is removed.
     * The replacement is crouch stationary exchange (P1).
     */
    private static ChakraCalculation calculateChakraRecovery(Chakra.Pathway<?> pathway, double soulCurrent,
                                                             double bodyCurrent, double current, double max,
                                                             boolean low) {
        if (!hasChakra(pathway)) {
            return new ChakraCalculation(0.0D, 0.0D);
        }

        // P0: Emergency recovery only — consume soul/body when chakra is below threshold
        if (low) {
            double rateSum = Configs.chakraEmergency.soulToChakraRate + Configs.chakraEmergency.bodyToChakraRate;
            double maxEnergyCost = Math.min(soulCurrent, bodyCurrent) * Configs.chakraEmergency.chakraEmergencyEnergyCostPercent ;
            if (rateSum <= 0.0D || maxEnergyCost <= 0.0D) {
                return new ChakraCalculation(0.0D, 0.0D);
            }

            double targetChakra = max * Configs.chakraEmergency.chakraEmergencyThreshold;
            double neededChakra = Math.max(0.0D, targetChakra - current);
            double costNeeded = neededChakra / rateSum;
            double energyCost = Math.min(maxEnergyCost, costNeeded);
            energyCost = Math.min(energyCost, Math.min(soulCurrent, bodyCurrent));
            energyCost = Math.max(0.0D, energyCost);
            return new ChakraCalculation(energyCost * rateSum, energyCost);
        }

        return new ChakraCalculation(0.0D, 0.0D);
    }

    /**
     * P1: Crouch stationary chakra exchange.
     * Returns separate flags for display eligibility vs execution trigger.
     * <ul>
     *   <li>{@code eligible} — all conditions met (sneaking, grounded, stationary, chakra not full, energy sufficient per policy)</li>
     *   <li>{@code triggerThisTick} — eligible AND interval tick matches</li>
     * </ul>
     * Config defense: invalid interval (<= 0), zero/missing costs, and SCALE divide-by-zero
     * all result in disabled exchange.
     */
    private static CrouchExchangeCalculation calculateCrouchExchange(EntityPlayer player,
                                                                     double soulCurrent, double bodyCurrent,
                                                                     double chakraCurrent, double chakraMax,
                                                                     int stationaryTicks) {
        if (player == null || !hasChakra(Chakra.pathway(player))) {
            return new CrouchExchangeCalculation(false, false, 0.0D, 0.0D, 0.0D);
        }

        Configs.CrouchChakraExchangeConfig cfg = Configs.chakraCrouchExchange;
        // Config defense: disabled or invalid interval
        if (!cfg.enabled || cfg.triggerIntervalTicks <= 0) {
            return new CrouchExchangeCalculation(false, false, 0.0D, 0.0D, 0.0D);
        }

        // Config defense: zero/negative costs should disable (no free exchange loop)
        if (cfg.soulCostPercentPerTrigger <= 0.0D && cfg.bodyCostPercentPerTrigger <= 0.0D) {
            return new CrouchExchangeCalculation(false, false, 0.0D, 0.0D, 0.0D);
        }
        if (cfg.chakraGainPercentPerTrigger <= 0.0D) {
            return new CrouchExchangeCalculation(false, false, 0.0D, 0.0D, 0.0D);
        }

        // Conditions: sneaking, on ground, stationary long enough, chakra not full
        if (!player.isSneaking() || !player.onGround || chakraCurrent >= chakraMax) {
            return new CrouchExchangeCalculation(false, false, 0.0D, 0.0D, 0.0D);
        }

        boolean eligible = stationaryTicks >= cfg.requiredTicks;
        if (!eligible) {
            return new CrouchExchangeCalculation(false, false, 0.0D, 0.0D, 0.0D);
        }

        // Check energy sufficiency for eligibility (SKIP policy needs enough energy)
        boolean hasEnoughForPolicy;
        if (cfg.insufficientPolicy == Configs.CrouchChakraExchangeConfig.InsufficientPolicy.SKIP) {
            hasEnoughForPolicy = soulCurrent >= (soulCurrent * cfg.soulCostPercentPerTrigger )
                && bodyCurrent >= (bodyCurrent * cfg.bodyCostPercentPerTrigger );
        } else {
            // SCALE: eligible as long as at least one side has > 0 and we can compute a positive ratio
            hasEnoughForPolicy = cfg.soulCostPercentPerTrigger > 0.0D || cfg.bodyCostPercentPerTrigger > 0.0D;
        }

        if (!hasEnoughForPolicy) {
            return new CrouchExchangeCalculation(false, false, 0.0D, 0.0D, 0.0D);
        }

        // Determine trigger this tick
        int elapsed = stationaryTicks - cfg.requiredTicks;
        boolean triggerThisTick = elapsed % cfg.triggerIntervalTicks == 0;

        // Compute gain/cost
        double gain;
        double soulCost;
        double bodyCost;
        if (cfg.insufficientPolicy == Configs.CrouchChakraExchangeConfig.InsufficientPolicy.SKIP) {
            gain = Math.min(chakraMax * cfg.chakraGainPercentPerTrigger , chakraMax - chakraCurrent);
            soulCost = soulCurrent * cfg.soulCostPercentPerTrigger ;
            bodyCost = bodyCurrent * cfg.bodyCostPercentPerTrigger ;
        } else {
            // SCALE: proportionally reduce based on available energy
            double fullSoulCost = soulCurrent * cfg.soulCostPercentPerTrigger ;
            double fullBodyCost = bodyCurrent * cfg.bodyCostPercentPerTrigger ;
            double soulRatio = cfg.soulCostPercentPerTrigger > 0.0D ? Math.min(1.0D, soulCurrent / fullSoulCost) : Double.MAX_VALUE;
            double bodyRatio = cfg.bodyCostPercentPerTrigger > 0.0D ? Math.min(1.0D, bodyCurrent / fullBodyCost) : Double.MAX_VALUE;
            double scale = Math.min(1.0D, Math.min(soulRatio, bodyRatio));
            if (scale <= 0.0D) {
                return new CrouchExchangeCalculation(false, false, 0.0D, 0.0D, 0.0D);
            }
            gain = Math.min(chakraMax * cfg.chakraGainPercentPerTrigger  * scale, chakraMax - chakraCurrent);
            soulCost = fullSoulCost * scale;
            bodyCost = fullBodyCost * scale;
        }

        gain = Math.max(0.0D, gain);
        soulCost = Math.max(0.0D, soulCost);
        bodyCost = Math.max(0.0D, bodyCost);

        // eligible=true even if gain is 0 (room check already done above)
        boolean finalEligible = gain > 0.0D || (chakraCurrent < chakraMax);
        return new CrouchExchangeCalculation(finalEligible, triggerThisTick && finalEligible,
                gain, soulCost, bodyCost);
    }

    /**
     * P3: Calculate sleep recovery values (one-time, not per-tick).
     * Formula: restore = min(max * percent, max - current)
     * i.e. restore UP TO `percent` of max, but no more than remaining room.
     */
    private static SleepRecoveryCalculation calculateSleepRecovery(double soulCurrent, double soulMax,
                                                                   double bodyCurrent, double bodyMax) {
        if (Configs.sleep.soulRecoveryPercent <= 0.0D && Configs.sleep.bodyRecoveryPercent <= 0.0D) {
            return new SleepRecoveryCalculation(false, 0.0D, 0.0D);
        }

        double soulRestore = 0.0D;
        double bodyRestore = 0.0D;
        if (Configs.sleep.soulRecoveryPercent > 0.0D && soulMax > 0.0D) {
            double rawSoulRecovery = soulMax * Configs.sleep.soulRecoveryPercent;
            double soulRoom = Math.max(0.0D, soulMax - soulCurrent);
            soulRestore = Math.min(rawSoulRecovery, soulRoom);
        }
        if (Configs.sleep.bodyRecoveryPercent > 0.0D && bodyMax > 0.0D) {
            double rawBodyRecovery = bodyMax * Configs.sleep.bodyRecoveryPercent;
            double bodyRoom = Math.max(0.0D, bodyMax - bodyCurrent);
            bodyRestore = Math.min(rawBodyRecovery, bodyRoom);
        }

        boolean available = soulRestore > 0.0D || bodyRestore > 0.0D;
        return new SleepRecoveryCalculation(available, soulRestore, bodyRestore);
    }

    private static double getBodyFoodDebt(EntityPlayer player) {
        if (player == null) {
            return 0.0D;
        }
        return player.getEntityData().getDouble(BODY_FOOD_DEBT);
    }

    private static boolean hasChakra(Chakra.Pathway<?> pathway) {
        return pathway != null;
    }

    private static boolean isBelowRatio(double current, double max, double ratio) {
        return ratio > 0.0D && max > 0.0D && current / max < ratio;
    }

    private static double clampToMax(double value, double max) {
        if (max <= 0.0D) {
            return Math.max(0.0D, value);
        }
        return Math.max(0.0D, Math.min(max, value));
    }

    public static final class FoodPreview {
        private final double consumedFood;
        private final double remainingFood;
        private final double debtBefore;
        private final double debtAfter;

        private FoodPreview(double consumedFood, double remainingFood, double debtBefore, double debtAfter) {
            this.consumedFood = consumedFood;
            this.remainingFood = remainingFood;
            this.debtBefore = debtBefore;
            this.debtAfter = debtAfter;
        }

        public double getConsumedFood() {
            return this.consumedFood;
        }

        public double getRemainingFood() {
            return this.remainingFood;
        }

        public double getDebtBefore() {
            return this.debtBefore;
        }

        public double getDebtAfter() {
            return this.debtAfter;
        }
    }

    private static final class SoulCalculation {
        private final double recoveryPerTick;
        private final boolean sleepingRecovery;
        private final boolean idleRecovery;

        private SoulCalculation(double recoveryPerTick, boolean sleepingRecovery, boolean idleRecovery) {
            this.recoveryPerTick = recoveryPerTick;
            this.sleepingRecovery = sleepingRecovery;
            this.idleRecovery = idleRecovery;
        }
    }

    private static final class BodyCalculation {
        private final double recoveryPerTick;
        private final double requestedFoodPerTick;
        private final double consumedFoodPerTick;
        private final double foodDebtBefore;
        private final double foodDebtAfter;

        private BodyCalculation(double recoveryPerTick, double requestedFoodPerTick, double consumedFoodPerTick,
                                double foodDebtBefore, double foodDebtAfter) {
            this.recoveryPerTick = recoveryPerTick;
            this.requestedFoodPerTick = requestedFoodPerTick;
            this.consumedFoodPerTick = consumedFoodPerTick;
            this.foodDebtBefore = foodDebtBefore;
            this.foodDebtAfter = foodDebtAfter;
        }
    }

    private static final class ChakraCalculation {
        private final double recoveryPerTick;
        private final double energyCostPerTick;

        private ChakraCalculation(double recoveryPerTick, double energyCostPerTick) {
            this.recoveryPerTick = recoveryPerTick;
            this.energyCostPerTick = energyCostPerTick;
        }
    }

    private static final class CrouchExchangeCalculation {
        private final boolean eligible;
        private final boolean triggerThisTick;
        private final double chakraGain;
        private final double soulCost;
        private final double bodyCost;

        private CrouchExchangeCalculation(boolean eligible, boolean triggerThisTick,
                                          double chakraGain, double soulCost, double bodyCost) {
            this.eligible = eligible;
            this.triggerThisTick = triggerThisTick;
            this.chakraGain = chakraGain;
            this.soulCost = soulCost;
            this.bodyCost = bodyCost;
        }
    }

    private static final class SleepRecoveryCalculation {
        private final boolean available;
        private final double soulRecovery;
        private final double bodyRecovery;

        private SleepRecoveryCalculation(boolean available, double soulRecovery, double bodyRecovery) {
            this.available = available;
            this.soulRecovery = soulRecovery;
            this.bodyRecovery = bodyRecovery;
        }
    }
}
