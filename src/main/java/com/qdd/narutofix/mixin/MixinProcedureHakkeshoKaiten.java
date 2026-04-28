package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.narutomod.Chakra;
import net.narutomod.NarutomodModVariables;
import net.narutomod.Particles;
import net.narutomod.PlayerTracker;
import net.narutomod.entity.EntityHakkeshoKeiten;
import net.narutomod.item.ItemByakugan;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureHakkeshoKaiten;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureHakkeshoKaiten.class)
public abstract class MixinProcedureHakkeshoKaiten {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$useVirtualByakugan(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        Object worldObject = dependencies.get("world");
        if (!(entity instanceof EntityPlayer) || !(worldObject instanceof World)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        ItemStack virtualEye = DojutsuEyeHelper.getVirtualEye(player);
        if (virtualEye.isEmpty() || virtualEye.getItem() != ItemByakugan.helmet || DojutsuEyeHelper.getHeadEye(player).getItem() == ItemByakugan.helmet) {
            return;
        }

        boolean isPressed = Boolean.TRUE.equals(dependencies.get("is_pressed"));
        World world = (World) worldObject;
        double cooldown;
        if (virtualEye.hasTagCompound() && virtualEye.getTagCompound().getBoolean(NarutomodModVariables.RINNESHARINGAN_ACTIVATED)) {
            cooldown = entity.getEntityData().getDouble("press_time");
            if (isPressed) {
                if (cooldown < 200) {
                    entity.getEntityData().setDouble("press_time", cooldown + 1);
                    if (!entity.world.isRemote) {
                        player.sendStatusMessage(new TextComponentString("Power: " + (cooldown / 2)), true);
                    }
                }
            } else {
                world.playSound(null, entity.posX, entity.posY, entity.posZ,
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:dojutsu")),
                        SoundCategory.NEUTRAL, 1.0F, 1.0F);
                for (int index = 0; index < 1000; index++) {
                    Particles.spawnParticle(world, Particles.Types.SMOKE, entity.posX, entity.posY + 1.4D, entity.posZ, 1, 1D, 0D, 1D,
                            ProcedureUtils.rngGaussian(), 1D, ProcedureUtils.rngGaussian(), 0x10FFFFFF, 30, 0);
                }
                ProcedureAoeCommand.set(entity, 0D, cooldown / 2).exclude(entity).knockback(3F);
                ProcedureUtils.purgeHarmfulEffects((net.minecraft.entity.EntityLivingBase) entity);
                entity.extinguish();
                entity.getEntityData().setDouble("press_time", 0);
            }
            ci.cancel();
            return;
        }

        boolean allowed = player.capabilities.isCreativeMode || (ProcedureUtils.isOriginalOwner(player, virtualEye) && PlayerTracker.getBattleXp(player) >= 1500);
        if (!allowed) {
            ci.cancel();
            return;
        }

        if (!world.isRemote) {
            if (isPressed) {
                if (!(entity.getRidingEntity() instanceof EntityHakkeshoKeiten.EntityCustom)) {
                    cooldown = virtualEye.hasTagCompound() ? virtualEye.getTagCompound().getDouble("HakkeshoKaitenCD") : -1;
                    if (player.capabilities.isCreativeMode || NarutomodModVariables.world_tick > cooldown || NarutomodModVariables.world_tick < cooldown - 6000) {
                        if (Chakra.pathway(player).getAmount() >= ItemByakugan.getKaitenChakraUsage(player)) {
                            world.playSound(null, entity.posX, entity.posY, entity.posZ,
                                    net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:HakkeshoKaiten")),
                                    SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            entity.world.spawnEntity(new EntityHakkeshoKeiten.EntityCustom(player));
                        }
                    } else {
                        player.sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted",
                                (int) ((cooldown - NarutomodModVariables.world_tick) / 20)), true);
                    }
                }
            } else {
                Entity spawned = entity.getRidingEntity();
                if (spawned instanceof EntityHakkeshoKeiten.EntityCustom) {
                    spawned.setDead();
                }
            }
        }
        ci.cancel();
    }
}