package com.qdd.narutofix.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import com.qdd.narutofix.items.Sharingan1;
import net.minecraftforge.items.ItemHandlerHelper;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.procedure.ProcedureKGDistribution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureKGDistribution.class)
public class mixinKGD {
    @Inject(method = "executeProcedure",at= @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V"), cancellable = true,remap = false)
    private static void executeProcedure(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity)dependencies.get("entity");
        Advancement adv = ((EntityPlayerMP)entity).server.getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:byakuganopened"));
        AdvancementProgress ap = ((EntityPlayerMP)entity).getAdvancements().getProgress(adv);
        if(ap.isDone()){
            ItemStack stack = new ItemStack(Sharingan1.helmet,1);
            ((ItemDojutsu.Base)stack.getItem()).setOwner(stack, (EntityLivingBase)entity);
            ItemHandlerHelper.giveItemToPlayer((EntityPlayer)entity, stack);
            ci.cancel();
        }
    }
}
