package com.qdd.narutofix.entity.susanoo;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.narutomod.Particles;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureUtils;

/**
 * Yasaka Magatama — the projectile fired by L2+ Susanoo forms.
 *
 * <p>On impact it creates an explosion and applies AOE magic damage.
 */
public class SusanooMagatamaEntity extends EntityScalableProjectile.Base {

    private static final DataParameter<Integer> COLOR = EntityDataManager
            .<Integer>createKey(SusanooMagatamaEntity.class, DataSerializers.VARINT);

    private int explosionSize;
    private float damage;

    // ---- constructors -------------------------------------------------------

    public SusanooMagatamaEntity(World world) {
        super(world);
        this.setOGSize(1.0F, 1.0F);
    }

    /**
     * @param shooter the entity that created this projectile (Susanoo entity)
     * @param color   the flame colour (packed ARGB)
     * @param scale   visual / damage scale factor
     */
    public SusanooMagatamaEntity(EntityLivingBase shooter, int color, float scale) {
        super(shooter);
        this.setOGSize(1.0F, 1.0F);
        this.setEntityScale(scale);
        this.setColor(color);
        this.explosionSize = (int) (scale * 3);
        this.damage = scale * 20.0F;
        this.setIdlePosition();
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    // ---- data manager -------------------------------------------------------

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(COLOR, 0xFFFFFFFF);
    }

    public int getColor() {
        return this.dataManager.get(COLOR);
    }

    public void setColor(int color) {
        this.dataManager.set(COLOR, color);
    }

    // ---- initial position ---------------------------------------------------

    private void setIdlePosition() {
        if (this.shootingEntity != null) {
            Vec3d look = this.shootingEntity.getLookVec()
                    .scale(this.getEntityScale() + 0.5D);
            Vec3d eyes = this.shootingEntity.getPositionEyes(1.0F)
                    .subtract(0.0D, this.height, 0.0D);
            Vec3d pos = look.add(eyes);
            this.setLocationAndAngles(pos.x, pos.y, pos.z,
                    ProcedureUtils.getYawFromVec(look.x, look.z),
                    ProcedureUtils.getPitchFromVec(look.x, look.y, look.z));
        }
    }

    // ---- impact -------------------------------------------------------------

    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (result.entityHit != null
                    && (result.entityHit.equals(this.shootingEntity)
                    || result.entityHit instanceof SusanooMagatamaEntity)) {
                return;
            }
            boolean griefing = net.minecraftforge.event.ForgeEventFactory
                    .getMobGriefingEvent(this.world, this.shootingEntity);
            this.world.newExplosion(this.shootingEntity,
                    this.posX, this.posY, this.posZ,
                    this.explosionSize, griefing, griefing);
            ProcedureAoeCommand.set(this, 0.0D, 3.0D)
                    .exclude(this.shootingEntity)
                    .damageEntities(
                            ItemJutsu.causeJutsuDamage(this, this.shootingEntity),
                            this.damage);
            this.setDead();
        }
    }

    // ---- particles ----------------------------------------------------------

    public void renderParticles() {
        float scale = this.getEntityScale();
        Particles.spawnParticle(this.world, Particles.Types.SMOKE,
                this.posX, this.posY + this.height / 2.0F, this.posZ,
                (int) (scale * 10),
                0.3D * this.width, 0.3D * this.height, 0.3D * this.width,
                0.0D, 0.0D, 0.0D,
                this.getColor(),
                10 + (int) (scale * 10),
                (int) (4.0D / (this.rand.nextDouble() * 0.8D + 0.2D)),
                0xF0);
    }

    // ---- idle hover / timeout -----------------------------------------------

    protected void checkOnGround() {
        // magatama never sticks to the ground
    }

    @Override
    public void setDead() {
        super.setDead();
        if (this.shootingEntity instanceof SusanooClothedEntity) {
            ((SusanooClothedEntity) this.shootingEntity).setSwingingArms(false);
        } else if (this.shootingEntity instanceof SusanooWingedEntity) {
            ((SusanooWingedEntity) this.shootingEntity).setSwingingArms(false);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.isLaunched()) {
            this.setIdlePosition();
        }
        if (this.ticksAlive % 12 == 1) {
            this.playSound(SoundEvent.REGISTRY
                            .getObject(new ResourceLocation("narutomod:magatama_spin")),
                    1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
        }
        if (this.ticksInAir > 100) {
            this.setDead();
        }
    }
}
