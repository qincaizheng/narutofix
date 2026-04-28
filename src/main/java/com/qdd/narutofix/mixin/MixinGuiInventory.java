package com.qdd.narutofix.mixin;

import com.qdd.narutofix.cap.eye.EyeInventoryConstants;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiInventory.class)
public abstract class MixinGuiInventory extends GuiContainer {
    protected MixinGuiInventory(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Inject(method = "drawGuiContainerBackgroundLayer", at = @At(value = "TAIL"))
    private void narutofix$drawEquippedEyeSlot(float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
        int x = this.guiLeft + EyeInventoryConstants.EQUIPPED_SLOT_X - 1;
        int y = this.guiTop + EyeInventoryConstants.EQUIPPED_SLOT_Y - 1;
        Gui.drawRect(x, y, x + 18, y + 18, 0xFF8B8B8B);
        Gui.drawRect(x + 1, y + 1, x + 17, y + 17, 0xFF373737);
        Gui.drawRect(x + 2, y + 2, x + 16, y + 16, 0xFF141414);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}