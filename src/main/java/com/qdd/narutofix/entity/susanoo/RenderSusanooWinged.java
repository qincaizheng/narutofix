package com.qdd.narutofix.entity.susanoo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import net.narutomod.procedure.ProcedureUtils;

/**
 * Renderer for {@link SusanooWingedEntity}.
 *
 * <p>Copied faithfully from
 * {@link net.narutomod.entity.EntitySusanooWinged.Renderer.RenderSusanooWinged}
 * with class references adapted to the narutofix package tree.</p>
 */
public class RenderSusanooWinged extends RenderLiving<SusanooWingedEntity> {

    private static final float MODELSCALE = 8.0F;
    private static final ResourceLocation MAIN_TEXTURE =
            new ResourceLocation("narutomod:textures/susanoo_winged.png");
    private static final ResourceLocation FLAME_TEXTURE =
            new ResourceLocation("narutomod:textures/gas256.png");

    // ---- constructor -------------------------------------------------------

    public RenderSusanooWinged(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSusanooWinged(), MODELSCALE * 0.5F);
        this.addLayer(new LayerHeldItem(this));
    }

    // ---- doRender ----------------------------------------------------------

    @Override
    public void doRender(SusanooWingedEntity entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        if (entity.isBeingRidden()
                && entity.getControllingPassenger() instanceof AbstractClientPlayer) {
            this.copyLimbSwing(entity,
                    (AbstractClientPlayer) entity.getControllingPassenger());
        }
        this.setModelVisibilities(entity);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    // ---- limb-swing copying ------------------------------------------------

    private void copyLimbSwing(SusanooWingedEntity entity, AbstractClientPlayer rider) {
        entity.swingProgress = rider.swingProgress;
        entity.swingProgressInt = rider.swingProgressInt;
        entity.prevSwingProgress = rider.prevSwingProgress;
        entity.isSwingInProgress = rider.isSwingInProgress;
        entity.swingingHand = rider.swingingHand;
    }

    // ---- model visibility --------------------------------------------------

    private void setModelVisibilities(SusanooWingedEntity entity) {
        ModelSusanooWinged model = (ModelSusanooWinged) this.getMainModel();
        model.wingSwingProgress = entity.getWingSwingProgress();
        model.setVisible(true);
        if (Minecraft.getMinecraft().getRenderViewEntity()
                .equals(entity.getControllingPassenger())
                && this.renderManager.options.thirdPersonView == 0) {
            model.bipedHead.showModel = false;
            model.bipedHeadwear.showModel = false;
        }
        model.rightArmPose = entity.getHeldItemMainhand().isEmpty()
                ? ModelBiped.ArmPose.EMPTY : ModelBiped.ArmPose.ITEM;
        model.sword.showModel = entity.shouldShowSword();
    }

    // ---- renderModel (two-pass + flight adjustments) -----------------------

    @Override
    protected void renderModel(SusanooWingedEntity entity,
                               float limbSwing, float limbSwingAmount,
                               float ageInTicks, float netHeadYaw,
                               float headPitch, float scaleFactor) {
        if (this.bindEntityTexture(entity)) {
            if (!entity.onGround) {
                limbSwingAmount = 0.0F;
                if (this.isMovingTowardsLookDirection(entity)) {
                    headPitch += this.getFlyingBodyRotationAmount(entity) * -90.0F;
                }
            }
            ModelSusanooWinged model = (ModelSusanooWinged) this.getMainModel();
            model.renderFlame = false;
            this.mainModel.render(entity, limbSwing, limbSwingAmount,
                    ageInTicks, netHeadYaw, headPitch, scaleFactor);
            this.bindTexture(FLAME_TEXTURE);
            model.renderFlame = true;
            this.mainModel.render(entity, limbSwing, limbSwingAmount,
                    ageInTicks, netHeadYaw, headPitch, scaleFactor * 0.99F);
        }
    }

    // ---- renderLayers (with MODELSCALE) ------------------------------------

    @Override
    protected void renderLayers(SusanooWingedEntity entity,
                                float limbSwing, float limbSwingAmount,
                                float partialTicks, float ageInTicks,
                                float netHeadYaw, float headPitch, float scaleIn) {
        if (!entity.onGround) {
            limbSwingAmount = 0.0F;
            if (this.isMovingTowardsLookDirection(entity)) {
                headPitch += this.getFlyingBodyRotationAmount(entity) * -90.0F;
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 1.5F - 1.5F * MODELSCALE, 0.0F);
        GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
        super.renderLayers(entity, limbSwing, limbSwingAmount,
                partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);
        GlStateManager.popMatrix();
    }

    // ---- applyRotations (flight body tilt) ---------------------------------

    @Override
    protected void applyRotations(SusanooWingedEntity entity,
                                  float p_77043_2_, float rotationYaw,
                                  float partialTicks) {
        super.applyRotations(entity, p_77043_2_, rotationYaw, partialTicks);
        if (!entity.onGround && this.isMovingTowardsLookDirection(entity)) {
            float f0 = this.getFlyingBodyRotationAmount(entity);
            float f1 = (1.0F - MathHelper.cos(f0 * 1.5708F)) * entity.height * 0.75F;
            float f2 = MathHelper.sin(f0 * 1.5708F) * entity.height * 0.75F;
            GlStateManager.translate(0.0F, f1, f2);
            GlStateManager.rotate(f0 * -90.0F, 1.0F, 0.0F, 0.0F);
        }
    }

    // ---- flight helpers ----------------------------------------------------

    private float getFlyingBodyRotationAmount(SusanooWingedEntity entity) {
        return MathHelper.clamp(
                entity.getMotionXZ().lengthVector() * 1.2F, 0.0F, 1.0F);
    }

    private boolean isMovingTowardsLookDirection(SusanooWingedEntity entity) {
        ProcedureUtils.Vec2f vec = entity.getMotionXZ();
        float yaw = ProcedureUtils.getYawFromVec(vec.x, vec.y);
        float diff = Math.abs(MathHelper.wrapDegrees(yaw - entity.getHeadYaw()));
        return vec.lengthVector() > 1.0E-6F && diff < 90.0F;
    }

    // ---- entity texture ----------------------------------------------------

    @Override
    protected ResourceLocation getEntityTexture(SusanooWingedEntity entity) {
        return MAIN_TEXTURE;
    }
}
