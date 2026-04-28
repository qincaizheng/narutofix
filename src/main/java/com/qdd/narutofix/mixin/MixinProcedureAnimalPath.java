package com.qdd.narutofix.mixin;

import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.entity.EntityGiantDog2h;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.procedure.ProcedureAnimalPath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureAnimalPath.class)
public abstract class MixinProcedureAnimalPath {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$useVirtualEye(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        Object worldObject = dependencies.get("world");
        if (!(entity instanceof EntityPlayer) || !(worldObject instanceof net.minecraft.world.World)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        if (!DojutsuEyeHelper.hasVirtualEye(player, ModItems.SIX_TOMOE_RINNEGAN)) {
            return;
        }

        net.minecraft.world.World world = (net.minecraft.world.World) worldObject;
        double summonedId = 0;
        if (player instanceof EntityLivingBase) {
            ((EntityLivingBase) player).swingArm(net.minecraft.util.EnumHand.MAIN_HAND);
        }

        ItemStack eyeStack = DojutsuEyeHelper.getCapabilityEye(player);
        summonedId = eyeStack.hasTagCompound() ? eyeStack.getTagCompound().getDouble("SummonedAnimal_id") : -1;
        if (summonedId <= 0) {
            if (!world.isRemote) {
                if (Chakra.pathway(player).consume(ItemRinnegan.getAnimalPathChakraUsage(player))) {
                    world.playSound(null, player.posX, player.posY, player.posZ,
                            net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("narutomod:kuchiyosenojutsu")),
                            net.minecraft.util.SoundCategory.NEUTRAL, 2.0F, 0.8F);
                    EntityGiantDog2h.EntityCustom entityToSpawn = new EntityGiantDog2h.EntityCustom(player);
                    Particles.spawnParticle(world, Particles.Types.SEAL_FORMULA, entityToSpawn.posX, entityToSpawn.posY + 0.015d, entityToSpawn.posZ,
                            1, 0d, 0d, 0d, 0d, 0d, 0d, 200, 0, 60);
                    if (world instanceof net.minecraft.world.WorldServer) {
                        ((net.minecraft.world.WorldServer) world).spawnParticle(net.minecraft.util.EnumParticleTypes.EXPLOSION_HUGE,
                                entityToSpawn.posX, player.posY + (player.height / 2), entityToSpawn.posZ, 300, 4, player.height, 4, 1, new int[0]);
                    }
                    player.world.spawnEntity(entityToSpawn);
                    if (!eyeStack.hasTagCompound()) {
                        eyeStack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                    }
                    eyeStack.getTagCompound().setDouble("SummonedAnimal_id", entityToSpawn.getEntityId());
                } else {
                    Chakra.pathway(player).warningDisplay();
                }
            }
        } else {
            if (!eyeStack.hasTagCompound()) {
                eyeStack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
            }
            eyeStack.getTagCompound().setDouble("SummonedAnimal_id", 0);
            Entity summoned = world.getEntityByID((int) summonedId);
            if (summoned instanceof EntityGiantDog2h.EntityCustom) {
                if (world instanceof net.minecraft.world.WorldServer) {
                    ((net.minecraft.world.WorldServer) world).spawnParticle(net.minecraft.util.EnumParticleTypes.EXPLOSION_HUGE,
                            summoned.posX, summoned.posY + 15, summoned.posZ, 200, 4, 15, 4, 1, new int[0]);
                }
                summoned.world.removeEntity(summoned);
            }
        }
        ci.cancel();
    }
}