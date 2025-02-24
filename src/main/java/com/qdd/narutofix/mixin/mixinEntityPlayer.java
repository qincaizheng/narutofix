package com.qdd.narutofix.mixin;

import com.mojang.authlib.GameProfile;
import com.qdd.narutofix.gui.CustomContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public class mixinEntityPlayer {
    @Shadow public Container inventoryContainer;

    @Shadow public InventoryPlayer inventory;

    @Inject(method = "<init>",at = @At(value = "CTOR_HEAD", unsafe = true))
    private void onInit(World worldIn, GameProfile gameProfileIn,CallbackInfo ci) {
        this.inventoryContainer=new CustomContainer(this.inventory, !worldIn.isRemote, this);
    }
}
