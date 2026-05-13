package com.qdd.narutofix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.narutomod.PlayerTracker;
import net.narutomod.item.ItemKamuiShuriken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemKamuiShuriken.EntityKamuiShuriken.class)
public abstract class MixinEntityKamuiShuriken {

    @Shadow(remap = false)
    public abstract float getScale();

    @Inject(method = "onImpact", at = @At("HEAD"), cancellable = true, remap = false)
    private void narutofix$boostDamage(RayTraceResult result, CallbackInfo ci) {
        if (result.entityHit == null) return;
        Entity self = (Entity) (Object) this;
        if (!self.getEntityData().hasKey("narutofix_kamui_damage_mult")) return;

        // Skip owner chain
        for (Entity e = self; e != null; e = e.getRidingEntity())
            if (result.entityHit.equals(e)) return;

        // Replace damage calculation with a fixed high value
        if (!self.world.isRemote) {
            EntityLivingBase shooter = ((ItemKamuiShuriken.EntityKamuiShuriken)(Object)this).getThrower();
            double bxp = shooter instanceof EntityPlayer ? PlayerTracker.getBattleXp((EntityPlayer) shooter) : 0;
            float damage = 80.0F + (float)(bxp * 0.005D);
            float scale = this.getScale();
            damage *= Math.max(1.0F, scale * 0.3F);

            if (result.entityHit instanceof EntityLivingBase) {
                ((EntityLivingBase) result.entityHit).attackEntityFrom(
                        DamageSource.causePlayerDamage((EntityPlayer) shooter), damage);
            } else {
                result.entityHit.attackEntityFrom(DamageSource.OUT_OF_WORLD, damage);
            }
        }
        self.setDead();
        ci.cancel();
    }
}
