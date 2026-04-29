package com.qdd.narutofix.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.narutomod.Chakra;
import net.narutomod.item.ItemMilitaryRationsPill;
import net.narutomod.item.ItemMilitaryRationsPillGold;
import net.narutomod.potion.PotionChakraRegeneration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ItemMilitaryRationsPill.ItemFoodCustom.class, ItemMilitaryRationsPillGold.ItemFoodCustom.class})
public class MixinItemMilitaryRationsPillFood {
    @Redirect(method = "onFoodEaten", at = @At(value = "INVOKE",
            target = "Lnet/narutomod/Chakra$PathwayPlayer;consume(DZ)Z"), remap = false)
    private boolean narutofix$skipDirectChakraRestore(Chakra.PathwayPlayer pathway, double amount, boolean force) {
        return true;
    }

    @Redirect(method = "onFoodEaten", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/EntityPlayer;addPotionEffect(Lnet/minecraft/potion/PotionEffect;)V"))
    private void narutofix$skipChakraRegenPotion(EntityPlayer player, PotionEffect effect) {
        if (effect.getPotion() != PotionChakraRegeneration.potion) {
            player.addPotionEffect(effect);
        }
    }
}
