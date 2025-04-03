package com.qdd.narutofix.AI;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.passive.EntityTameable;
import net.narutomod.entity.EntitySusanooBase;

public class SusanooAIOwnerHurtTarget extends EntityAITarget {
    EntitySusanooBase tameable;
    EntityLivingBase attacker;
    private int timestamp;

    public SusanooAIOwnerHurtTarget(EntitySusanooBase theEntityTameableIn) {
        super(theEntityTameableIn, false);
        this.tameable = theEntityTameableIn;
        this.setMutexBits(1);
    }

    public boolean shouldExecute() {
            EntityLivingBase entitylivingbase = this.tameable.getOwnerPlayer();
            if (entitylivingbase == null) {
                return false;
            } else {
                this.attacker = entitylivingbase.getLastAttackedEntity();
                int i = entitylivingbase.getLastAttackedEntityTime();
                return i != this.timestamp && this.isSuitableTarget(this.attacker, false) ;
            }

    }

    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.attacker);
        EntityLivingBase entitylivingbase = this.tameable.getOwnerPlayer();
        if (entitylivingbase != null) {
            this.timestamp = entitylivingbase.getLastAttackedEntityTime();
        }

        super.startExecuting();
    }
}
