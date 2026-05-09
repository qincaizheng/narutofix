package com.qdd.narutofix.entity.susanoo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.narutomod.Chakra;
import net.narutomod.PlayerTracker;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.potion.PotionFeatherFalling;
import net.narutomod.procedure.ProcedureUtils;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.util.DojutsuEyeHelper;


public class SusanooSummonHandler {

    // ========================================================================
    // Public API
    // ========================================================================

    public static void summonSusanoo(EntityPlayer player) {
        if (SusanooStateHelper.isActivated(player)) {
            dismissSusanoo(player);
            return;
        }

        // 1. Not blinded
        ItemStack helmet = player.inventory.armorInventory.get(3);
        if (helmet.hasTagCompound() && helmet.getTagCompound().getBoolean("sharingan_blinded")) {
            return;
        }

        // 2. Has a compatible eye (Mangekyo / Eternal / Obito / SixTomoe)
        if (DojutsuEyeHelper.getCompatibleSusanooEye(player).isEmpty()) {
            return;
        }

        // 3. Battle XP >= L0 threshold
        if (PlayerTracker.getBattleXp(player) < Configs.susanoo.bxpRequiredL0) {
            return;
        }

        // 4. Chakra (creative mode bypasses)
        if (!player.isCreative()) {
            if (!Chakra.pathway(player).consume(Configs.susanoo.baseChakraUsage)) {
                return;
            }
        }

        // 5. Spawn L0 skeleton
        SusanooSkeletonEntity entity = new SusanooSkeletonEntity(player, false);
        player.world.spawnEntity(entity);
        SusanooStateHelper.activate(player, entity.getEntityId());
    }


    public static void upgradeSusanoo(EntityPlayer player) {
        Entity riding = player.getRidingEntity();
        System.out.println("[narutofix] upgrade: riding=" + riding + " " + (riding != null ? riding.getClass().getName() : "null"));
        if (riding == null) return;

        double bxp = PlayerTracker.getBattleXp(player);
        System.out.println("[narutofix] upgrade: bxp=" + bxp + " requiredL1=" + Configs.susanoo.bxpRequiredL1 + " requiredL2=" + Configs.susanoo.bxpRequiredL2);

        if (riding instanceof SusanooSkeletonEntity) {
            SusanooSkeletonEntity skeleton = (SusanooSkeletonEntity) riding;
            System.out.println("[narutofix] upgrade: skeleton fullBody=" + skeleton.isFullBody());

            if (!skeleton.isFullBody() && bxp >= Configs.susanoo.bxpRequiredL1) {
                System.out.println("[narutofix] upgrade: L0->L1 attempt, chakra=" + Chakra.pathway(player).getAmount());
                if (Chakra.pathway(player).consume(Configs.susanoo.baseChakraUsage)) {
                    System.out.println("[narutofix] upgrade: L0->L1 success");
                    changeEntity(player, skeleton, new SusanooSkeletonEntity(player, true));
                } else {
                    System.out.println("[narutofix] upgrade: L0->L1 failed - insufficient chakra");
                }
            } else if (skeleton.isFullBody() && bxp >= Configs.susanoo.bxpRequiredL2) {
                System.out.println("[narutofix] upgrade: L1->L2 attempt");
                if (Chakra.pathway(player).consume(Configs.susanoo.baseChakraUsage)) {
                    System.out.println("[narutofix] upgrade: L1->L2 success");
                    changeEntity(player, skeleton, new SusanooClothedEntity(player, false));
                } else {
                    System.out.println("[narutofix] upgrade: L1->L2 failed - insufficient chakra");
                }
            } else {
                System.out.println("[narutofix] upgrade: skeleton condition not met - fullBody=" + skeleton.isFullBody() + " bxp=" + bxp + "/" + Configs.susanoo.bxpRequiredL1);
            }

        } else if (riding instanceof SusanooClothedEntity) {
            SusanooClothedEntity clothed = (SusanooClothedEntity) riding;
            System.out.println("[narutofix] upgrade: clothed hasLegs=" + clothed.hasLegs());

            if (!clothed.hasLegs() && bxp >= Configs.susanoo.bxpRequiredL3) {
                System.out.println("[narutofix] upgrade: L2->L3 attempt");
                if (Chakra.pathway(player).consume(Configs.susanoo.baseChakraUsage)) {
                    System.out.println("[narutofix] upgrade: L2->L3 success");
                    changeEntity(player, clothed, new SusanooClothedEntity(player, true));
                } else {
                    System.out.println("[narutofix] upgrade: L2->L3 failed - insufficient chakra");
                }
            } else if (clothed.hasLegs() && bxp >= Configs.susanoo.bxpRequiredL4) {
                System.out.println("[narutofix] upgrade: L3->L4 attempt");
                if (Chakra.pathway(player).consume(Configs.susanoo.baseChakraUsage)) {
                    System.out.println("[narutofix] upgrade: L3->L4 success");
                    changeEntity(player, clothed, new SusanooWingedEntity(player));
                } else {
                    System.out.println("[narutofix] upgrade: L3->L4 failed - insufficient chakra");
                }
            } else {
                System.out.println("[narutofix] upgrade: clothed condition not met - hasLegs=" + clothed.hasLegs() + " bxp=" + bxp);
            }
        } else {
            System.out.println("[narutofix] upgrade: unknown entity type " + (riding != null ? riding.getClass().getName() : "null"));
        }
    }


    public static void dismissSusanoo(EntityPlayer player) {
        if (!SusanooStateHelper.isActivated(player)) return;

        double cooldown = SusanooStateHelper.calculateCooldown(player);

        Entity susanoo = player.world.getEntityByID(SusanooStateHelper.getSummonedEntityId(player));
        if (susanoo != null) {
            susanoo.setDead();
        }

        SusanooStateHelper.deactivate(player);

        // Always apply feather falling
        player.addPotionEffect(new PotionEffect(PotionFeatherFalling.potion, 60, 5));

        // Weakness + Nausea for non-creative, non-Rinnegan players
        boolean isCreativeOrRinnegan = player.isCreative()
                || ProcedureUtils.hasItemInInventory(player, ItemRinnegan.helmet);
        if (!isCreativeOrRinnegan) {
            player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, (int) cooldown, 3));
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int) cooldown, 2));
        }
    }


    public static void cleanupSusanoo(EntityPlayer player) {
        if (!SusanooStateHelper.isActivated(player)) return;

        Entity susanoo = player.world.getEntityByID(SusanooStateHelper.getSummonedEntityId(player));
        if (susanoo != null) {
            susanoo.setDead();
        }
        SusanooStateHelper.deactivate(player);
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private static void changeEntity(EntityPlayer player, Entity oldEntity, Entity newEntity) {
        oldEntity.setDead();
        newEntity.copyLocationAndAnglesFrom(oldEntity);
        player.world.spawnEntity(newEntity);
        SusanooStateHelper.activate(player, newEntity.getEntityId());
    }

    // ========================================================================
    // Event handlers (cleanup)
    // ========================================================================

    @SubscribeEvent
    public void onPlayerChangeDimension(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            cleanupSusanoo((EntityPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        cleanupSusanoo(event.player);
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            cleanupSusanoo((EntityPlayer) event.getEntity());
        }
    }
}
