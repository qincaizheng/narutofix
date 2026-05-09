package com.qdd.narutofix.entity.susanoo;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

/**
 * Target AI that copies the owner's attack target.
 * Whenever the owner attacks a mob, this Susanoo entity will switch to the
 * same target.
 * <p>
 * Ported from {@code com.qdd.narutofix.AI.SusanooAIOwnerHurtTarget} and
 * adapted to work with {@link SusanooEntityBase}.
 */
public class SusanooAIOwnerHurtTarget extends EntityAITarget {

    protected final SusanooEntityBase tameable;
    protected EntityLivingBase attacker;
    private int timestamp;

    /**
     * @param tameable the Susanoo entity that should mimic its owner's target
     */
    public SusanooAIOwnerHurtTarget(SusanooEntityBase tameable) {
        super(tameable, false);
        this.tameable = tameable;
        this.setMutexBits(1);
    }

    // ----------------------------------------------------------------

    @Override
    public boolean shouldExecute() {
        EntityLivingBase owner = this.tameable.getOwnerPlayer();
        if (owner == null) {
            return false;
        }
        this.attacker = owner.getLastAttackedEntity();
        int lastTime = owner.getLastAttackedEntityTime();
        if (lastTime == this.timestamp) {
            return false;
        }
        return this.isSuitableTarget(this.attacker, false);
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.attacker);
        EntityLivingBase owner = this.tameable.getOwnerPlayer();
        if (owner != null) {
            this.timestamp = owner.getLastAttackedEntityTime();
        }
        super.startExecuting();
    }
}
