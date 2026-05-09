package com.qdd.narutofix.util;

@SuppressWarnings("unused")
public final class EnergyRecoverySnapshot {
    private final Entry soul;
    private final Entry body;
    private final Entry chakra;
    private final boolean indraBloodline;
    private final boolean asuraBloodline;
    private final double soulRecoveryMultiplier;
    private final double bodyRecoveryMultiplier;
    private final boolean soulSleepingRecovery;
    private final boolean soulIdleRecovery;
    private final double bodyFoodRequestedPerTick;
    private final double bodyFoodConsumedPerTick;
    private final double bodyFoodDebtBefore;
    private final double bodyFoodDebtAfter;
    private final double chakraEnergyCostPerTick;
    /** P0: Actual emergency chakra recovery applied per tick (separate from display total). */
    private final double chakraEmergencyRecoveryPerTick;
    /** P1: Crouch exchange is eligible for display (conditions met, regardless of interval). */
    private final boolean crouchExchangeEligible;
    /** P1: Crouch exchange is triggered this tick (eligible + interval match). */
    private final boolean crouchExchangeTriggered;
    private final double crouchExchangeChakraGain;
    private final double crouchExchangeSoulCost;
    private final double crouchExchangeBodyCost;
    private final boolean sleepRecoveryAvailable;
    private final double sleepSoulRecovery;
    private final double sleepBodyRecovery;

    public EnergyRecoverySnapshot(Entry soul, Entry body, Entry chakra, boolean indraBloodline, boolean asuraBloodline,
                                  double soulRecoveryMultiplier, double bodyRecoveryMultiplier,
                                  boolean soulSleepingRecovery, boolean soulIdleRecovery,
                                  double bodyFoodRequestedPerTick, double bodyFoodConsumedPerTick,
                                  double bodyFoodDebtBefore, double bodyFoodDebtAfter,
                                  double chakraEnergyCostPerTick, double chakraEmergencyRecoveryPerTick,
                                  boolean crouchExchangeEligible, boolean crouchExchangeTriggered,
                                  double crouchExchangeChakraGain,
                                  double crouchExchangeSoulCost, double crouchExchangeBodyCost,
                                  boolean sleepRecoveryAvailable, double sleepSoulRecovery, double sleepBodyRecovery) {
        this.soul = soul;
        this.body = body;
        this.chakra = chakra;
        this.indraBloodline = indraBloodline;
        this.asuraBloodline = asuraBloodline;
        this.soulRecoveryMultiplier = soulRecoveryMultiplier;
        this.bodyRecoveryMultiplier = bodyRecoveryMultiplier;
        this.soulSleepingRecovery = soulSleepingRecovery;
        this.soulIdleRecovery = soulIdleRecovery;
        this.bodyFoodRequestedPerTick = bodyFoodRequestedPerTick;
        this.bodyFoodConsumedPerTick = bodyFoodConsumedPerTick;
        this.bodyFoodDebtBefore = bodyFoodDebtBefore;
        this.bodyFoodDebtAfter = bodyFoodDebtAfter;
        this.chakraEnergyCostPerTick = chakraEnergyCostPerTick;
        this.chakraEmergencyRecoveryPerTick = chakraEmergencyRecoveryPerTick;
        this.crouchExchangeEligible = crouchExchangeEligible;
        this.crouchExchangeTriggered = crouchExchangeTriggered;
        this.crouchExchangeChakraGain = crouchExchangeChakraGain;
        this.crouchExchangeSoulCost = crouchExchangeSoulCost;
        this.crouchExchangeBodyCost = crouchExchangeBodyCost;
        this.sleepRecoveryAvailable = sleepRecoveryAvailable;
        this.sleepSoulRecovery = sleepSoulRecovery;
        this.sleepBodyRecovery = sleepBodyRecovery;
    }

    public Entry getSoul() {
        return this.soul;
    }

    public Entry getBody() {
        return this.body;
    }

    public Entry getChakra() {
        return this.chakra;
    }

    public boolean hasIndraBloodline() {
        return this.indraBloodline;
    }

    public boolean hasAsuraBloodline() {
        return this.asuraBloodline;
    }

    public double getSoulRecoveryMultiplier() {
        return this.soulRecoveryMultiplier;
    }

    public double getBodyRecoveryMultiplier() {
        return this.bodyRecoveryMultiplier;
    }

    public boolean isSoulSleepingRecovery() {
        return this.soulSleepingRecovery;
    }

    public boolean isSoulIdleRecovery() {
        return this.soulIdleRecovery;
    }

    public double getBodyFoodRequestedPerTick() {
        return this.bodyFoodRequestedPerTick;
    }

    public double getBodyFoodConsumedPerTick() {
        return this.bodyFoodConsumedPerTick;
    }

    public double getBodyFoodDebtBefore() {
        return this.bodyFoodDebtBefore;
    }

    public double getBodyFoodDebtAfter() {
        return this.bodyFoodDebtAfter;
    }

    public double getChakraEnergyCostPerTick() {
        return this.chakraEnergyCostPerTick;
    }

    /** P0: Emergency chakra recovery that will actually be executed per tick. */
    public double getChakraEmergencyRecoveryPerTick() {
        return this.chakraEmergencyRecoveryPerTick;
    }

    /** P1: Crouch exchange is eligible for HUD display (conditions met). */
    public boolean isCrouchExchangeEligible() {
        return this.crouchExchangeEligible;
    }

    /** P1: Crouch exchange is triggered for execution this tick. */
    public boolean isCrouchExchangeTriggered() {
        return this.crouchExchangeTriggered;
    }

    /** @deprecated Use {@link #isCrouchExchangeEligible()} for display or {@link #isCrouchExchangeTriggered()} for execution. */
    @Deprecated
    public boolean isCrouchExchangeActive() {
        return this.crouchExchangeTriggered;
    }

    public double getCrouchExchangeChakraGain() {
        return this.crouchExchangeChakraGain;
    }

    public double getCrouchExchangeSoulCost() {
        return this.crouchExchangeSoulCost;
    }

    public double getCrouchExchangeBodyCost() {
        return this.crouchExchangeBodyCost;
    }

    public boolean isSleepRecoveryAvailable() {
        return this.sleepRecoveryAvailable;
    }

    public double getSleepSoulRecovery() {
        return this.sleepSoulRecovery;
    }

    public double getSleepBodyRecovery() {
        return this.sleepBodyRecovery;
    }

    public static final class Entry {
        private final double current;
        private final double max;
        private final double recoveryPerTick;
        private final double drainPerTick;
        private final boolean low;
        private final boolean recovering;

        public Entry(double current, double max, double recoveryPerTick, double drainPerTick,
                     boolean low, boolean recovering) {
            this.current = current;
            this.max = max;
            this.recoveryPerTick = recoveryPerTick;
            this.drainPerTick = drainPerTick;
            this.low = low;
            this.recovering = recovering;
        }

        public double getCurrent() {
            return this.current;
        }

        public double getMax() {
            return this.max;
        }

        public double getRecoveryPerTick() {
            return this.recoveryPerTick;
        }

        public double getDrainPerTick() {
            return this.drainPerTick;
        }

        public double getNetPerTick() {
            return this.recoveryPerTick - this.drainPerTick;
        }

        public double getProjectedCurrent() {
            double projected = this.current + this.getNetPerTick();
            if (this.max <= 0.0D) {
                return Math.max(0.0D, projected);
            }
            return Math.max(0.0D, Math.min(this.max, projected));
        }

        public boolean isLow() {
            return this.low;
        }

        public boolean isRecovering() {
            return this.recovering;
        }
    }
}
