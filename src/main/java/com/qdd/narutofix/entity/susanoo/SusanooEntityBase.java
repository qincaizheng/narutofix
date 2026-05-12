package com.qdd.narutofix.entity.susanoo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import net.narutomod.Chakra;
import net.narutomod.Particles;

import javax.annotation.Nullable;

/**
 * Abstract base class for Susanoo entities.
 * Mirrors the core design of {@link net.narutomod.entity.EntitySusanooBase}
 * but lives in the narutofix package tree so subclasses are not coupled to
 * the narutomod entity hierarchy.
 */
public abstract class SusanooEntityBase extends EntityCreature implements IRangedAttackMob {

    private static final DataParameter<Integer> OWNER_ID = EntityDataManager
            .<Integer>createKey(SusanooEntityBase.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FLAME_COLOR = EntityDataManager
            .<Integer>createKey(SusanooEntityBase.class, DataSerializers.VARINT);

    /** Amaterasu damage source – bypasses armor and counts as magic damage. */
    public static final DamageSource AMATERASU = new DamageSource("amaterasu")
            .setDamageBypassesArmor().setMagicDamage();

    /** Chakra consumed per second (every 20 ticks). */
    protected double chakraUsage = 30.0D;
    /** Multiplier applied to chakraUsage each consume cycle. */
    protected double chakraUsageModifier = 2.0D;

    // ---- constructors -------------------------------------------------------

    public SusanooEntityBase(World world) {
        super(world);
        this.experienceValue = 5;
        this.isImmuneToFire = true;
        this.stepHeight = 0.5F;
        this.setNoAI(true);
        this.enablePersistence();
    }

    public SusanooEntityBase(EntityLivingBase player) {
        this(player.world);
        this.setLocationAndAngles(player.posX, player.posY, player.posZ,
                player.rotationYaw, 0.0F);
        this.setOwnerPlayer(player);
        this.setHealth(this.getMaxHealth());
        this.setAlwaysRenderNameTag(false);
        player.startRiding(this);
    }

    // ---- data manager -------------------------------------------------------

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(OWNER_ID, Integer.valueOf(-1));
        this.dataManager.register(FLAME_COLOR, Integer.valueOf(0x202C183D));
    }

    // ---- attributes ---------------------------------------------------------

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getAttributeMap().registerAttribute(EntityPlayer.REACH_DISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.1D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(7.0D);
    }

    // ---- owner API ----------------------------------------------------------

    /**
     * @return the owning living entity, or {@code null} if it no longer exists
     *         in the world
     */
    @Nullable
    public EntityLivingBase getOwnerPlayer() {
        Entity entity = this.world.getEntityByID(this.dataManager.get(OWNER_ID).intValue());
        return entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null;
    }

    protected void setOwnerPlayer(EntityLivingBase owner) {
        this.dataManager.set(OWNER_ID, Integer.valueOf(owner.getEntityId()));
    }

    // ---- flame colour API ---------------------------------------------------

    public int getFlameColor() {
        return this.dataManager.get(FLAME_COLOR).intValue();
    }

    protected void setFlameColor(int color) {
        this.dataManager.set(FLAME_COLOR, Integer.valueOf(color));
    }

    // ---- abstract weapon display --------------------------------------------

    public abstract boolean shouldShowSword();

    public abstract void setShowSword(boolean show);

    // ---- creature type / despawn --------------------------------------------

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected boolean canDropLoot() {
        return false;
    }

    // ---- ride system --------------------------------------------------------

    @Override
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public boolean canBeSteered() {
        return true;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public double getMountedYOffset() {
        return 0.35D;
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (this.isBeingRidden() && this.isAIDisabled()) {
            Entity entity = this.getControllingPassenger();
            this.rotationYaw = entity.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = entity.rotationPitch;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.jumpMovementFactor = this.getAIMoveSpeed();
            this.renderYawOffset = entity.rotationYaw;
            this.rotationYawHead = entity.rotationYaw;
            this.stepHeight = this.height / 3.0F;
            if (entity instanceof EntityLivingBase) {
                this.setAIMoveSpeed((float) this.getEntityAttribute(
                        SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                float f = ((EntityLivingBase) entity).moveForward;
                float s = ((EntityLivingBase) entity).moveStrafing;
                super.travel(s, 0.0F, f);
            }
        } else {
            this.jumpMovementFactor = 0.02F;
            super.travel(strafe, vertical, forward);
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        super.processInteract(player, hand);
        if (!this.world.isRemote && player.equals(this.getOwnerPlayer())) {
            if (player.isRiding()) {
                // Already riding → fire the held weapon
                this.fireHeldWeapon();
            } else {
                player.startRiding(this);
            }
            return true;
        }
        return false;
    }

    /**
     * Fires the entity's held weapon (called when rider right-clicks).
     * Override in subclasses for weapon-specific behavior (kagutsuchi, kamui, etc.).
     */
    protected void fireHeldWeapon() {
    }

    /**
     * Called when the rider releases the use key while holding a ranged weapon (shuriken).
     * Override in subclasses for projectile behavior (magatama, etc.).
     */
    public void attackEntityRanged(double x, double y, double z) {
    }

    // ---- damage / immunity --------------------------------------------------

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        // Immunity: rider's own attacks
        if (source.getImmediateSource() instanceof EntityPlayer
                && source.getImmediateSource().equals(this.getControllingPassenger())) {
            return false;
        }
        // Immunity: self-inflicted
        if (source.getImmediateSource() instanceof EntityCreature
                && source.getImmediateSource().equals(this)) {
            return false;
        }
        // Immunity: arrows
        if (source.getImmediateSource() instanceof net.minecraft.entity.projectile.EntityArrow) {
            return false;
        }
        // Immunity: potions
        if (source.getImmediateSource() instanceof net.minecraft.entity.projectile.EntityPotion) {
            return false;
        }
        // Immunity: fall / cactus / drown / magic / wither / amaterasu
        if (source == DamageSource.FALL
                || source == DamageSource.CACTUS
                || source == DamageSource.DROWN
                || source == DamageSource.MAGIC
                || source == DamageSource.WITHER
                || source == AMATERASU) {
            return false;
        }

        float oldHealth = this.getHealth();
        boolean flag = super.attackEntityFrom(source, amount);
        EntityLivingBase owner = this.getOwnerPlayer();
        if (flag && owner != null && !this.isEntityAlive()) {
            owner.attackEntityFrom(source,
                    net.minecraft.util.CombatRules.getDamageAfterAbsorb(
                            amount, (float) this.getTotalArmorValue(), 0.0F) - oldHealth);
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        Entity passenger = this.getControllingPassenger();
        if (passenger instanceof EntityLivingBase) {
            float dmg = (float) this.getEntityAttribute(
                    SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
            float strength = 1.0F;
            if (passenger instanceof EntityPlayer) {
                strength = ((EntityPlayer) passenger).getCooledAttackStrength(0.5F);
                ((EntityPlayer) passenger).resetCooldown();
            }
            dmg *= strength;
            boolean flag = target.attackEntityFrom(
                    DamageSource.causeMobDamage(this), dmg);
            if (flag && target instanceof EntityLivingBase) {
                this.applyEnchantments(this, target);
                if (strength > 0.8F) {
                    target.motionX *= 0.6D;
                    target.motionZ *= 0.6D;
                }
            }
            return flag;
        }
        return super.attackEntityAsMob(target);
    }

    // ---- per-tick upkeep ----------------------------------------------------

    /**
     * Consumes chakra from the owner every 20 ticks (once per second).
     * If the owner does not have enough chakra the entity is killed.
     */
    protected void consumeChakra() {
        if (this.ticksExisted % 20 == 0) {
            EntityLivingBase owner = this.getOwnerPlayer();
            if (owner != null
                    && !Chakra.pathway(owner).consume(this.chakraUsage * this.chakraUsageModifier)) {
                this.setDead();
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        EntityLivingBase owner = this.getOwnerPlayer();
        boolean isPlayer = owner instanceof EntityPlayer;

        // Kill if owner is missing, dead, or disconnected
        if (!this.world.isRemote
                && (owner == null
                        || !owner.isEntityAlive()
                        || (owner instanceof EntityPlayerMP
                                && ((EntityPlayerMP) owner).hasDisconnected())
                        || (!isPlayer && !this.isBeingRidden()))) {
            this.setDead();
        }

        if (isPlayer) {
            if (!((EntityPlayer) owner).isCreative()) {
                if (!this.world.isRemote) {
                    this.consumeChakra();
                }
            }
            // Mining fatigue every ~1s (offset from consumeChakra by 1 tick)
            if (!this.world.isRemote && this.ticksExisted % 20 == 1) {
                owner.addPotionEffect(new PotionEffect(
                        MobEffects.MINING_FATIGUE, 22, 6, false, false));
            }
        }

        this.updateArmSwingProgress();
        super.onLivingUpdate();

        this.clampMotion(0.05D);

        // Flame ambient sound
        if (this.ticksExisted % 30 == 0) {
            this.playSound(SoundEvent.REGISTRY.getObject(
                    new ResourceLocation("block.fire.ambient")),
                    1.0F, this.rand.nextFloat() * 0.7F + 0.3F);
        }

        // Flame particles around the body
        for (int i = 0; i < (int) this.height; ++i) {
            double d0 = this.posX + (this.rand.nextFloat() - 0.5D) * this.width;
            double d1 = this.posY + this.rand.nextFloat() * this.height;
            double d2 = this.posZ + (this.rand.nextFloat() - 0.5D) * this.width;
            this.world.spawnAlwaysVisibleParticle(
                    Particles.Types.FLAME.getID(),
                    d0, d1, d2, 0.0D, 0.05D, 0.0D,
                    this.getFlameColor(),
                    (int) (this.width * 15.0F));
        }
    }

    // ---- death --------------------------------------------------------------

    @Override
    protected void onDeathUpdate() {
        this.playSound(net.minecraft.init.SoundEvents.ITEM_SHIELD_BREAK,
                1.0F, this.rand.nextFloat() * 0.4F + 0.7F);
        this.setDead();
    }

    @Override
    public void setDead() {
        this.killBullet();
        super.setDead();
    }



    // ---- AI inner classes --------------------------------------------------

    /**
     * AI task that moves toward a ranged attack target and fires when in range.
     */
    public static class AIAttackRangedAndMoveTowardsTarget extends EntityAIBase {
        private final SusanooEntityBase entityHost;
        private final IRangedAttackMob rangedAttackEntityHost;
        private EntityLivingBase attackTarget;
        private int rangedAttackTime;
        private final double entityMoveSpeed;
        private final int maxRangedAttackTime;
        private final float minAttackRadius;
        private int navigateDelay;

        public AIAttackRangedAndMoveTowardsTarget(IRangedAttackMob attacker, double movespeed,
                                                  int maxAttackTime, float minAttackDistanceIn) {
            if (!(attacker instanceof SusanooEntityBase)) {
                throw new IllegalArgumentException(
                        "AIAttackRangedAndMoveTowardsTarget requires SusanooEntityBase");
            }
            this.rangedAttackEntityHost = attacker;
            this.entityHost = (SusanooEntityBase) attacker;
            this.entityMoveSpeed = movespeed;
            this.maxRangedAttackTime = maxAttackTime;
            this.rangedAttackTime = maxAttackTime;
            double reach = this.entityHost.getEntityAttribute(EntityPlayer.REACH_DISTANCE)
                    .getAttributeValue();
            this.minAttackRadius = (float) reach + minAttackDistanceIn;
            this.setMutexBits(3);
        }

        public boolean shouldExecute() {
            EntityLivingBase target = this.entityHost.getAttackTarget();
            if (target == null || !target.isEntityAlive()) {
                return false;
            }
            if (target.getDistance(this.entityHost) < this.minAttackRadius) {
                return false;
            }
            this.attackTarget = target;
            return true;
        }

        public boolean shouldContinueExecuting() {
            return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
        }

        public void resetTask() {
            this.attackTarget = null;
        }

        public void updateTask() {
            --this.navigateDelay;
            double dist = this.entityHost.getDistance(
                    this.attackTarget.posX,
                    this.attackTarget.getEntityBoundingBox().minY,
                    this.attackTarget.posZ);
            if (dist < this.minAttackRadius) {
                this.entityHost.getNavigator().clearPath();
            } else if (this.navigateDelay <= 0) {
                this.entityHost.getNavigator().tryMoveToEntityLiving(
                        this.attackTarget, this.entityMoveSpeed);
                this.navigateDelay = 15;
            }
            this.entityHost.getLookHelper().setLookPositionWithEntity(
                    this.attackTarget, 30.0F, 30.0F);
            if (--this.rangedAttackTime <= 0) {
                this.rangedAttackEntityHost.attackEntityWithRangedAttack(
                        this.attackTarget, 1.0F);
                this.rangedAttackTime = this.maxRangedAttackTime;
            }
        }
    }

    /**
     * AI task that attacks the target with a melee swing when within reach.
     */
    public static class AIAttackMelee extends EntityAIBase {
        protected final SusanooEntityBase attacker;
        protected int attackTick;
        protected final double speedTowardsTarget;
        protected final int attackInterval = 20;
        private final double attackReachSqr;
        private int navigateDelay;

        public AIAttackMelee(SusanooEntityBase creature, double speedIn) {
            this.attacker = creature;
            this.speedTowardsTarget = speedIn;
            double reach = creature.getEntityAttribute(EntityPlayer.REACH_DISTANCE)
                    .getAttributeValue();
            this.attackReachSqr = reach * reach;
            this.setMutexBits(3);
        }

        public boolean shouldExecute() {
            EntityLivingBase target = this.attacker.getAttackTarget();
            if (target == null || !target.isEntityAlive()) {
                return false;
            }
            return this.attacker.getDistanceSq(target) <= this.attackReachSqr;
        }

        public boolean shouldContinueExecuting() {
            EntityLivingBase target = this.attacker.getAttackTarget();
            if (target == null || !target.isEntityAlive()) {
                return false;
            }
            return this.attacker.isEntityAlive();
        }

        public void startExecuting() {
            this.attackTick = 0;
            this.navigateDelay = 0;
        }

        public void resetTask() {
            this.attacker.getNavigator().clearPath();
        }

        public void updateTask() {
            EntityLivingBase target = this.attacker.getAttackTarget();
            if (target == null) return;

            this.attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);

            if (--this.navigateDelay <= 0) {
                double distSq = this.attacker.getDistanceSq(target);
                if (distSq > this.attackReachSqr * 0.75D) {
                    this.attacker.getNavigator().tryMoveToEntityLiving(
                            target, this.speedTowardsTarget);
                } else {
                    this.attacker.getNavigator().clearPath();
                }
                this.navigateDelay = 10;
            }

            if (this.attacker.getDistanceSq(target) <= this.attackReachSqr
                    && --this.attackTick <= 0) {
                this.attackTick = this.attackInterval;
                this.attacker.attackEntityAsMob(target);
            }
        }
    }

    // ---- bullet sub-entity helpers (overridden in winged / full-body forms) -

    public void createBullet(float size) {
    }

    public void killBullet() {
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
    }

    protected void showHeldWeapons() {
        EntityLivingBase owner = this.getOwnerPlayer();
        if (!this.world.isRemote && owner != null) {
            this.setShowSword(owner.getHeldItemMainhand().getItem() == net.narutomod.item.ItemChokuto.block);
            if (owner.getHeldItemMainhand().getItem() == net.narutomod.item.ItemShuriken.block) {
                this.createBullet((float)this.getEntityData().getDouble("entityModelScale") * 0.5f);
            } else {
                this.killBullet();
            }
        }
    }

    // ---- helpers ------------------------------------------------------------

    private void clampMotion(double limit) {
        if (Math.abs(this.motionX) > limit) {
            this.motionX = this.motionX > 0.0D ? limit : -limit;
        }
        if (Math.abs(this.motionY) > limit) {
            this.motionY = this.motionY > 0.0D ? limit : -limit;
        }
        if (Math.abs(this.motionZ) > limit) {
            this.motionZ = this.motionZ > 0.0D ? limit : -limit;
        }
    }
}
