package com.qdd.narutofix.entity.susanoo;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import net.narutomod.procedure.ProcedureUtils;

/**
 * Renderer for {@link SusanooMagatamaEntity} (Yasaka Magatama projectile).
 *
 * <p>Copied faithfully from
 * {@link net.narutomod.entity.EntitySusanooClothed.Renderer.RenderMagatama}
 * with class references adapted to the narutofix package tree.</p>
 */
public class RenderSusanooMagatama extends Render<SusanooMagatamaEntity> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("narutomod:textures/yasaka_magatama.png");

    // ---- constructor -------------------------------------------------------

    public RenderSusanooMagatama(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.1F;
    }

    // ---- doRender (Tessellator-based) --------------------------------------

    @Override
    public void doRender(SusanooMagatamaEntity entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        this.bindEntityTexture(entity);
        float scale = entity.getEntityScale();
        int color = entity.getColor();
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(
                -entity.prevRotationYaw
                        - (entity.rotationYaw - entity.prevRotationYaw) * partialTicks,
                0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(
                entity.prevRotationPitch
                        + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks,
                1.0F, 0.0F, 0.0F);
        GlStateManager.scale(scale, scale, scale);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        if (ProcedureUtils.getVelocity(entity) > 0.001D) {
            GlStateManager.rotate(
                    (float) entity.ticksExisted + partialTicks, 0.0F, 0.0F, 1.0F);
        }
        GlStateManager.rotate(
                -30.0F * ((float) entity.ticksExisted + partialTicks),
                0.0F, 1.0F, 0.0F);

        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(
                OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

        // Outer layer (full alpha)
        buffer.pos(-0.5D, 0.5D, -0.5D).tex(0.0D, 1.0D)
                .color(red, green, blue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.5D, 0.5D, -0.5D).tex(1.0D, 1.0D)
                .color(red, green, blue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.5D, 0.5D, 0.5D).tex(1.0D, 0.0D)
                .color(red, green, blue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(-0.5D, 0.5D, 0.5D).tex(0.0D, 0.0D)
                .color(red, green, blue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();

        // Inner glow layers (0.5 alpha)
        buffer.pos(-0.49D, 0.51D, -0.49D).tex(0.0D, 1.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.49D, 0.51D, -0.49D).tex(1.0D, 1.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.49D, 0.51D, 0.49D).tex(1.0D, 0.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(-0.49D, 0.51D, 0.49D).tex(0.0D, 0.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();

        buffer.pos(-0.49D, 0.49D, -0.49D).tex(0.0D, 1.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.49D, 0.49D, -0.49D).tex(1.0D, 1.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.49D, 0.49D, 0.49D).tex(1.0D, 0.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(-0.49D, 0.49D, 0.49D).tex(0.0D, 0.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();

        buffer.pos(-0.48D, 0.52D, -0.48D).tex(0.0D, 1.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.48D, 0.52D, -0.48D).tex(1.0D, 1.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.48D, 0.52D, 0.48D).tex(1.0D, 0.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(-0.48D, 0.52D, 0.48D).tex(0.0D, 0.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();

        buffer.pos(-0.48D, 0.48D, -0.48D).tex(0.0D, 1.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.48D, 0.48D, -0.48D).tex(1.0D, 1.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.48D, 0.48D, 0.48D).tex(1.0D, 0.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(-0.48D, 0.48D, 0.48D).tex(0.0D, 0.0D)
                .color(red, green, blue, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();

        // Yellow core highlights
        buffer.pos(-0.45D, 0.53D, -0.45D).tex(0.0D, 1.0D)
                .color(1.0F, 1.0F, 0.0F, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.45D, 0.53D, -0.45D).tex(1.0D, 1.0D)
                .color(1.0F, 1.0F, 0.0F, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.45D, 0.53D, 0.45D).tex(1.0D, 0.0D)
                .color(1.0F, 1.0F, 0.0F, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(-0.45D, 0.53D, 0.45D).tex(0.0D, 0.0D)
                .color(1.0F, 1.0F, 0.0F, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();

        buffer.pos(-0.45D, 0.47D, -0.45D).tex(0.0D, 1.0D)
                .color(1.0F, 1.0F, 0.0F, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.45D, 0.47D, -0.45D).tex(1.0D, 1.0D)
                .color(1.0F, 1.0F, 0.0F, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.45D, 0.47D, 0.45D).tex(1.0D, 0.0D)
                .color(1.0F, 1.0F, 0.0F, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(-0.45D, 0.47D, 0.45D).tex(0.0D, 0.0D)
                .color(1.0F, 1.0F, 0.0F, 0.5F).normal(0.0F, 1.0F, 0.0F).endVertex();

        tessellator.draw();

        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    // ---- entity texture ----------------------------------------------------

    @Override
    protected ResourceLocation getEntityTexture(SusanooMagatamaEntity entity) {
        return TEXTURE;
    }
}
