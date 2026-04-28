package com.qdd.narutofix.mixin;

import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.narutomod.Chakra;
import net.narutomod.NarutomodModVariables;
import net.narutomod.PlayerTracker;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.entity.EntitySusanooClothed;
import net.narutomod.entity.EntitySusanooSkeleton;
import net.narutomod.entity.EntitySusanooWinged;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemSharingan;
import net.narutomod.potion.PotionFeatherFalling;
import net.narutomod.procedure.ProcedureSusanoo;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Extends ProcedureSusanoo to support virtual eye (capability-based eye slot).
 * When the player has a Sharingan-family eye in the virtual eye slot and no Sharingan
 * on their head, this mixin takes over execute/upgrade with logic identical to
 * the original, using the virtual eye in place of the helmet.
 */
@Mixin(ProcedureSusanoo.class)
public abstract class MixinProcedureSusanoo {
    private static final String SUMMONED_SUSANOO = "summonedSusanooID";

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$useVirtualEye(EntityPlayer player, CallbackInfo ci) {
        ItemStack virtualEye = DojutsuEyeHelper.getVirtualEye(player);
        if (virtualEye.isEmpty() || !narutofix$isSharinganFamily(virtualEye)
                || narutofix$isSharinganFamily(DojutsuEyeHelper.getHeadEye(player))) {
            return;
        }

        // Mirror original ProcedureSusanoo.execute() exactly, substituting virtualEye for helmet
        World world = player.world;
        boolean hasRinneganSupport = player.isCreative()
                || ProcedureUtils.hasItemInInventory(player, ItemRinnegan.helmet)
                || ProcedureUtils.hasItemInInventory(player, ModItems.SIX_TOMOE_RINNEGAN)
                || virtualEye.getItem() == ModItems.SIX_TOMOE_RINNEGAN;

        if (!player.getEntityData().getBoolean("susanoo_activated")) {
            // -- Activation branch (identical to original) --
            if (virtualEye.hasTagCompound() && !virtualEye.getTagCompound().getBoolean("sharingan_blinded")
                    && PlayerTracker.getBattleXp(player) >= EntitySusanooBase.BXP_REQUIRED_L0
                    && Chakra.pathway(player).consume(ProcedureSusanoo.BASE_CHAKRA_USAGE)) {
                player.getEntityData().setBoolean("susanoo_activated", true);
                player.getEntityData().setDouble("susanoo_cd", NarutomodModVariables.world_tick + 2400.0D);
                EntitySusanooBase entity = new EntitySusanooSkeleton.EntityCustom(player);
                world.spawnEntity(entity);
                player.getEntityData().setInteger(SUMMONED_SUSANOO, entity.getEntityId());
            }
        } else {
            // -- Deactivation branch (identical to original) --
            // Kill entity by stored ID; do NOT manually dismount.
            // When the dead entity is removed from the world next tick,
            // its rider is auto-dismounted (bypassing the EntityMountEvent guard
            // because the entity will no longer be alive).
            double cooldown = player.getEntityData().getDouble("susanoo_ticks") * 0.25D;
            cooldown *= ProcedureUtils.getCooldownModifier(player);
            player.getEntityData().removeTag("susanoo_activated");
            player.getEntityData().removeTag("susanoo_ticks");
            Entity entitySpawned = world.getEntityByID(ProcedureSusanoo.getSummonedSusanooId(player));
            player.getEntityData().removeTag(SUMMONED_SUSANOO);
            if (entitySpawned != null) {
                entitySpawned.setDead();
            }
            // Explicit dismount: susanoo_activated already removed, so the mount handler won't block
            if (player.isRiding()) {
                player.dismountRidingEntity();
            }
            if (!hasRinneganSupport && virtualEye.getItem() != ItemMangekyoSharinganEternal.helmet) {
                player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, (int) cooldown, 3));
                player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int) cooldown, 2));
            }
            player.addPotionEffect(new PotionEffect(PotionFeatherFalling.potion, 60, 5));
        }
        ci.cancel();
    }

//    @Inject(method = "upgrade", at = @At("HEAD"), cancellable = true, remap = false)
//    private static void narutofix$upgradeVirtualSusanoo(EntityPlayer player, CallbackInfo ci) {
//        if (!DojutsuEyeHelper.hasEitherEye(player, ModItems.SIX_TOMOE_RINNEGAN)) {
//            return;
//        }
//
//        // Mirror original ProcedureSusanoo.upgrade() exactly
//        Entity susanoo = player.getRidingEntity();
//        double playerXp = PlayerTracker.getBattleXp(player);
//        if (susanoo instanceof EntitySusanooBase) {
//            if (susanoo instanceof EntitySusanooSkeleton.EntityCustom) {
//                boolean fullBody = ((EntitySusanooSkeleton.EntityCustom) susanoo).isFullBody();
//                if (!fullBody && playerXp >= EntitySusanooBase.BXP_REQUIRED_L1) {
//                    if (Chakra.pathway(player).consume(ProcedureSusanoo.BASE_CHAKRA_USAGE)) {
//                        susanoo.setDead();
//                        player.dismountRidingEntity();
//                        EntitySusanooBase entity = new EntitySusanooSkeleton.EntityCustom(player, true);
//                        player.world.spawnEntity(entity);
//                        player.getEntityData().setInteger(SUMMONED_SUSANOO, entity.getEntityId());
//                    }
//                } else if (fullBody && playerXp >= EntitySusanooBase.BXP_REQUIRED_L2) {
//                    if (Chakra.pathway(player).consume(ProcedureSusanoo.BASE_CHAKRA_USAGE)) {
//                        susanoo.setDead();
//                        player.dismountRidingEntity();
//                        EntitySusanooBase entity = new EntitySusanooClothed.EntityCustom(player, false);
//                        player.world.spawnEntity(entity);
//                        player.getEntityData().setInteger(SUMMONED_SUSANOO, entity.getEntityId());
//                    }
//                }
//            } else if (susanoo instanceof EntitySusanooClothed.EntityCustom) {
//                boolean hasLegs = ((EntitySusanooClothed.EntityCustom) susanoo).hasLegs();
//                if (hasLegs && playerXp >= EntitySusanooBase.BXP_REQUIRED_L4) {
//                    if (Chakra.pathway(player).consume(ProcedureSusanoo.BASE_CHAKRA_USAGE)) {
//                        susanoo.setDead();
//                        player.dismountRidingEntity();
//                        EntitySusanooBase entity = new EntitySusanooWinged.EntityCustom(player);
//                        player.world.spawnEntity(entity);
//                        player.getEntityData().setInteger(SUMMONED_SUSANOO, entity.getEntityId());
//                    }
//                } else if (!hasLegs && playerXp >= EntitySusanooBase.BXP_REQUIRED_L3) {
//                    if (Chakra.pathway(player).consume(ProcedureSusanoo.BASE_CHAKRA_USAGE)) {
//                        susanoo.setDead();
//                        player.dismountRidingEntity();
//                        EntitySusanooBase entity = new EntitySusanooClothed.EntityCustom(player, true);
//                        player.world.spawnEntity(entity);
//                        player.getEntityData().setInteger(SUMMONED_SUSANOO, entity.getEntityId());
//                    }
//                }
//            }
//        }
//        ci.cancel();
//    }

    @Unique
    private static boolean narutofix$isSharinganFamily(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() == ItemSharingan.helmet
                || stack.getItem() == ItemMangekyoSharingan.helmet
                || stack.getItem() == ItemMangekyoSharinganObito.helmet
                || stack.getItem() == ItemMangekyoSharinganEternal.helmet
                || stack.getItem() == ModItems.SIX_TOMOE_RINNEGAN);
    }
}