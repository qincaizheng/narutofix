package com.qdd.narutofix.gui;



import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public class CustomGuiContainer extends GuiInventory {
    private final EntityPlayer player = Minecraft.getMinecraft().player;
    private float oldMouseX;
    private float oldMouseY;

    public CustomGuiContainer(EntityPlayer player) {
        super(resetinventoryContainer(player));
    }

    private static EntityPlayer resetinventoryContainer(EntityPlayer player) {
        player.inventoryContainer= new CustomContainer(player.inventory,!player.world.isRemote,player);
        return player;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer (float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        drawCustomSlot(i - 3, j - 18); // 新增第一个头盔槽
        drawCustomSlot(i + 15, j - 18); // 新增第二个头盔槽
        drawEntityOnScreen(i + 51, j + 75, 30, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, this.mc.player);
    }

    private void drawCustomSlot(int x, int y) {
        // 绘制原版的槽图案，这里假设槽图案是32x32的区域
        int slotSize = 18; // 每个槽的大小是18x18像素，符合原版物品栏的标准
        int u = 7; // 原版槽图案的起始X坐标
        int v = 7; // 原版槽图案的起始Y坐标

        // 绘制原版物品槽
        this.drawTexturedModalRect(x, y, u, v, slotSize, slotSize);
    }
}




/*
public class CustomGuiContainer extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/inventory.png");
    public CustomGuiContainer(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // 绑定原版物品栏的贴图
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);

        // 计算绘制位置
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        // 绘制原版物品栏背景
//        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        // 绘制两个新增的头盔槽
        // 假设我们的位置是在原版头盔槽的旁边
        drawCustomSlot(x - 3, y - 18); // 新增第一个头盔槽
        drawCustomSlot(x + 15, y - 18); // 新增第二个头盔槽
//        GuiInventory.drawEntityOnScreen(this.width / 2 - 35, this.height / 2-7 , 35,(float) x-mouseX+50,(float) y-mouseY+22,this.mc.player);
    }
    private void drawCustomSlot(int x, int y) {
        // 绘制原版的槽图案，这里假设槽图案是32x32的区域
        int slotSize = 18; // 每个槽的大小是18x18像素，符合原版物品栏的标准
        int u = 7; // 原版槽图案的起始X坐标
        int v = 7; // 原版槽图案的起始Y坐标

        // 绘制原版物品槽
        this.drawTexturedModalRect(x, y, u, v, slotSize, slotSize);
    }
}
*/