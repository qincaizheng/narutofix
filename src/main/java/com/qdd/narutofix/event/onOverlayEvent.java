package com.qdd.narutofix.event;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import com.qdd.narutofix.cap.body.BodyEnergyDataProvider;
import com.qdd.narutofix.cap.body.IBodyEnergyData;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.Chakra;
import net.narutomod.item.ItemJutsu;

import java.util.Objects;

@Mod.EventBusSubscriber
public class onOverlayEvent {
    private static final ResourceLocation DEFUALT = new ResourceLocation(NarutoFix.MODID,"textures/gui/default.png");
    private static final ResourceLocation ENERGY_HUD = new ResourceLocation(NarutoFix.MODID, "textures/gui/hud.png");
    private static final int HUD_BAR_WIDTH = 82;
    private static final int HUD_BAR_HEIGHT = 11;
    private static final int HUD_ROW_HEIGHT = 12;
    private static final int HUD_LABEL_GAP = 6;
    private static final int HUD_VALUE_GAP = 6;
    private static final int HUD_VALUE_WIDTH = 54;
    private static final int HUD_HOTBAR_WIDTH = 182;
    private static final int HUD_HOTBAR_HEIGHT = 22;
    private static final int HUD_HOTBAR_HORIZONTAL_GAP = 16;
    private static final int HUD_HOTBAR_VERTICAL_GAP = HUD_ROW_HEIGHT + 2;
    private static final int HUD_FRAME_V = 0;
    private static final int HUD_BODY_V = 20;    // yellow
    private static final int HUD_SOUL_V = 30;    // blue
    private static final int HUD_CHAKRA_V = 40;  // green

    private static final int FILL_BODY_V = 22;
    private static final int FILL_SOUL_V = 31;
    private static final int FILL_CHAKRA_V = 40;

    private static class EnergyHudLayout {
        private final int x;
        private final boolean showText;
        private final int labelColumnWidth;

        private EnergyHudLayout(int x, boolean showText, int labelColumnWidth) {
            this.x = x;
            this.showText = showText;
            this.labelColumnWidth = labelColumnWidth;
        }
    }
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event){
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;
        if(player.isSpectator()) return;
        int width = event.getResolution().getScaledWidth();
        int height = event.getResolution().getScaledHeight();
        if (Objects.requireNonNull(event.getType()) == RenderGameOverlayEvent.ElementType.HOTBAR) {
            renderEnergyHUD(player);
            if(player.hasCapability(JutsuInventoryCapability.Jutsu_INV, null)) {
                IJutsuInventory Jutsu_INV= player.getCapability(JutsuInventoryCapability.Jutsu_INV, null);
                renderSpellHUD(player, Jutsu_INV, width, height, event.getPartialTicks());
            }
        }


//        if (RenderGameOverlayEvent.ElementType.ALL.equals(event.getType())) {
//            Minecraft mc = Minecraft.getMinecraft();
//            Entity entity = mc.getRenderViewEntity();
//            if (entity instanceof EntityPlayer)
//            {
//                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//                mc.getTextureManager().bindTexture(WIDGETS);
//                IJutsuInventory inv = entity.getCapability(JutsuInventoryCapability.Jutsu_INV, null);
//                int i = event.getResolution().getScaledWidth() / 2;
//                GlStateManager.pushMatrix(); // 保存当前坐标系
//                GlStateManager.translate(0, 0, -100);
//                mc.ingameGUI.drawTexturedModalRect(i - 91,  22, 0, 0, 182, 22);
//                mc.ingameGUI.drawTexturedModalRect(i - 91 - 1 + inv.getSelected() * 20, 21, 0, 22, 24, 22);
//                GlStateManager.popMatrix();
//                GlStateManager.enableRescaleNormal();
//                GlStateManager.enableBlend();
//                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//                RenderHelper.enableGUIStandardItemLighting();
//
//                for (int l = 0; l < 9; ++l)
//                {
//                    int i1 = i - 90 + l * 20 + 2;
//                    int j1 = 25;
//                    mc.getRenderItem().renderItemAndEffectIntoGUI(inv.getItems().getStackInSlot(l),i1, j1);
//                }
//                RenderHelper.disableStandardItemLighting();
//                GlStateManager.disableRescaleNormal();
//                GlStateManager.disableBlend();
//            }
//        }
    }

    private static void renderSpellHUD(EntityPlayer player, IJutsuInventory Jutsu_INV, int width, int height, float partialTicks){

        GlStateManager.pushMatrix();
        int x=width;
        int y = height;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1, 1, 1);
        int x1 = x-24-2;
        // y is upside-down so this is the other way round
        int y1 = (y-192)/2;
        int num=0;
        for(int i =0 ; i<9; i++) {
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(Jutsu_INV.getItems().getStackInSlot(i), x1, y1+i*22+2);
            if(Jutsu_INV.getItems().getStackInSlot(i).getItem() instanceof ItemJutsu.Base) num++;
        }
        if(Jutsu_INV.isIzanagi()){
            Minecraft.getMinecraft().ingameGUI.drawString(Minecraft.getMinecraft().fontRenderer, I18n.format(NarutoFix.MODID+".text.isIzanaqi"),x-100,y-20,0xFFFFFF);
        }
        int select=Jutsu_INV.getSelected();
        x1 =  width-29;
        y1 =  y1 -2+ select*22;
        Minecraft.getMinecraft().renderEngine.bindTexture(DEFUALT);
        float scale = 0.6f;
        GlStateManager.scale(scale, scale, scale);
        if(num>0)Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x1/scale,y1/scale,0,12,36,36);


        GlStateManager.popMatrix();
    }

    private static void renderEnergyHUD(EntityPlayer player) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);
        int totalHeight = HUD_ROW_HEIGHT * 2 + HUD_BAR_HEIGHT;
        String soulLabel = I18n.format(NarutoFix.MODID + ".hud.soul");
        String bodyLabel = I18n.format(NarutoFix.MODID + ".hud.body");
        String chakraLabel = I18n.format(NarutoFix.MODID + ".hud.chakra");
        int labelColumnWidth = getLabelColumnWidth(mc, soulLabel, bodyLabel, chakraLabel);
        EnergyHudLayout layout = getHudLayout(resolution, labelColumnWidth);
        int y = getHudTop(resolution, totalHeight);

        ISoulEnergyData soul = SoulEnergyDataProvider.get(player);
        IBodyEnergyData body = BodyEnergyDataProvider.get(player);
        Chakra.Pathway chakra = Chakra.pathway(player);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        drawEnergyBar(mc, soulLabel, layout.x, y, FILL_SOUL_V,
                soul == null ? 0.0D : soul.getCurrent(),
                soul == null ? 0.0D : soul.getMax(), layout);
        drawEnergyBar(mc, bodyLabel, layout.x, y + HUD_ROW_HEIGHT, FILL_BODY_V,
                body == null ? 0.0D : body.getCurrent(),
                body == null ? 0.0D : body.getMax(), layout);
        drawEnergyBar(mc, chakraLabel, layout.x, y + HUD_ROW_HEIGHT * 2, FILL_CHAKRA_V,
                chakra == null ? 0.0D : chakra.getAmount(),
                chakra == null ? 0.0D : chakra.getMax(), layout);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static int getLabelColumnWidth(Minecraft mc, String soulLabel, String bodyLabel, String chakraLabel) {
        int maxLabelWidth = Math.max(mc.fontRenderer.getStringWidth(soulLabel),
                Math.max(mc.fontRenderer.getStringWidth(bodyLabel), mc.fontRenderer.getStringWidth(chakraLabel)));
        return maxLabelWidth + HUD_LABEL_GAP;
    }

    private static EnergyHudLayout getHudLayout(ScaledResolution resolution, int labelColumnWidth) {
        int hotbarLeft = resolution.getScaledWidth() / 2 - HUD_HOTBAR_WIDTH / 2;
        int availableWidth = Math.max(0, hotbarLeft - HUD_HOTBAR_HORIZONTAL_GAP);
        int textWidth = labelColumnWidth + HUD_BAR_WIDTH + HUD_VALUE_GAP + HUD_VALUE_WIDTH;
        boolean showText = availableWidth >= textWidth;
        int requiredWidth = showText ? textWidth : HUD_BAR_WIDTH;
        int leftSlack = Math.max(0, availableWidth - requiredWidth);
        int left = leftSlack * 2 / 5 + Configs.hudXOffset;
        int maxLeft = Math.max(0, availableWidth - requiredWidth);
        return new EnergyHudLayout(Math.max(0, Math.min(left, maxLeft)), showText, labelColumnWidth);
    }

    private static int getHudTop(ScaledResolution resolution, int totalHeight) {
        int hotbarTop = resolution.getScaledHeight() - HUD_HOTBAR_HEIGHT;
        return Math.max(0, hotbarTop - totalHeight - HUD_HOTBAR_VERTICAL_GAP + Configs.hudYOffset);
    }

    private static void drawEnergyBar(Minecraft mc, String label, int x, int y, int fillV, double current, double max, EnergyHudLayout layout) {
        double ratio = max <= 0.0D ? 0.0D : Math.max(0.0D, Math.min(1.0D, current / max));
        int barX = layout.showText ? x + layout.labelColumnWidth : x;
        int valueStartX = barX + HUD_BAR_WIDTH + HUD_VALUE_GAP;
        int textY = y + 2;
        int fillWidth = (int) ((HUD_BAR_WIDTH - 4) * ratio);
        String value = (int) current + "/" + (int) max;
        int valueX = valueStartX + Math.max(0, HUD_VALUE_WIDTH - mc.fontRenderer.getStringWidth(value));

        mc.renderEngine.bindTexture(ENERGY_HUD);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.ingameGUI.drawTexturedModalRect(barX, y, 0, HUD_FRAME_V, HUD_BAR_WIDTH, HUD_BAR_HEIGHT);
        if (fillWidth > 0) {
            mc.ingameGUI.drawTexturedModalRect(barX + 2, y + 2, 2, fillV, fillWidth, 7);
        }
        mc.ingameGUI.drawTexturedModalRect(barX, y, 0, HUD_FRAME_V, HUD_BAR_WIDTH, HUD_BAR_HEIGHT);

        if (layout.showText) {
            mc.fontRenderer.drawStringWithShadow(label, x, textY, 0xFFFFFFFF);
            mc.fontRenderer.drawStringWithShadow(value, valueX, textY, 0xFFFFFFFF);
        }
    }

}
