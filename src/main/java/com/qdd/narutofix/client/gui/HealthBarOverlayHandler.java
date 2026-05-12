package com.qdd.narutofix.client.gui;

import com.qdd.narutofix.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Replaces the vanilla heart rendering with a compact progress bar.
 * Uses Pre(HEALTH) to cancel hearts, then draws a bar from hud.png.
 * GL state is restored for subsequent ARMOR/FOOD elements.
 */
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(value = Side.CLIENT)
public class HealthBarOverlayHandler {

    private static final ResourceLocation HUD_TEX = new ResourceLocation("narutofix:textures/gui/hud.png");

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

        event.setCanceled(true);

        net.minecraftforge.client.GuiIngameForge.left_height += 11;

        int barWidth = 82;
        int barHeight = 11;
        int x = event.getResolution().getScaledWidth() / 2 - 91;
        int y = event.getResolution().getScaledHeight() - 39;

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float ratio = Math.min(1.0f, health / maxHealth);
        int fillWidth = Math.max(0, (int) ((barWidth - 4) * ratio));

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);

        mc.renderEngine.bindTexture(HUD_TEX);

        // Background frame (V=0)
        mc.ingameGUI.drawTexturedModalRect(x, y, 0, 0, barWidth, barHeight);

        // Red fill inside the frame (V=40)
        if (fillWidth > 0) {
            mc.ingameGUI.drawTexturedModalRect(x + 2, y + 2, 2, 13, fillWidth, 7);
        }

        // Re-draw frame on top to keep border
        mc.ingameGUI.drawTexturedModalRect(x, y, 0, 0, barWidth, barHeight);

        // HP text
        String hpText = String.format("%.0f / %.0f", health, maxHealth);
        int textWidth = mc.fontRenderer.getStringWidth(hpText);
        mc.fontRenderer.drawStringWithShadow(hpText,
                x + (barWidth - textWidth) / 2f,
                y + 1,
                0xFFFFFFFF);

        GlStateManager.enableBlend();

        GlStateManager.popMatrix();

        // Restore GL state for subsequent ARMOR/FOOD
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        mc.renderEngine.bindTexture(Gui.ICONS);
    }
}
