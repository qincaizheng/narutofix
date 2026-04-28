package com.qdd.narutofix.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.narutomod.entity.EntityBeamBase;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.entity.EntitySealingChains;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySealingChains.EC.class)
public abstract class mixinSealingChains extends EntityBeamBase.Base{

    public mixinSealingChains(World a) {
        super(a);
    }

    @Shadow(remap = false)
    protected abstract EntityLivingBase getTarget();


    @Inject(method = "onUpdate",at= @At(value = "INVOKE", target = "Lnet/narutomod/entity/EntitySealingChains$EC;getDistance(Lnet/minecraft/entity/Entity;)F",shift = At.Shift.AFTER) ,remap = false)
    public void onUpdate(CallbackInfo ci) {
        EntityLivingBase target = this.getTarget();
        if (target instanceof EntityBijuManager.ITailBeast && this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).isCreative() && ProcedureUtils.getModifiedSpeed(target) < 0.05) {
            this.shootingEntity.sendMessage(new TextComponentTranslation("narutofix.msg.sealingchains"));
            ((EntityBijuManager.ITailBeast)target).fuuinIntoVessel(this.shootingEntity, 400);
        }
    }

}
