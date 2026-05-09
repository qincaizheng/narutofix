package com.qdd.narutofix.entity.susanoo;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * Follow-owner AI for Susanoo entities when the player is dismounted.
 * If the owner moves farther than {@code maxDist} the entity will teleport
 * close to the owner.
 * <p>
 * Ported from {@code com.qdd.narutofix.AI.EntityAISusanoo} and adapted
 * to work with {@link SusanooEntityBase}.
 */
public class SusanooAIFollowOwner extends EntityAIBase {

    private final SusanooEntityBase tameable;
    private EntityLivingBase owner;

    /** Movement speed when following. */
    private static final double FOLLOW_SPEED = 1.0D;

    /** Minimum distance from owner before we start following. */
    private static final float MIN_DIST = 10.0F;

    /** Maximum allowed distance before we teleport instead of pathing. */
    private static final float MAX_DIST = 64.0F;

    /** Distance-squared threshold that triggers teleport. */
    private static final double TELEPORT_THRESHOLD_SQ = 144.0D;

    private final PathNavigate petPathfinder;
    private int timeToRecalcPath;
    private float oldWaterCost;

    /**
     * @param tameable the Susanoo entity that should follow its owner
     * @throws IllegalArgumentException if the entity's navigator type is
     *         neither {@link PathNavigateGround} nor {@link PathNavigateFlying}
     */
    public SusanooAIFollowOwner(SusanooEntityBase tameable) {
        this.tameable = tameable;
        this.petPathfinder = tameable.getNavigator();
        this.setMutexBits(3);
        if (!(tameable.getNavigator() instanceof PathNavigateGround)
                && !(tameable.getNavigator() instanceof PathNavigateFlying)) {
            throw new IllegalArgumentException(
                    "Unsupported mob type for FollowOwnerGoal");
        }
    }

    // ----------------------------------------------------------------

    @Override
    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = this.tameable.getOwnerPlayer();
        if (entitylivingbase == null) {
            return false;
        }
        if (entitylivingbase instanceof EntityPlayer
                && ((EntityPlayer) entitylivingbase).isSpectator()) {
            return false;
        }
        if (this.tameable.getDistanceSq(entitylivingbase)
                < (double) (MIN_DIST * MIN_DIST)) {
            return false;
        }
        this.owner = entitylivingbase;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.petPathfinder.noPath()
                && this.tameable.getDistanceSq(this.owner)
                        > (double) (MAX_DIST * MAX_DIST);
    }

    @Override
    public void startExecuting() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tameable.getPathPriority(PathNodeType.WATER);
        this.tameable.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    @Override
    public void resetTask() {
        this.owner = null;
        this.petPathfinder.clearPath();
        this.tameable.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }

    @Override
    public void updateTask() {
        this.tameable.getLookHelper().setLookPositionWithEntity(
                this.owner, 10.0F,
                (float) this.tameable.getVerticalFaceSpeed());

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;

            if (!this.petPathfinder.tryMoveToEntityLiving(
                        this.owner, FOLLOW_SPEED)
                    && !this.tameable.getLeashed()
                    && !this.tameable.isRiding()
                    && this.tameable.getDistanceSq(this.owner)
                            >= TELEPORT_THRESHOLD_SQ) {
                // Try to teleport to a nearby block
                int x = MathHelper.floor(this.owner.posX) - 2;
                int z = MathHelper.floor(this.owner.posZ) - 2;
                int y = MathHelper.floor(
                        this.owner.getEntityBoundingBox().minY);

                for (int l = 0; l <= 4; ++l) {
                    for (int i1 = 0; i1 <= 4; ++i1) {
                        if ((l < 1 || i1 < 1 || l > 3 || i1 > 3)
                                && this.isTeleportFriendlyBlock(
                                        x, z, y, l, i1)) {
                            this.tameable.setLocationAndAngles(
                                    (double) ((float) (x + l) + 0.5F),
                                    (double) y,
                                    (double) ((float) (z + i1) + 0.5F),
                                    this.tameable.rotationYaw,
                                    this.tameable.rotationPitch);
                            this.petPathfinder.clearPath();
                            return;
                        }
                    }
                }
            }
        }
    }

    // ---- helpers ------------------------------------------------------------

    /**
     * Checks whether a block position is safe to teleport to.
     * Requires a solid floor, air above, and air two blocks up.
     */
    protected boolean isTeleportFriendlyBlock(int baseX, int baseZ, int baseY,
                                              int xOffset, int zOffset) {
        BlockPos pos = new BlockPos(baseX + xOffset, baseY - 1, baseZ + zOffset);
        IBlockState state = this.tameable.world.getBlockState(pos);
        return state.getBlockFaceShape(this.tameable.world, pos, EnumFacing.DOWN)
                        == BlockFaceShape.SOLID
                && state.canEntitySpawn(this.tameable)
                && this.tameable.world.isAirBlock(pos.up())
                && this.tameable.world.isAirBlock(pos.up(2));
    }
}
