package com.qdd.narutofix.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemSharingan.class)
public class mixinItemSharingan {
    /**
     * @author qdd
     * @reason 123
     */
    @Overwrite(remap = false)
    public static boolean wearingAny(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer){
            EntityPlayer Player = (EntityPlayer) entity;
            for(int i=3;i<Player.inventory.armorInventory.size();i++){
                if(Player.inventory.armorInventory.get(i).getItem() instanceof ItemSharingan.Base) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @author qdd
     * @reason 123
     */
    @Overwrite(remap = false)
    public static boolean isWearingMangekyo(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer){
            EntityPlayer Player = (EntityPlayer) entity;
            for(int i=3;i<Player.inventory.armorInventory.size();i++){
                Item item = Player.inventory.armorInventory.get(i).getItem();
                if(item instanceof ItemSharingan.Base && item!=ItemSharingan.helmet) {
                    return true;
                }
            }
        }
        return false;
    }


}
