package com.qdd.narutofix.event;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.narutomod.PlayerTracker;
import net.narutomod.entity.EntityAltCamView;
import net.narutomod.gui.overlay.OverlayByakuganView;
import net.narutomod.item.ItemByakugan;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Client-side overlay handler for Amenotejikara's Byakugan-like visual effect.
 * Renders independently of the original OverlayByakuganView by checking
 * the byakuganActivated flag directly, without needing to hack the armor slot check.
 *
 * When byakuganActivated is true but the player does NOT have an actual Byakugan helmet,
 * this handler renders the same white overlay + entity glow + camera offset as the original.
 * The original OverlayByakuganView.eventHandler will skip rendering (armor check fails)
 * so there is no double-rendering.
 */
@SideOnly(Side.CLIENT)
public class AmenotejikaraOverlayHandler {
    private final List<EntityLivingBase> glowList = Lists.newArrayList();
    private EntityAltCamView.EntityCustom camEntity;
    private int prevRenderDistance;
    private boolean cameraActive;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.HELMET) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null || mc.world == null) {
            return;
        }

        boolean isAmenotejikaraEffect = OverlayByakuganView.byakuganActivated
                && player.inventory.armorInventory.get(3).getItem() != ItemByakugan.helmet;

        if (isAmenotejikaraEffect) {
            renderOverlay(event, mc, player);
        } else {
            cleanUp(mc, player);
        }
    }

    private void renderOverlay(RenderGameOverlayEvent event, Minecraft mc, EntityPlayer player) {
        int sWidth = event.getResolution().getScaledWidth();
        int sHeight = event.getResolution().getScaledHeight();

        // White inverted overlay (identical to original Byakugan)
        GlStateManager.enableAlpha();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.INVERT);
        GuiIngame.drawRect(0, 0, sWidth, sHeight, 0x1AFFFFFF);
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.COPY);
        GlStateManager.disableColorLogic();
        GlStateManager.disableAlpha();

        // Crosshair
        GuiIngame.drawRect(sWidth / 2 - 5, sHeight / 2, sWidth / 2 + 5, sHeight / 2 + 1, -1);
        GuiIngame.drawRect(sWidth / 2, sHeight / 2 - 5, sWidth / 2 + 1, sHeight / 2 + 5, -1);

        // Camera offset (same logic as original Byakugan setFOV)
        double xp = PlayerTracker.getNinjaLevel(player) / 3.0D;
        if (!this.cameraActive) {
            this.prevRenderDistance = mc.gameSettings.renderDistanceChunks;
            mc.gameSettings.renderDistanceChunks = MathHelper.clamp((int) xp * 11 / 16, 16, 32);
            this.camEntity = new EntityAltCamView.EntityCustom(player);
            mc.world.spawnEntity(this.camEntity);
            mc.setRenderViewEntity(this.camEntity);
            this.cameraActive = true;
        }
        if (this.camEntity != null) {
            float fov = OverlayByakuganView.byakuganActivated ? 110.0F : 0.0F;
            Vec3d vec = player.getPositionEyes(1.0F)
                    .add(player.getLookVec().scale(
                            (110.0F - fov) * Math.min((float) xp, 70.0F) / 10.0F + 1.0F));
            this.camEntity.setLocationAndAngles(vec.x, vec.y, vec.z,
                    player.rotationYaw, player.rotationPitch);
        }

        // Entity glow
        for (EntityLivingBase entity : mc.world.getEntitiesWithinAABB(EntityLivingBase.class,
                player.getEntityBoundingBox().grow(mc.gameSettings.renderDistanceChunks * 8))) {
            if (!entity.isGlowing() && !entity.equals(player)) {
                entity.setGlowing(true);
                this.glowList.add(entity);
            }
        }
    }

    private void cleanUp(Minecraft mc, EntityPlayer player) {
        if (this.cameraActive) {
            mc.setRenderViewEntity(player);
            if (this.camEntity != null) {
                player.world.removeEntity(this.camEntity);
                this.camEntity = null;
            }
            mc.gameSettings.renderDistanceChunks = this.prevRenderDistance;
            this.cameraActive = false;
        }
        if (!this.glowList.isEmpty()) {
            for (EntityLivingBase entity : this.glowList) {
                if (!entity.isInvisible()) {
                    entity.setGlowing(false);
                }
            }
            this.glowList.clear();
        }
    }
}
