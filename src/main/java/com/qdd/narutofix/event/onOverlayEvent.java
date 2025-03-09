package com.qdd.narutofix.event;

import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemSharingan;

import java.util.Objects;

@Mod.EventBusSubscriber
public class onOverlayEvent {
    private static final ResourceLocation DEFUALT = new ResourceLocation(NarutoFix.MODID,"textures/gui/default.png");
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event){
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player.isSpectator()) return;
        int width = event.getResolution().getScaledWidth();
        int height = event.getResolution().getScaledHeight();
        if(!player.hasCapability(JutsuInventoryCapability.Jutsu_INV, null))return;
        IJutsuInventory Jutsu_INV= player.getCapability(JutsuInventoryCapability.Jutsu_INV, null);
        if (Objects.requireNonNull(event.getType()) == RenderGameOverlayEvent.ElementType.HOTBAR) {
            renderSpellHUD(player, Jutsu_INV, width, height, event.getPartialTicks());
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

}
