package com.qdd.narutofix.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import com.qdd.narutofix.container.JutsuContainer;
import com.qdd.narutofix.keybind.KeyLoader;

import javax.annotation.Nullable;
import java.io.IOException;

public class GuiJutsuCntainer extends GuiContainer {
    private static final String NarutoFix_PATH = NarutoFix.MODID + ":" + "textures/gui/container/jutsucontainer.png";
    private static final ResourceLocation TEXTURE = new ResourceLocation(NarutoFix_PATH);
    private int IzanagiSize;
    public GuiJutsuCntainer(JutsuContainer container) {
        super(container);
        IzanagiSize=container.player.getCapability(JutsuInventoryCapability.Jutsu_INV, null).getIzanagiSize();
        xSize = 176;
        ySize = 164;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        final int k = (this.width - this.xSize) / 2;
        final int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
//        this.drawTexturedModalRect(k+(this.xSize-20)/2,l-20,0,this.ySize,20,20);
        if(this.IzanagiSize>0){
            for (int i = 0; i < this.IzanagiSize; i++) {
                this.drawTexturedModalRect(k+this.xSize+4,l+i*20,0,this.ySize,20,20);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == KeyLoader.openjutsugui.getKeyCode()) {
            this.mc.player.closeScreen();
        }
        super.keyTyped(typedChar, keyCode);
    }

}
