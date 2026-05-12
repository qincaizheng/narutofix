package com.qdd.narutofix.entity.susanoo;

import com.qdd.narutofix.handler.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
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

        // 6. Play summon sound
        player.world.playSound(null, player.posX, player.posY, player.posZ,
                ModSounds.SUSANOO, SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }


    public static void upgradeSusanoo(EntityPlayer player) {
        Entity riding = player.getRidingEntity();
        if (riding == null) return;

        double bxp = PlayerTracker.getBattleXp(player);

        boolean upgraded = false;

        if (riding instanceof SusanooSkeletonEntity) {
            SusanooSkeletonEntity skeleton = (SusanooSkeletonEntity) riding;

            if (!skeleton.isFullBody() && bxp >= Configs.susanoo.bxpRequiredL1) {
                if (Chakra.pathway(player).consume(Configs.susanoo.baseChakraUsage)) {
                    changeEntity(player, skeleton, new SusanooSkeletonEntity(player, true));
                    upgraded = true;
                }
            } else if (skeleton.isFullBody() && bxp >= Configs.susanoo.bxpRequiredL2) {
                if (Chakra.pathway(player).consume(Configs.susanoo.baseChakraUsage)) {
                    changeEntity(player, skeleton, new SusanooClothedEntity(player, false));
                    upgraded = true;
                }
            }

        } else if (riding instanceof SusanooClothedEntity) {
            SusanooClothedEntity clothed = (SusanooClothedEntity) riding;

            if (!clothed.hasLegs() && bxp >= Configs.susanoo.bxpRequiredL3) {
                if (Chakra.pathway(player).consume(Configs.susanoo.baseChakraUsage)) {
                    changeEntity(player, clothed, new SusanooClothedEntity(player, true));
                    upgraded = true;
                }
            } else if (clothed.hasLegs() && bxp >= Configs.susanoo.bxpRequiredL4) {
                if (Chakra.pathway(player).consume(Configs.susanoo.baseChakraUsage)) {
                    changeEntity(player, clothed, new SusanooWingedEntity(player));
                    upgraded = true;
                }
            }
        } else if (riding instanceof SusanooWingedEntity) {
            boolean usingKamui = ((SusanooWingedEntity) riding).toggleActiveWeapon();
            player.sendStatusMessage(new TextComponentTranslation(usingKamui
                    ? "message.narutofix.susanoo.weapon.kamui"
                    : "message.narutofix.susanoo.weapon.kagutsuchi"), true);
        }

        // Play upgrade sound if any upgrade succeeded
        if (upgraded) {
            player.world.playSound(null, player.posX, player.posY, player.posZ,
                    ModSounds.SUSANOO, SoundCategory.NEUTRAL, 1.0F, 1.0F);
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
