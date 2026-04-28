package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.narutomod.Chakra;
import net.narutomod.NarutomodModVariables;
import net.narutomod.PlayerTracker;
import net.narutomod.entity.EntityEightTrigrams;
import net.narutomod.item.ItemByakugan;
import net.narutomod.procedure.ProcedureEightTrigrams64Palms;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureEightTrigrams64Palms.class)
public abstract class MixinProcedureEightTrigrams64Palms {
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

        World world = (World) worldObject;
        boolean allowed = !world.isRemote && (player.capabilities.isCreativeMode || (PlayerTracker.getBattleXp(player) >= 1000 && ProcedureUtils.isOriginalOwner(player, virtualEye)));
        if (!allowed) {
            ci.cancel();
            return;
        }

        double cooldown = virtualEye.hasTagCompound() ? virtualEye.getTagCompound().getDouble("HakkeRokujuuyonshouCD") : -1;
        if (player.capabilities.isCreativeMode || NarutomodModVariables.world_tick > cooldown || NarutomodModVariables.world_tick < cooldown - 1200) {
            if (Chakra.pathway((EntityLivingBase) entity).consume(ItemByakugan.getRokujuyonshoChakraUsage((EntityLivingBase) entity))) {
                world.playSound(null, entity.posX, entity.posY, entity.posZ,
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:HakkeRokujuuyonShou")),
                        SoundCategory.NEUTRAL, 1.0F, 1.0F);
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HASTE, 240, 3));
                entity.world.spawnEntity(new EntityEightTrigrams.EntityCustom((EntityLivingBase) entity));
                double modifiedCooldown = ProcedureUtils.getCooldownModifier(player);
                if (!virtualEye.hasTagCompound()) {
                    virtualEye.setTagCompound(new NBTTagCompound());
                }
                virtualEye.getTagCompound().setDouble("HakkeRokujuuyonshouCD", NarutomodModVariables.world_tick + (modifiedCooldown * 1200));
            } else {
                Chakra.pathway(player).warningDisplay();
            }
        } else {
            player.sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted", (cooldown - NarutomodModVariables.world_tick) / 20), true);
        }
        ci.cancel();
    }
}