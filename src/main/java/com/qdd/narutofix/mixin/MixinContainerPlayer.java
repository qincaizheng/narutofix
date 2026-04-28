package com.qdd.narutofix.mixin;

import com.qdd.narutofix.cap.eye.EquippedEyeItemHandler;
import com.qdd.narutofix.cap.eye.EyeInventoryConstants;
import com.qdd.narutofix.cap.eye.SlotDojutsuEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerPlayer.class)
public abstract class MixinContainerPlayer extends Container {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void narutofix$addEquippedEyeSlot(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer player, CallbackInfo ci) {
        this.addSlotToContainer(new SlotDojutsuEye(new EquippedEyeItemHandler(player), 0,
                EyeInventoryConstants.EQUIPPED_SLOT_X, EyeInventoryConstants.EQUIPPED_SLOT_Y,
                player, false));
    }
}