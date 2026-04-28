package com.qdd.narutofix.mixin;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import com.qdd.narutofix.Configs;
import com.qdd.narutofix.items.Sharingan1;
import com.qdd.narutofix.items.Sharingan2;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemSharingan.PlayerHook.class)
public class mixinPlayerHook{
    @Inject(method ="onMouseEvent" ,at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setBoolean(Ljava/lang/String;Z)V"),cancellable = true)
    public void onMouseEvent(CallbackInfo ci) {
        ci.cancel();
//        System.out.println("[Mixin] 鼠标事件已拦截");
    }

    @Inject(method = "onAttacked",at = @At(value = "INVOKE", target = "Lnet/narutomod/procedure/ProcedureUtils;getAllAirBlocks(Lnet/minecraft/world/World;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;"),remap = false,cancellable = true)
    public void mixinonAttackEvent(LivingAttackEvent event,CallbackInfo ci) {
        Item head=event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem();
        if( head!= Sharingan1.helmet && head!= Sharingan2.helmet){
            event.setCanceled(true);
        } else if (head == Sharingan1.helmet && event.getAmount()< Configs.miss1) {
            event.setCanceled(true);
        }else if (head == Sharingan2.helmet && event.getAmount()<Configs.miss2) {
            event.setCanceled(true);
        }
        ci.cancel();
    }
}
