package com.qdd.narutofix.entity.susanoo;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.PlayerTracker;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.item.ItemChokuto;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemShuriken;
import net.narutomod.item.ItemTotsukaSword;
import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureTotsukaSwordToolInHandTick;

import java.util.HashMap;

/**
 * Susanoo Clothed — L2 (armoured torso, no legs) and L3 (armoured with legs).
 */
public class SusanooClothedEntity extends SusanooEntityBase {

    private static final DataParameter<Boolean> HAS_LEGS = EntityDataManager
            .<Boolean>createKey(SusanooClothedEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager
            .<Boolean>createKey(SusanooClothedEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SHOW_SWORD = EntityDataManager
            .<Boolean>createKey(SusanooClothedEntity.class, DataSerializers.BOOLEAN);

    private static final float MODELSCALE = 4.0F;
    private static final AttributeModifier SWORD_REACH = new AttributeModifier(
            "susanoo.swordReachExtension", 2.0D, 0);
    private static final AttributeModifier SWORD_ATTACK = new AttributeModifier(
            "susanoo.swordAttackDamage", 1.2D, 1);

    private SusanooMagatamaEntity bulletEntity;

    // ---- constructors -------------------------------------------------------

    public SusanooClothedEntity(World world) {
        super(world);
        this.setSize(MODELSCALE * 0.8F, MODELSCALE * (this.hasLegs() ? 2.0F : 1.25F));
        this.getEntityData().setDouble("entityModelScale", (double) MODELSCALE);
        this.chakraUsage = this.hasLegs() ? 70.0D : 60.0D;
    }

    public SusanooClothedEntity(EntityPlayer player, boolean hasLegs) {
        super(player);
        this.setLegs(hasLegs);
        this.getEntityData().setDouble("entityModelScale", (double) MODELSCALE);
        // Give the player a Chokuto (草薙剑) if they don't already have one
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,
                new net.minecraft.item.ItemStack(net.narutomod.item.ItemChokuto.block));
        double xp = PlayerTracker.getBattleXp(player);

        if (hasLegs) {
            this.getEntityAttribute(EntityPlayer.REACH_DISTANCE)
                    .applyModifier(new AttributeModifier("susanoo.reachExtension", 3.0D, 0));
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
                    .applyModifier(new AttributeModifier("susanoo.speedboost", 0.2D, 0));
        }

        double maxBxp = hasLegs ? 40000.0D : 20000.0D;
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
                .setBaseValue(Math.min(xp, maxBxp) * 0.003D);

        this.setHealth(this.getMaxHealth());
        this.chakraUsage = hasLegs ? 70.0D : 60.0D;
        this.stepHeight = this.height / 3.0F;
    }

    // ---- data manager -------------------------------------------------------

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(HAS_LEGS, Boolean.FALSE);
        this.dataManager.register(SWINGING_ARMS, Boolean.FALSE);
        this.dataManager.register(SHOW_SWORD, Boolean.FALSE);
    }

    // ---- legs ---------------------------------------------------------------

    public boolean hasLegs() {
        return this.dataManager.get(HAS_LEGS);
    }

    public void setLegs(boolean hasLegs) {
        this.dataManager.set(HAS_LEGS, hasLegs);
        this.setSize(MODELSCALE * 0.8F, MODELSCALE * (hasLegs ? 2.0F : 1.25F));
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (HAS_LEGS.equals(key) && this.world.isRemote) {
            this.setSize(MODELSCALE * 0.8F, MODELSCALE * (this.hasLegs() ? 2.0F : 1.25F));
        }
    }

    // ---- sword display ------------------------------------------------------

    @Override
    public boolean shouldShowSword() {
        return this.dataManager.get(SHOW_SWORD);
    }

    @Override
    public void setShowSword(boolean show) {
        if (show != this.shouldShowSword()) {
            if (show) {
                this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).applyModifier(SWORD_REACH);
                this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(SWORD_ATTACK);
                this.chakraUsage += 15.0D;
            } else {
                this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).removeModifier(SWORD_REACH);
                this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(SWORD_ATTACK);
                this.chakraUsage -= 15.0D;
            }
            this.dataManager.set(SHOW_SWORD, show);
        }
    }

    // ---- AI -----------------------------------------------------------------

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new AIAttackRangedAndMoveTowardsTarget(this, 1.5D, 30, 4.0F));
        this.tasks.addTask(2, new AIAttackMelee(this, 1.5D));
    }

    @Override
    protected void updateAITasks() {
        if (this.getAttackTarget() != null && !this.getAttackTarget().isEntityAlive()) {
            this.setAttackTarget(null);
        }
    }

    // ---- mounted Y offset ---------------------------------------------------

    @Override
    public double getMountedYOffset() {
        if (this.hasLegs()) {
            return (double) MODELSCALE;
        }
        return super.getMountedYOffset();
    }

    // ---- held weapons -------------------------------------------------------

    protected void showHeldWeapons() {
        super.showHeldWeapons();
        EntityLivingBase owner = this.getOwnerPlayer();
        if (owner instanceof EntityPlayer) {
            ItemStack ownerHand = owner.getHeldItemMainhand();
            ItemStack thisHand = this.getHeldItemMainhand();
            if (ownerHand.getItem() == ItemTotsukaSword.block) {
                if (thisHand.getItem() != ItemTotsukaSword.block) {
                    this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ownerHand.copy());
                }
            } else if (thisHand.getItem() == ItemTotsukaSword.block) {
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
        }
        if (this.getHeldItemMainhand().getItem() == ItemTotsukaSword.block) {
            HashMap<String, Object> deps = new HashMap<>();
            deps.put("entity", this);
            deps.put("itemstack", this.getHeldItemMainhand());
            deps.put("world", this.world);
            ProcedureTotsukaSwordToolInHandTick.executeProcedure(deps);
        }
    }

    // ---- entity update ------------------------------------------------------

    public void onEntityUpdate() {
        super.onEntityUpdate();
        this.showHeldWeapons();
        if (!this.world.isRemote && !this.isAIDisabled()
                && this.ticksExisted % 20 == 0) {
            EntityLivingBase owner = this.getOwnerPlayer();
            if (owner instanceof net.minecraft.entity.EntityLiving) {
                this.setAttackTarget(
                        ((net.minecraft.entity.EntityLiving) owner).getAttackTarget());
            }
        }
    }

    // ---- collision — amaterasu on contact -----------------------------------

    @Override
    protected void collideWithEntity(Entity entity) {
        if (!this.world.isRemote && entity instanceof EntityLivingBase
                && !entity.equals(this.getOwnerPlayer())) {
            if (this.getOwnerPlayer() != null) {
                ItemStack helm = this.getOwnerPlayer()
                        .getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                if (helm.getItem() == ItemMangekyoSharingan.helmet
                        || helm.getItem() == ItemMangekyoSharinganEternal.helmet) {
                    ((EntityLivingBase) entity).addPotionEffect(
                            new PotionEffect(PotionAmaterasuFlame.potion, 200,
                                    this.hasLegs() ? 2 : 1, false, false));
                }
            }
        }
        super.collideWithEntity(entity);
    }

    // ---- swinging arms (client-side) ----------------------------------------

    public boolean isSwingingArms() {
        return this.dataManager.get(SWINGING_ARMS);
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        this.dataManager.set(SWINGING_ARMS, swingingArms);
    }

    // ---- ranged attack (magatama) -------------------------------------------

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (this.bulletEntity == null) {
            this.createBullet(MODELSCALE * 0.5F);
        }
        Vec3d vec = target.getPositionEyes(1.0F)
                .subtract(this.bulletEntity.getPositionVector());
        this.attackEntityRanged(vec.x, vec.y, vec.z);
    }

    public void attackEntityRanged(double x, double y, double z) {
        if (this.bulletEntity == null) {
            this.createBullet(MODELSCALE * 0.5F);
        }
        this.bulletEntity.shoot(x, y, z, 0.99F, 0.0F);
        this.bulletEntity = null;
        this.setSwingingArms(false);
    }

    public void createBullet(float size) {
        this.setSwingingArms(true);
        if (this.bulletEntity == null) {
            this.bulletEntity = new SusanooMagatamaEntity(this, this.getFlameColor(), size);
            this.world.spawnEntity(this.bulletEntity);
        } else if (this.bulletEntity.getEntityScale() != size) {
            this.bulletEntity.setEntityScale(size);
        }
    }

    public void killBullet() {
        if (this.bulletEntity != null) {
            this.bulletEntity.setDead();
            this.bulletEntity = null;
        }
        this.setSwingingArms(false);
    }

    @Override
    public void setDead() {
        super.setDead();
        this.killBullet();
    }
}
