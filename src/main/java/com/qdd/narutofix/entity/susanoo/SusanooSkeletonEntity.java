package com.qdd.narutofix.entity.susanoo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import net.narutomod.PlayerTracker;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.potion.PotionAmaterasuFlame;

/**
 * Susanoo Skeleton — L0 (ribcage half-torso) and L1 (upper-body skeleton).
 *
 * <p>Two forms distinguished by the {@link #FULL_BODY} data parameter.
 */
public class SusanooSkeletonEntity extends SusanooEntityBase {

    private static final DataParameter<Boolean> FULL_BODY = EntityDataManager
            .<Boolean>createKey(SusanooSkeletonEntity.class, DataSerializers.BOOLEAN);

    // ---- constructors -------------------------------------------------------

    public SusanooSkeletonEntity(World world) {
        super(world);
        this.setSize(2.4F, 2.4F);
    }

    /**
     * @param player   the owning player
     * @param fullBody {@code true} for the full upper-body form (L1)
     */
    public SusanooSkeletonEntity(EntityPlayer player, boolean fullBody) {
        super(player);
        this.setSize(2.4F, 2.4F);
        double xp = PlayerTracker.getBattleXp(player);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
                .setBaseValue(Math.min(xp, 10000.0D) * 0.003D);
        if (!fullBody) {
            this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(0.0D);
            this.chakraUsage = 30.0D;
        } else {
            this.setFullBody(true);
        }
        this.stepHeight = this.height / 3.0F;
    }

    // ---- data manager -------------------------------------------------------

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FULL_BODY, Boolean.FALSE);
    }

    // ---- full body API ------------------------------------------------------

    public boolean isFullBody() {
        return this.dataManager.get(FULL_BODY);
    }

    public void setFullBody(boolean fullBody) {
        this.dataManager.set(FULL_BODY, fullBody);
        this.setSize(2.4F, fullBody ? 3.6F : 2.4F);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (FULL_BODY.equals(key) && this.world.isRemote) {
            this.setSize(2.4F, this.isFullBody() ? 3.6F : 2.4F);
        }
    }

    // ---- sword display (always disabled for skeleton) -----------------------

    @Override
    public boolean shouldShowSword() {
        return false;
    }

    @Override
    public void setShowSword(boolean show) {
        // no-op: skeleton never displays a sword
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
    }

    // ---- per-tick -----------------------------------------------------------

    @Override
    public void onUpdate() {
        EntityLivingBase owner = this.getOwnerPlayer();
        if (owner != null && owner.swingProgressInt == -1) {
            this.swingArm(EnumHand.MAIN_HAND);
        }
        if (this.ticksExisted % 20 == 1) {
            this.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 22, 2, false, false));
        }
        super.onUpdate();
    }

    // ---- collision — amaterasu on contact -----------------------------------

    @Override
    protected void collideWithEntity(Entity entity) {
        if (!this.world.isRemote
                && this.getOwnerPlayer() != null
                && this.getOwnerPlayer().getItemStackFromSlot(EntityEquipmentSlot.HEAD)
                        .getItem() == ItemMangekyoSharingan.helmet
                && entity instanceof EntityLivingBase
                && !entity.equals(this.getOwnerPlayer())) {
            ((EntityLivingBase) entity).addPotionEffect(
                    new PotionEffect(PotionAmaterasuFlame.potion, 200, 0, false, false));
        }
        super.collideWithEntity(entity);
    }
}
