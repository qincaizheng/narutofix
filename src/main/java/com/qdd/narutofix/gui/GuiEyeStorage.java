package com.qdd.narutofix.gui;

import com.qdd.narutofix.gui.ContainerEyeStorage;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiEyeStorage extends GuiContainer {
    public GuiEyeStorage(ContainerEyeStorage inventorySlotsIn) {
        super(inventorySlotsIn);
        this.xSize = 176;
        this.ySize = 133;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString("Eye Storage", 8, 6, 0x404040);
        this.fontRenderer.drawString(this.mc.player.inventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int left = this.guiLeft;
        int top = this.guiTop;
        Gui.drawRect(left, top, left + this.xSize, top + this.ySize, 0xF0101010);
        Gui.drawRect(left + 3, top + 3, left + this.xSize - 3, top + this.ySize - 3, 0xFFBEB79E);
        Gui.drawRect(left + 4, top + 4, left + this.xSize - 4, top + this.ySize - 4, 0xFF1F1B18);

        for (int index = 0; index < 3; index++) {
            this.drawSlotFrame(left + 43 + index * 18, top + 19);
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.drawSlotFrame(left + 7 + column * 18, top + 50 + row * 18);
            }
        }

        for (int column = 0; column < 9; column++) {
            this.drawSlotFrame(left + 7 + column * 18, top + 108);
        }
    }

    private void drawSlotFrame(int x, int y) {
        Gui.drawRect(x, y, x + 18, y + 18, 0xFF8B8B8B);
        Gui.drawRect(x + 1, y + 1, x + 17, y + 17, 0xFF373737);
        Gui.drawRect(x + 2, y + 2, x + 16, y + 16, 0xFF141414);
    }
}