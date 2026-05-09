package com.qdd.narutofix.entity.susanoo;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;

/**
 * Renderer for {@link SusanooClothedEntity}.
 *
 * <p>Copied faithfully from
 * {@link net.narutomod.entity.EntitySusanooClothed.Renderer.RenderSusanooClothed}
 * with class references adapted to the narutofix package tree.</p>
 */
public class RenderSusanooClothed extends RenderLiving<SusanooClothedEntity> {

    private static final float MODELSCALE = 4.0F;
    private static final ResourceLocation MAIN_TEXTURE =
            new ResourceLocation("narutomod:textures/susanoo_clothed.png");
    private static final ResourceLocation FLAME_TEXTURE =
            new ResourceLocation("narutomod:textures/gas256.png");

    // ---- constructor -------------------------------------------------------

    public RenderSusanooClothed(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSusanooClothed(), 0.6F * MODELSCALE);
        this.addLayer(new LayerHeldItem(this));
    }

    // ---- doRender ----------------------------------------------------------

    @Override
    public void doRender(SusanooClothedEntity entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        if (entity.isBeingRidden()
                && entity.getControllingPassenger() instanceof AbstractClientPlayer) {
            AbstractClientPlayer passenger =
                    (AbstractClientPlayer) entity.getControllingPassenger();
            this.copyLimbSwing(entity, passenger);
        }
        this.setModelVisibilities(entity);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    // ---- limb-swing copying ------------------------------------------------

    private void copyLimbSwing(SusanooClothedEntity entity, AbstractClientPlayer rider) {
        entity.swingProgress = rider.swingProgress;
        entity.swingProgressInt = rider.swingProgressInt;
        entity.prevSwingProgress = rider.prevSwingProgress;
        entity.isSwingInProgress = rider.isSwingInProgress;
        entity.swingingHand = rider.swingingHand;
    }

    // ---- model visibility --------------------------------------------------

    private void setModelVisibilities(SusanooClothedEntity entity) {
        ModelSusanooClothed model = (ModelSusanooClothed) this.getMainModel();
        model.setVisible(true);
        if (!entity.hasLegs()) {
            model.bipedLeftLeg.showModel = false;
            model.bipedRightLeg.showModel = false;
        }
        if (this.renderManager.renderViewEntity.equals(entity.getControllingPassenger())
                && this.renderManager.options.thirdPersonView == 0) {
            model.bipedBody.showModel = false;
            model.bipedHead.showModel = false;
            model.bipedHeadwear.showModel = false;
        }
        model.rightArmPose = entity.getHeldItemMainhand().isEmpty()
                ? ModelBiped.ArmPose.EMPTY : ModelBiped.ArmPose.ITEM;
        model.sword.showModel = entity.shouldShowSword();
    }

    // ---- renderModel (two-pass: solid + flame) -----------------------------

    @Override
    protected void renderModel(SusanooClothedEntity entity,
                               float limbSwing, float limbSwingAmount,
                               float ageInTicks, float netHeadYaw,
                               float headPitch, float scaleFactor) {
        if (this.bindEntityTexture(entity)) {
            ModelSusanooClothed model = (ModelSusanooClothed) this.getMainModel();
            model.setRenderFlame(false);
            this.mainModel.render(entity, limbSwing, limbSwingAmount,
                    ageInTicks, netHeadYaw, headPitch, scaleFactor);
            this.bindTexture(FLAME_TEXTURE);
            model.setRenderFlame(true);
            this.mainModel.render(entity, limbSwing, limbSwingAmount,
                    ageInTicks, netHeadYaw, headPitch, scaleFactor * 0.99F);
        }
    }

    // ---- renderLayers (with MODELSCALE) ------------------------------------

    @Override
    protected void renderLayers(SusanooClothedEntity entity,
                                float limbSwing, float limbSwingAmount,
                                float partialTicks, float ageInTicks,
                                float netHeadYaw, float headPitch, float scaleIn) {
        float offset = 1.5F - 1.5F * MODELSCALE / (entity.hasLegs() ? 1 : 2);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, offset, 0.0F);
        GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
        super.renderLayers(entity, limbSwing, limbSwingAmount,
                partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);
        GlStateManager.popMatrix();
    }

    // ---- entity texture ----------------------------------------------------

    @Override
    protected ResourceLocation getEntityTexture(SusanooClothedEntity entity) {
        return MAIN_TEXTURE;
    }
}
