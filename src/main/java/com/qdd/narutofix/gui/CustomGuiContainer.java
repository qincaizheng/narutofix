package com.qdd.narutofix.gui;



import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;


public class CustomGuiContainer extends InventoryEffectRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/inventory.png");
    private float oldMouseX;
    /** The old y position of the mouse pointer */
    private float oldMouseY;
    private GuiButtonImage recipeButton;
    private final GuiRecipeBook recipeBookGui = new GuiRecipeBook();
    private boolean widthTooNarrow;
    private boolean buttonClicked;
    public CustomGuiContainer(CustomContainer container) {
        super(container);
        this.allowUserInput = true;
    }
    private void resetGuiLeft()
    {
        this.guiLeft = (this.width - this.xSize) / 2;
    }
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        updateActivePotionEffects();
        resetGuiLeft();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();

        super.initGui();

        this.widthTooNarrow = this.width < 379;
        this.recipeBookGui.func_194303_a(this.width, this.height, this.mc, this.widthTooNarrow, ((ContainerPlayer)this.inventorySlots).craftMatrix);
        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
        this.recipeButton = new GuiButtonImage(10, this.guiLeft + 104, this.height / 2 - 22, 20, 18, 178, 0, 19, INVENTORY_BACKGROUND);
        this.buttonList.add(this.recipeButton);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString(I18n.format("container.crafting"), 97, 8, 4210752);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.hasActivePotionEffects = !this.recipeBookGui.isVisible();

        if (this.recipeBookGui.isVisible() && this.widthTooNarrow)
        {
            this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
            this.recipeBookGui.render(mouseX, mouseY, partialTicks);
        }
        else
        {
            this.recipeBookGui.render(mouseX, mouseY, partialTicks);
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, false, partialTicks);
        }

        this.renderHoveredToolTip(mouseX, mouseY);
        this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
        this.oldMouseX = (float)mouseX;
        this.oldMouseY = (float)mouseY;
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        drawCustomSlot(i - 3, j - 18); // 新增第一个头盔槽
        drawCustomSlot(i + 15, j - 18); // 新增第二个头盔槽
        GuiInventory.drawEntityOnScreen(i + 51, j + 75, 30, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, this.mc.player);
    }



    /**
     * Test if the 2D point is in a rectangle (relative to the GUI). Args : rectX, rectY, rectWidth, rectHeight, pointX,
     * pointY
     */
    protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY)
    {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (!this.recipeBookGui.mouseClicked(mouseX, mouseY, mouseButton))
        {
            if (!this.widthTooNarrow || !this.recipeBookGui.isVisible())
            {
                super.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    /**
     * Called when a mouse button is released.
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.buttonClicked)
        {
            this.buttonClicked = false;
        }
        else
        {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    protected boolean hasClickedOutside(int p_193983_1_, int p_193983_2_, int p_193983_3_, int p_193983_4_)
    {
        boolean flag = p_193983_1_ < p_193983_3_ || p_193983_2_ < p_193983_4_ || p_193983_1_ >= p_193983_3_ + this.xSize || p_193983_2_ >= p_193983_4_ + this.ySize;
        return this.recipeBookGui.hasClickedOutside(p_193983_1_, p_193983_2_, this.guiLeft, this.guiTop, this.xSize, this.ySize) && flag;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 10)
        {
            this.recipeBookGui.initVisuals(this.widthTooNarrow, ((ContainerPlayer)this.inventorySlots).craftMatrix);
            this.recipeBookGui.toggleVisibility();
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            this.recipeButton.setPosition(this.guiLeft + 104, this.height / 2 - 22);
            this.buttonClicked = true;
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!this.recipeBookGui.keyPressed(typedChar, keyCode))
        {
            super.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    public void recipesUpdated()
    {
        this.recipeBookGui.recipesUpdated();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        this.recipeBookGui.removed();
        super.onGuiClosed();
    }

    public GuiRecipeBook func_194310_f()
    {
        return this.recipeBookGui;
    }


//     @Override
//     protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
//     {
//         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//         this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
//         int i = this.guiLeft;
//         int j = this.guiTop;
//         this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
//         drawCustomSlot(i - 3, j - 18); // 新增第一个头盔槽
//         drawCustomSlot(i + 15, j - 18); // 新增第二个头盔槽
//         drawEntityOnScreen(i + 51, j + 75, 30, (float)(i + 51) - mouseX, (float)(j + 75 - 50) - mouseY, this.mc.player);

//     }
    private void drawCustomSlot(int x, int y) {
        // 绘制原版的槽图案，这里假设槽图案是32x32的区域
        int slotSize = 18; // 每个槽的大小是18x18像素，符合原版物品栏的标准
        int u = 7; // 原版槽图案的起始X坐标
        int v = 7; // 原版槽图案的起始Y坐标

        // 绘制原版物品槽
        this.drawTexturedModalRect(x, y, u, v, slotSize, slotSize);
    }
}


/**
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
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        // 绘制两个新增的头盔槽
        // 假设我们的位置是在原版头盔槽的旁边
        drawCustomSlot(x - 3, y - 18); // 新增第一个头盔槽
        drawCustomSlot(x + 15, y - 18); // 新增第二个头盔槽
        GuiInventory.drawEntityOnScreen(this.width / 2 - 35, this.height / 2-7 , 35,(float) x-mouseX+50,(float) y-mouseY+22,this.mc.player);
    }
    private void drawCustomSlot(int x, int y) {
        // 绘制原版的槽图案，这里假设槽图案是32x32的区域
        int slotSize = 18; // 每个槽的大小是18x18像素，符合原版物品栏的标准
        int u = 7; // 原版槽图案的起始X坐标
        int v = 7; // 原版槽图案的起始Y坐标

        // 绘制原版物品槽
        this.drawTexturedModalRect(x, y, u, v, slotSize, slotSize);
    }
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }



}
*/