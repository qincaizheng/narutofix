package com.qdd.narutofix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(targets ="net.narutomod.item.ItemMangekyoSharinganEternal$1" )
public abstract class mixinEternal extends net.narutomod.item.ItemDojutsu.Base{

    public mixinEternal(ArmorMaterial material) {
        super(material);
    }

    public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
        super.onUpdate(itemstack, world, entity, par4, par5);
    }

}

