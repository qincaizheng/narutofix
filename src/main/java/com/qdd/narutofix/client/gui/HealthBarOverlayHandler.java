package com.qdd.narutofix.client.gui;

import com.qdd.narutofix.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Replaces the vanilla heart rendering with a compact progress bar.
 * Uses Forge's Pre(HEALTH) event: cancelling it prevents heart rendering
 * without affecting armor/food/mount-health (they have separate element events).
 * Only draws the bar - no covering rects that could interfere with other elements.
 */
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(value = Side.CLIENT)
public class HealthBarOverlayHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onPreHealth(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HEALTH) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;
        if (!Configs.body.enableCompactHealthBar) return;

        // Cancel vanilla heart rendering (only affects HEALTH element)
        event.setCanceled(true);

        // Draw compact health bar
        int width = event.getResolution().getScaledWidth();
        int height = event.getResolution().getScaledHeight();

        int barWidth = 80;
        int barHeight = 8;
        int x = width / 2 - 91;
        int y = height - 39;

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float ratio = Math.min(1.0f, health / maxHealth);

        // Save GL state before custom drawing to avoid leaking
        // blend/texture/color state to subsequent overlay elements (ARMOR, FOOD, etc.)
        GlStateManager.pushMatrix();

        // Bar background
        Gui.drawRect(x, y, x + barWidth, y + barHeight, 0xFF555555);

        // Health fill - green/yellow/red
        int color;
        if (ratio > 0.6f) {
            color = 0xFF00FF00;
        } else if (ratio > 0.3f) {
            color = 0xFFFFAA00;
        } else {
            color = 0xFFFF0000;
        }

        int fillWidth = (int) ((barWidth - 2) * ratio);
        if (fillWidth > 0) {
            Gui.drawRect(x + 1, y + 1, x + 1 + fillWidth, y + barHeight - 1, color);
        }

        // Border
        Gui.drawRect(x, y, x + barWidth, y + 1, 0xFF000000);
        Gui.drawRect(x, y + barHeight - 1, x + barWidth, y + barHeight, 0xFF000000);
        Gui.drawRect(x, y, x + 1, y + barHeight, 0xFF000000);
        Gui.drawRect(x + barWidth - 1, y, x + barWidth, y + barHeight, 0xFF000000);

        // HP text (moved up: y - 1)
        String hpText = String.format("%.0f / %.0f", health, maxHealth);
        int textWidth = mc.fontRenderer.getStringWidth(hpText);
        mc.fontRenderer.drawStringWithShadow(hpText,
                x + (barWidth - textWidth) / 2f,
                y - 1,
                0xFFFFFFFF);

        GlStateManager.popMatrix();

        // Restore GL states that may have been polluted by Gui.drawRect
        // and fontRenderer.drawStringWithShadow
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
