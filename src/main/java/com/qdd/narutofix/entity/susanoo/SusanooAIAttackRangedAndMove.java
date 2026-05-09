package com.qdd.narutofix.entity.susanoo;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

/**
 * Ranged attack AI for Susanoo entities (Yasaka Magatama / Yasaka beads).
 * Fires projectiles at the current target when within the configured attack
 * radius; otherwise moves towards the target, recalculating the path every
 * 15 ticks.  The actual projectile is created by the entity's implementation
 * of {@link IRangedAttackMob#attackEntityWithRangedAttack}.
 * <p>
 * Adapted from {@code net.narutomod.entity.EntitySusanooBase.AIAttackRangedAndMoveTowardsTarget}.
 */
public class SusanooAIAttackRangedAndMove extends EntityAIBase implements IRangedAttackMob {

    public void setSwingingArms(boolean swingingArms) {
    }

    protected final SusanooEntityBase attacker;
    protected EntityLivingBase attackTarget;
    protected int rangedAttackTime;
    protected final int maxRangedAttackTime = 30;
    protected final double minAttackRadius;
    private int navigateDelay;

    /**
     * @param attacker the Susanoo entity that will perform the ranged attacks
     */
    public SusanooAIAttackRangedAndMove(SusanooEntityBase attacker) {
        this.attacker = attacker;
        double reach = attacker.getEntityAttribute(EntityPlayer.REACH_DISTANCE)
                .getAttributeValue();
        this.minAttackRadius = reach + 4.0D;
        this.setMutexBits(3);
    }

    // ----------------------------------------------------------------

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive()) {
            return false;
        }
        // Spectator / creative players are not valid targets
        if (target instanceof EntityPlayer
                && (((EntityPlayer) target).isCreative()
                        || ((EntityPlayer) target).isSpectator())) {
            return false;
        }
        this.attackTarget = target;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.attackTarget == null || !this.attackTarget.isEntityAlive()) {
            return false;
        }
        if (this.attackTarget instanceof EntityPlayer
                && (((EntityPlayer) this.attackTarget).isCreative()
                        || ((EntityPlayer) this.attackTarget).isSpectator())) {
            return false;
        }
        return true;
    }

    @Override
    public void startExecuting() {
        this.rangedAttackTime = 0;
        this.navigateDelay = 0;
    }

    @Override
    public void resetTask() {
        this.attackTarget = null;
    }

    @Override
    public void updateTask() {
        if (this.attackTarget == null) {
            return;
        }

        double distSq = this.attacker.getDistanceSq(this.attackTarget);
        boolean canSee = this.attacker.getEntitySenses().canSee(this.attackTarget);

        // Face the target
        this.attacker.getLookHelper().setLookPositionWithEntity(
                this.attackTarget, 30.0F, 30.0F);

        // Navigation logic
        --this.navigateDelay;
        if (this.navigateDelay <= 0) {
            this.navigateDelay = 15;
            double dist = MathHelper.sqrt(distSq);
            if (dist > this.minAttackRadius) {
                // Move closer
                this.attacker.getNavigator().tryMoveToEntityLiving(
                        this.attackTarget, 1.0D);
            } else if (canSee) {
                // In range — stop and fire
                this.attacker.getNavigator().clearPath();
            }
        }

        // Attack timer
        --this.rangedAttackTime;
        if (this.rangedAttackTime <= 0) {
            if (distSq < (this.minAttackRadius * this.minAttackRadius) && canSee) {
                this.attackEntityWithRangedAttack(this.attackTarget, 1.0F);
                this.rangedAttackTime = this.maxRangedAttackTime;
            }
        }
    }

    // ---- IRangedAttackMob ---------------------------------------------------

    /**
     * Delegates the actual ranged attack to the entity.
     * <p>
     * Concrete Susanoo subclasses should implement {@link IRangedAttackMob}
     * so that this call creates the Yasaka Magatama projectile.  If the
     * entity does not implement the interface, falls back to
     * {@link SusanooEntityBase#createBullet(float)}.
     */
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target,
                                             float distanceFactor) {
        if (this.attacker instanceof IRangedAttackMob) {
            ((IRangedAttackMob) this.attacker)
                    .attackEntityWithRangedAttack(target, distanceFactor);
        } else {
            this.attacker.createBullet(distanceFactor);
        }
    }
}
