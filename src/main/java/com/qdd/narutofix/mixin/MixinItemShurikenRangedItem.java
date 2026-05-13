package com.qdd.narutofix.mixin;

import com.qdd.narutofix.entity.susanoo.SusanooEntityBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.narutomod.item.ItemShuriken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemShuriken.RangedItem.class)
public abstract class MixinItemShurikenRangedItem {

    @Inject(method = "onPlayerStoppedUsing", at = @At("HEAD"), cancellable = true, remap = false)
    private void narutofix$checkNarutofixSusanoo(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft, CallbackInfo ci) {
        if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP) {
            EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
            if (entity.getRidingEntity() instanceof SusanooEntityBase) {
                SusanooEntityBase susanoo = (SusanooEntityBase) entity.getRidingEntity();
                Vec3d vec = entity.getLookVec();
                susanoo.attackEntityRanged(vec.x, vec.y, vec.z);
                if (!entity.capabilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
                ci.cancel();
            }
        }
    }
}
