package com.qdd.narutofix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import com.qdd.narutofix.AI.EntityAISusanoo;
import com.qdd.narutofix.AI.SusanooAIOwnerHurtTarget;
import com.qdd.narutofix.Configs;
import net.narutomod.Particles;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntitySusanooBase.class)
public abstract class mixinSusanooBase extends EntityCreature {

    public mixinSusanooBase(World worldIn) {
        super(worldIn);
    }

    @Shadow(remap = false)
    @Final
    private static  DataParameter<Integer> FLAME_COLOR;

    @Shadow(remap = false)
    protected abstract void consumeChakra();

    @Shadow(remap = false)
    protected abstract void clampMotion(double d);

    @Shadow(remap = false)
    public abstract EntityLivingBase getOwnerPlayer();

    @Shadow(remap = false)
    public abstract int getFlameColor();

    @Shadow(remap = false)
    protected abstract void setFlameColor(int color);

    @Inject(method = "onLivingUpdate",at=@At("HEAD"),cancellable = true)
    public void onLivingUpdate(CallbackInfo ci) {
        ItemStack helmetstack = this.getOwnerPlayer().getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        int color = ((ItemSharingan.Base)helmetstack.getItem()).getColor(helmetstack);
        if(getFlameColor()!=color){
            setFlameColor( color);
        }
        if(Configs.unride){
            EntityLivingBase ownerPlayer = this.getOwnerPlayer();
            boolean flag = ownerPlayer instanceof EntityPlayer;
            EntityAISusanoo aiFollow =new EntityAISusanoo((EntitySusanooBase)(Object)this,1,10,64);
            SusanooAIOwnerHurtTarget aiTarget= new SusanooAIOwnerHurtTarget((EntitySusanooBase)(Object)this) ;
            EntityAIAttackMelee aiAttackMelee =new EntityAIAttackMelee(this, (double)5.0F, true);
            if (!this.world.isRemote && (ownerPlayer == null || !ownerPlayer.isEntityAlive() || ownerPlayer instanceof EntityPlayerMP && ((EntityPlayerMP)ownerPlayer).hasDisconnected() || !flag)) {
                this.setDead();
            }

            if (flag) {
                if (!((EntityPlayer)ownerPlayer).isCreative()) {
                    if (!this.world.isRemote) {
                        this.consumeChakra();
                    }
                }
                if(!this.isBeingRidden()){
                    this.setNoAI(false);
                    this.tasks.addTask(2,aiFollow);
                    this.targetTasks.addTask(1,aiTarget);
                    this.tasks.addTask(1,aiAttackMelee);
                }else{
                    this.tasks.removeTask(aiFollow);
                    this.targetTasks.removeTask(aiTarget);
                    this.tasks.removeTask(aiAttackMelee);
                    this.setNoAI(true);
                }

                if (!this.world.isRemote && this.ticksExisted % 20 == 1) {
                    ownerPlayer.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 22, 6, false, false));
                }
            }

            this.updateArmSwingProgress();
            super.onLivingUpdate();
            this.clampMotion(0.05);
            if (this.ticksExisted % 30 == 0) {
                this.playSound((SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("block.fire.ambient")), 1.0F, this.rand.nextFloat() * 0.7F + 0.3F);
            }

            for(int i = 0; i < (int)this.height; ++i) {
                double d0 = this.posX + ((double)this.rand.nextFloat() - (double)0.5F) * (double)this.width;
                double d1 = this.posY + (double)(this.rand.nextFloat() * this.height);
                double d2 = this.posZ + ((double)this.rand.nextFloat() - (double)0.5F) * (double)this.width;
                this.world.spawnAlwaysVisibleParticle(Particles.Types.FLAME.getID(), d0, d1, d2, (double)0.0F, 0.05, (double)0.0F, new int[]{this.getFlameColor(), (int)(this.width * 15.0F)});
            }

            ci.cancel();
        }

    }

    @Inject(method = "attackEntityAsMob",at =@At("HEAD"),cancellable = true)
    public void mixinattackEntity(Entity entityIn, CallbackInfoReturnable<Boolean> cir){
        if(!this.isBeingRidden()) {
            boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
            if (flag) {
                this.applyEnchantments(this, entityIn);
            }

            cir.setReturnValue(flag);
        }
    }



}
