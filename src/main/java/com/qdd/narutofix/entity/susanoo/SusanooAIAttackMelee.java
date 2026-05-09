package com.qdd.narutofix.entity.susanoo;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

/**
 * Melee attack AI for Susanoo entities.
 * Attacks the current target when within reach distance; otherwise navigates
 * toward the target, recalculating the path every 20 ticks.
 * <p>
 * Adapted from {@code net.narutomod.entity.EntitySusanooBase.AIAttackMelee}.
 */
public class SusanooAIAttackMelee extends EntityAIBase {

    protected final SusanooEntityBase attacker;
    protected int attackTick;
    protected final double speedTowardsTarget;
    protected final int attackInterval = 20;
    protected final double attackReachSqr;
    private int navigateDelay;

    /**
     * @param attacker      the Susanoo entity that will perform the attacks
     * @param speed         movement speed toward the target
     * @param useLongMemory ignored (kept for API compatibility with vanilla
     *                      {@link net.minecraft.entity.ai.EntityAIAttackMelee})
     */
    public SusanooAIAttackMelee(SusanooEntityBase attacker, double speed, boolean useLongMemory) {
        this.attacker = attacker;
        this.speedTowardsTarget = speed;
        double reach = attacker.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        this.attackReachSqr = reach * reach;
        this.setMutexBits(3);
    }

    // ----------------------------------------------------------------

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive()) {
            return false;
        }
        double distSq = this.attacker.getDistanceSq(target);
        if (distSq <= this.attackReachSqr) {
            return true;
        }
        return this.attacker.getNavigator().tryMoveToEntityLiving(target, this.speedTowardsTarget);
    }

    @Override
    public boolean shouldContinueExecuting() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive()) {
            return false;
        }
        if (target instanceof EntityPlayer
                && (((EntityPlayer) target).isCreative()
                        || ((EntityPlayer) target).isSpectator())) {
            return false;
        }
        return true;
    }

    @Override
    public void startExecuting() {
        this.attackTick = 0;
        this.navigateDelay = 0;
    }

    @Override
    public void resetTask() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        if (target instanceof EntityPlayer
                && (((EntityPlayer) target).isCreative()
                        || ((EntityPlayer) target).isSpectator())) {
            this.attacker.setAttackTarget(null);
        }
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        if (target == null) {
            return;
        }

        double distSq = this.attacker.getDistanceSq(target);
        this.attacker.getLookHelper().setLookPositionWithEntity(target,
                30.0F, 30.0F);

        if (distSq <= this.attackReachSqr) {
            // In range — stop moving and attack
            this.attacker.getNavigator().clearPath();
            --this.attackTick;
            if (this.attackTick <= 0) {
                this.attackTick = this.attackInterval;
                this.attacker.swingArm(EnumHand.MAIN_HAND);
                this.attacker.attackEntityAsMob(target);
            }
        } else {
            // Out of range — navigate every 20 ticks
            --this.navigateDelay;
            if (this.navigateDelay <= 0) {
                this.navigateDelay = 20;
                this.attacker.getNavigator().tryMoveToEntityLiving(target,
                        this.speedTowardsTarget);
            }
        }
    }
}
