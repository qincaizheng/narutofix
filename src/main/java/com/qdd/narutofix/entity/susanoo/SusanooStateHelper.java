package com.qdd.narutofix.entity.susanoo;

import net.minecraft.entity.player.EntityPlayer;

import net.narutomod.PlayerTracker;

/**
 * Pure static helper for reading / writing Susanoo activation state in a
 * player's persistent entity-data NBT.
 *
 * <p>All keys are namespaced with the prefix "narutofix_susanoo_" to avoid
 * collisions with other mods.</p>
 */
public final class SusanooStateHelper {

    private static final String ACTIVATED  = "narutofix_susanoo_activated";
    private static final String SUMMONED_ID = "narutofix_summoned_susanoo_id";
    private static final String TICKS      = "narutofix_susanoo_ticks";

    private SusanooStateHelper() {
    }

    /** @return {@code true} if the player currently has Susanoo activated */
    public static boolean isActivated(EntityPlayer player) {
        return player.getEntityData().getBoolean(ACTIVATED);
    }

    /**
     * @return the entity ID of the summoned Susanoo, or -1 if none is active
     */
    public static int getSummonedEntityId(EntityPlayer player) {
        return player.getEntityData().getInteger(SUMMONED_ID);
    }

    /** Marks the Susanoo as activated and stores the summoned entity ID. */
    public static void activate(EntityPlayer player, int entityId) {
        player.getEntityData().setBoolean(ACTIVATED, true);
        player.getEntityData().setInteger(SUMMONED_ID, entityId);
    }

    /** Clears the Susanoo activation state. */
    public static void deactivate(EntityPlayer player) {
        player.getEntityData().setBoolean(ACTIVATED, false);
        player.getEntityData().setInteger(SUMMONED_ID, -1);
        player.getEntityData().setDouble(TICKS, 0.0D);
    }

    /** Increments the Susanoo lifetime tick counter (call once per tick). */
    public static void tick(EntityPlayer player) {
        double ticks = player.getEntityData().getDouble(TICKS);
        player.getEntityData().setDouble(TICKS, ticks + 1.0D);
    }

    /** @return total ticks the Susanoo has been active this session */
    public static double getTicks(EntityPlayer player) {
        return player.getEntityData().getDouble(TICKS);
    }

    /**
     * Calculates the cooldown (in ticks) before the player can re-summon
     * Susanoo after it has been dismissed or destroyed.
     *
     * <p>Formula:<br>
     * {@code cooldown = activeTicks * 0.25 * cooldownModifier}<br>
     * where<br>
     * {@code cooldownModifier = 1.0 / (0.5 + 0.02 * sqrt(battleXp))}
     *
     * @param player the player
     * @return cooldown duration in ticks
     */
    public static double calculateCooldown(EntityPlayer player) {
        double ticks = getTicks(player);
        double modifier = getCooldownModifier(player);
        return ticks * 0.25D * modifier;
    }

    // ---- internal -----------------------------------------------------------

    /**
     * Cooldown modifier derived from the player's battle XP (ninja level).
     * Higher ninja levels yield shorter cooldowns.
     */
    private static double getCooldownModifier(EntityPlayer player) {
        double battleXp = PlayerTracker.getBattleXp(player);
        double level = Math.sqrt(battleXp);
        return 1.0D / (0.5D + 0.02D * level);
    }
}
