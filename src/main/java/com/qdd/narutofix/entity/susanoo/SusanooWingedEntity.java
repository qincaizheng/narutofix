package com.qdd.narutofix.entity.susanoo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.narutomod.PlayerTracker;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemKagutsuchiSwordRanged;
import net.narutomod.item.ItemKamuiShuriken;

/**
 * Susanoo Winged (Complete Body) — L4 with wings, flight and special weapons.
 */
public class SusanooWingedEntity extends SusanooEntityBase {

    private static final DataParameter<Float> WINGSWING = EntityDataManager
            .<Float>createKey(SusanooWingedEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager
            .<Boolean>createKey(SusanooWingedEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SHOW_SWORD = EntityDataManager
            .<Boolean>createKey(SusanooWingedEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> USING_KAMUI_WEAPON = EntityDataManager
            .<Boolean>createKey(SusanooWingedEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> MOTION_X = EntityDataManager
            .<Float>createKey(SusanooWingedEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> MOTION_Z = EntityDataManager
            .<Float>createKey(SusanooWingedEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> HEAD_YAW = EntityDataManager
            .<Float>createKey(SusanooWingedEntity.class, DataSerializers.FLOAT);

    private static final float MODELSCALE = 8.0F;
    private static final AttributeModifier SWORD_REACH = new AttributeModifier(
            "susanoo.swordReachExtension", 2.0D, 0);
    private static final AttributeModifier SWORD_ATTACK = new AttributeModifier(
            "susanoo.swordAttackDamage", 1.2D, 1);

    private static ItemStack _kagutsuchi() {
        ItemStack s = new ItemStack(ItemKagutsuchiSwordRanged.block);
        if (!s.isEmpty()) return s;
        Item item = net.minecraft.item.Item.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("narutomod", "kagutsuchiswordranged"));
        return item != null ? new ItemStack(item) : ItemStack.EMPTY;
    }
    private static ItemStack _kamui() {
        ItemStack s = new ItemStack(ItemKamuiShuriken.block);
        if (!s.isEmpty()) return s;
        Item item = net.minecraft.item.Item.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("narutomod", "kamuishuriken"));
        return item != null ? new ItemStack(item) : ItemStack.EMPTY;
    }

    private boolean isWingExtending;
    private boolean isWingDetracting;
    private int wingSwingProgressInt;
    private SusanooMagatamaEntity bulletEntity;
    private double lastX;
    private double lastZ;

    // ---- constructors -------------------------------------------------------

    public SusanooWingedEntity(World world) {
        super(world);
        this.setSize(MODELSCALE * 0.8F, MODELSCALE * 2.0F);
        this.getEntityData().setDouble("entityModelScale", (double) MODELSCALE);
        this.chakraUsage = 90.0D;
    }

    public SusanooWingedEntity(EntityPlayer player) {
        super(player);
        this.setSize(MODELSCALE * 0.8F, MODELSCALE * 2.0F);
        this.stepHeight = this.height / 3.0F;
        this.chakraUsage = 90.0D;
        this.wingSwingProgressInt = 0;
        this.isWingDetracting = false;
        this.isWingExtending = false;

        double xp = PlayerTracker.getBattleXp(player);

        this.getEntityAttribute(EntityPlayer.REACH_DISTANCE)
                .applyModifier(new AttributeModifier("susanoo.reachExtension", 12.0D, 0));
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
                .applyModifier(new AttributeModifier("susanoo.speedboost", 0.5D, 0));
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
                .setBaseValue(xp * 0.003D);
        this.getEntityData().setDouble("entityModelScale", (double) MODELSCALE);

        // Equip weapons on entity for visual and right-click-fire via processInteract.
        this.syncActiveWeaponStacks();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
                .setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() * 2.0D);
        this.setHealth(this.getMaxHealth());
    }

    // ---- data manager -------------------------------------------------------

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(WINGSWING, Float.valueOf(0.0F));
        this.dataManager.register(SWINGING_ARMS, Boolean.FALSE);
        this.dataManager.register(SHOW_SWORD, Boolean.FALSE);
        this.dataManager.register(USING_KAMUI_WEAPON, Boolean.FALSE);
        this.dataManager.register(MOTION_X, Float.valueOf(0.0F));
        this.dataManager.register(MOTION_Z, Float.valueOf(0.0F));
        this.dataManager.register(HEAD_YAW, Float.valueOf(0.0F));
    }

    // ---- wing animation -----------------------------------------------------

    public void setWingSwingProgress(float f) {
        this.dataManager.set(WINGSWING, f);
    }

    public float getWingSwingProgress() {
        return this.dataManager.get(WINGSWING);
    }

    public int getWingSwingAnimationEnd() {
        return 60;
    }

    public void extendWings() {
        this.isWingDetracting = false;
        this.isWingExtending = true;
    }

    public void detractWings() {
        this.isWingExtending = false;
        this.isWingDetracting = true;
    }

    public void updateWingSwing() {
        int animEnd = this.getWingSwingAnimationEnd();
        if (this.isWingExtending) {
            this.wingSwingProgressInt++;
            if (this.wingSwingProgressInt >= animEnd) {
                this.wingSwingProgressInt = animEnd;
                this.isWingExtending = false;
            }
        }
        if (this.isWingDetracting) {
            this.wingSwingProgressInt--;
            if (this.wingSwingProgressInt <= 0) {
                this.wingSwingProgressInt = 0;
                this.isWingDetracting = false;
            }
        }
        this.setWingSwingProgress((float) this.wingSwingProgressInt / (float) animEnd);
    }

    // ---- motion data (for client-side rendering) ----------------------------

    private void setMotionXZ(float x, float z, float headYaw) {
        this.dataManager.set(MOTION_X, x);
        this.dataManager.set(MOTION_Z, z);
        this.dataManager.set(HEAD_YAW, headYaw);
    }

    public ProcedureUtils.Vec2f getMotionXZ() {
        return new ProcedureUtils.Vec2f(this.dataManager.get(MOTION_X),
                                         this.dataManager.get(MOTION_Z));
    }

    public float getHeadYaw() {
        return this.dataManager.get(HEAD_YAW);
    }

    public boolean isSwingingArms() {
        return this.dataManager.get(SWINGING_ARMS);
    }

    public boolean isUsingKamuiWeapon() {
        return this.dataManager.get(USING_KAMUI_WEAPON);
    }

    public boolean toggleActiveWeapon() {
        this.dataManager.set(USING_KAMUI_WEAPON, Boolean.valueOf(!this.isUsingKamuiWeapon()));
        this.syncActiveWeaponStacks();
        return this.isUsingKamuiWeapon();
    }

    private void syncActiveWeaponStacks() {
        if (this.isUsingKamuiWeapon()) {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, _kamui());
            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, _kagutsuchi());
        } else {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, _kagutsuchi());
            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, _kamui());
        }
    }

    @Override
    public double getMountedYOffset() {
        return 14.0D;
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

    // ---- ranged attack (magatama) -------------------------------------------
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
        this.syncActiveWeaponStacks();
    }

    public void createBullet(float size) {
        this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
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
        if (!this.isDead) {
            this.syncActiveWeaponStacks();
        }
    }

    @Override
    protected void fireHeldWeapon() {
        EntityLivingBase owner = this.getOwnerPlayer();
        if (owner == null) return;
        int chargeTicks = this.getEntityData().getInteger("narutofix_chargeTicks");

        Vec3d look = owner.getLookVec();
        Vec3d spawn = this.getWeaponSpawnPosition(look);
        if (this.isUsingKamuiWeapon()) {
            // Charge-based power scaling: charge up to 400 ticks (~20s) for max power
            float chargePower = Math.min(1.0F, chargeTicks / 200.0F);
            float speed = 0.5F + chargePower * 1.5F;
            float damageScale = 0.5F + chargePower * 2.5F;

            net.narutomod.item.ItemKamuiShuriken.EntityKamuiShuriken shuriken =
                new net.narutomod.item.ItemKamuiShuriken.EntityKamuiShuriken(this.world, owner);
            shuriken.setScale((float) this.getEntityData().getDouble("entityModelScale") * (0.5F + chargePower * 0.5F));
            shuriken.setPosition(spawn.x, spawn.y, spawn.z);
            shuriken.ignoreEntity = this;
            shuriken.getEntityData().setInteger("narutofix_susanoo_owner", this.getEntityId());
            shuriken.shoot(look.x, look.y, look.z, speed, 0.0F);
            this.world.spawnEntity(shuriken);
        } else {
            // Charge scaling for kagutsuchi: more fireballs
            int count = chargeTicks > 80 ? 5 : (chargeTicks > 20 ? 3 : 1);
            Vec3d origin = new Vec3d(this.posX, spawn.y, this.posZ);
            for (int i = 0; i < count; ++i) {
                float yawOffset = (i - (count - 1) / 2.0F) * 15.0F;
                Vec3d target = origin.add(Vec3d.fromPitchYaw(owner.rotationPitch, owner.rotationYaw + yawOffset).scale(40.0D));
                net.narutomod.item.ItemKagutsuchiSwordRanged.EntityBigBlackFireball fireball =
                        new net.narutomod.item.ItemKagutsuchiSwordRanged.EntityBigBlackFireball(
                                this.world, this, target.x - spawn.x, target.y - spawn.y, target.z - spawn.z);
                fireball.posX = spawn.x;
                fireball.posY = spawn.y;
                fireball.posZ = spawn.z;
                fireball.setPosition(spawn.x, spawn.y, spawn.z);
                fireball.getEntityData().setInteger("narutofix_susanoo_owner", this.getEntityId());
                this.world.spawnEntity(fireball);
            }
        }
    }

    private Vec3d getWeaponSpawnPosition(Vec3d look) {
        Vec3d horizontal = new Vec3d(look.x, 0.0D, look.z);
        if (horizontal.length() < 0.0001D) {
            horizontal = Vec3d.fromPitchYaw(0.0F, this.rotationYaw);
        } else {
            horizontal = horizontal.normalize();
        }
        double forwardOffset = Math.max(this.width * 0.5D + 6.0D, MODELSCALE + 1.0D);
        double y = this.posY + (double) this.height * 0.58D;
        return new Vec3d(this.posX, y, this.posZ).add(horizontal.scale(forwardOffset));
    }

    @Override
    public void setDead() {
        super.setDead();
        this.killBullet();
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (this.isBeingRidden()) {
            Entity entity = this.getControllingPassenger();
            if (entity instanceof EntityLivingBase
                    && (!this.onGround || entity.rotationPitch < 0.0F)
                    && ((EntityLivingBase) entity).moveForward > 0.0F) {
                this.motionY -= entity.rotationPitch / 45.0D;
            }
            if (!this.onGround) {
                this.extendWings();
            } else {
                this.detractWings();
            }
        }
        super.travel(strafe, vertical, forward);
        this.setMotionXZ((float)(this.posX - this.lastX), (float)(this.posZ - this.lastZ), this.rotationYawHead);
    }

    @Override
    protected void collideWithEntity(Entity entity) {
        if (!this.world.isRemote && entity instanceof EntityLivingBase && !entity.equals(this.getOwnerPlayer())) {
            if (this.getOwnerPlayer() != null
             && this.getOwnerPlayer().getHeldItemMainhand().getItem() == net.narutomod.item.ItemKagutsuchiSwordRanged.block)
                ((EntityLivingBase) entity).addPotionEffect(new net.minecraft.potion.PotionEffect(net.narutomod.potion.PotionAmaterasuFlame.potion, 200, 2, false, false));
        }
        super.collideWithEntity(entity);
    }
}
