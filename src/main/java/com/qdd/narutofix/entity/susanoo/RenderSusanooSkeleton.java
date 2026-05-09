package com.qdd.narutofix.entity.susanoo;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

/**
 * Renderer for {@link SusanooSkeletonEntity}.
 *
 * <p>Copied faithfully from
 * {@link net.narutomod.entity.EntitySusanooSkeleton.Renderer.RenderSusanooSkeleton}
 * with class references adapted to the narutofix package tree.</p>
 */
public class RenderSusanooSkeleton extends RenderLiving<SusanooSkeletonEntity> {

    private static final ResourceLocation MAIN_TEXTURE =
            new ResourceLocation("narutomod:textures/susanooskeleton.png");
    private static final ResourceLocation FLAME_TEXTURE =
            new ResourceLocation("narutomod:textures/gas256.png");

    // ---- constructor -------------------------------------------------------

    public RenderSusanooSkeleton(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelSusanooSkeleton(), 1.5F);
    }

    // ---- doRender ----------------------------------------------------------

    @Override
    public void doRender(SusanooSkeletonEntity entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        this.setModelVisibilities(entity);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    // ---- model visibility --------------------------------------------------

    private void setModelVisibilities(SusanooSkeletonEntity entity) {
        ModelSusanooSkeleton model = (ModelSusanooSkeleton) this.getMainModel();
        model.bipedLeftLeg.showModel = false;
        model.bipedRightLeg.showModel = false;
        boolean flag = entity.isFullBody();
        model.bipedHead.showModel = flag;
        model.bipedHeadwear.showModel = flag;
        model.bipedRightArm.showModel = flag;
        model.bipedLeftArm.showModel = flag;
    }

    // ---- renderModel (two-pass: solid + flame) -----------------------------

    @Override
    protected void renderModel(SusanooSkeletonEntity entity,
                               float limbSwing, float limbSwingAmount,
                               float ageInTicks, float netHeadYaw,
                               float headPitch, float scaleFactor) {
        if (this.bindEntityTexture(entity)) {
            ModelSusanooSkeleton model = (ModelSusanooSkeleton) this.getMainModel();
            model.setRenderFlame(false);
            this.mainModel.render(entity, limbSwing, limbSwingAmount,
                    ageInTicks, netHeadYaw, headPitch, scaleFactor);
            this.bindTexture(FLAME_TEXTURE);
            model.setRenderFlame(true);
            this.mainModel.render(entity, limbSwing, limbSwingAmount,
                    ageInTicks, netHeadYaw, headPitch, scaleFactor * 0.99F);
        }
    }

    // ---- entity texture ----------------------------------------------------

    @Override
    protected ResourceLocation getEntityTexture(SusanooSkeletonEntity entity) {
        return MAIN_TEXTURE;
    }
}
