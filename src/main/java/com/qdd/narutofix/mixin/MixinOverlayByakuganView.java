package com.qdd.narutofix.mixin;

import com.qdd.narutofix.items.ItemSixTomoeRinnegan;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.narutomod.gui.overlay.OverlayByakuganView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverlayByakuganView.GUIRenderEventClass.class)
public abstract class MixinOverlayByakuganView {
    @Inject(method = "eventHandler", at = @At("HEAD"), cancellable = true, remap = false)
    private void narutofix$skipByakuganOverlayForSixTomoe(RenderGameOverlayEvent event, CallbackInfo ci) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HELMET) {
            return;
        }

        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }

        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!helmet.isEmpty() && helmet.getItem() instanceof ItemSixTomoeRinnegan) {
            ci.cancel();
        }
    }
}
