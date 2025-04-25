package com.qdd.narutofix.mixin;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.items.Sharingan1;
import com.qdd.narutofix.items.Sharingan2;
import com.qdd.narutofix.potion.PotionLoader;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.narutomod.Chakra;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chakra.Pathway.class)
public abstract class mixinPathway<T extends EntityLivingBase> {
    @Shadow(remap = false) @Final protected T user;

    @Shadow(remap = false)
    public abstract double getMax();

    @Inject(method = "onUpdate",at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;addPotionEffect(Lnet/minecraft/potion/PotionEffect;)V"),cancellable = true)
    private void onUpdate(CallbackInfo ci) {
        if(this.user.isPotionActive(PotionLoader.PotionIzanagi)){
            ci.cancel();
        }
    }

    @Inject(method = "onUpdate",at= @At(value = "HEAD"),remap = false)
    private void onUpdate2(CallbackInfo ci) {
        Item item =user.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem();
        if(item== Sharingan1.helmet||item== Sharingan2.helmet||item== ItemSharingan.helmet){
            ItemStack newitem;
           if(this.getMax()<= Configs.upgrade &&item!=Sharingan1.helmet){
               newitem=new ItemStack(Sharingan1.helmet,1);
               ((ItemDojutsu.Base)newitem.getItem()).setOwner(newitem, user);
               user.setItemStackToSlot(EntityEquipmentSlot.HEAD,newitem);
           } else if (this.getMax()<=Configs.upgrade*2&&item!=Sharingan2.helmet&&this.getMax()>Configs.upgrade) {
               newitem=new ItemStack(Sharingan2.helmet,1);
               ((ItemDojutsu.Base)newitem.getItem()).setOwner(newitem, user);
               user.setItemStackToSlot(EntityEquipmentSlot.HEAD,newitem);
           }else if(this.getMax()>Configs.upgrade*2){
               newitem=new ItemStack(ItemSharingan.helmet,1);
               ((ItemDojutsu.Base)newitem.getItem()).setOwner(newitem, user);
               user.setItemStackToSlot(EntityEquipmentSlot.HEAD,newitem);
           }

        }
    }
}
