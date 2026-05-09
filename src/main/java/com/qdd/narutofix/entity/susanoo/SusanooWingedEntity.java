package com.qdd.narutofix.entity.susanoo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraftforge.items.ItemHandlerHelper;

import net.narutomod.PlayerTracker;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemKagutsuchiSwordRanged;
import net.narutomod.item.ItemKamuiShuriken;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemTotsukaSword;
import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.procedure.ProcedureKagutsuchiSwordToolInUseTick;
import net.narutomod.procedure.ProcedureTotsukaSwordToolInHandTick;

import java.util.HashMap;

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
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, _kagutsuchi());
        this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, _kamui());
        this.setHealth(this.getMaxHealth());
    }

    // ---- data manager -------------------------------------------------------

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(WINGSWING, Float.valueOf(0.0F));
        this.dataManager.register(SWINGING_ARMS, Boolean.FALSE);
        this.dataManager.register(SHOW_SWORD, Boolean.FALSE);
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
    }

    @Override
    protected void fireHeldWeapon() {
        ItemStack held = this.getHeldItemMainhand();
        EntityLivingBase owner = this.getOwnerPlayer();
        if (owner == null) return;
        if (held.getItem() == _kagutsuchi().getItem()) {
            net.narutomod.item.ItemKagutsuchiSwordRanged.EntityBlackFireball fireball =
                new net.narutomod.item.ItemKagutsuchiSwordRanged.EntityBlackFireball(
                    this.world,
                    this.posX, this.posY + (double)this.height * 0.5D, this.posZ,
                    owner.getLookVec().x * 3.0D,
                    owner.getLookVec().y * 3.0D,
                    owner.getLookVec().z * 3.0D);
            this.world.spawnEntity(fireball);
        } else if (held.getItem() == _kamui().getItem()) {
            net.narutomod.item.ItemKamuiShuriken.EntityKamuiShuriken shuriken =
                new net.narutomod.item.ItemKamuiShuriken.EntityKamuiShuriken(this.world, owner);
            shuriken.shoot(owner, owner.rotationPitch, owner.rotationYaw, 0.0F, 2.0F, 0.0F);
            this.world.spawnEntity(shuriken);
        }
    }

    @Override
    public void setDead() {
        super.setDead();
        this.killBullet();
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (this.isBeingRidden()) {
            EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
            if ((!this.onGround || entity.rotationPitch < 0.0F) && entity.moveForward > 0.0F) {
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
