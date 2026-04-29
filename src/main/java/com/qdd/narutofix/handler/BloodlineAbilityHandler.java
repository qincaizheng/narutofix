package com.qdd.narutofix.handler;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

public class BloodlineAbilityHandler {
    private static final UUID ASURA_HEALTH_MODIFIER_ID = UUID.fromString("f68ff30a-15f3-4e00-8d2d-3e977f8ab321");
    private static final int TICK_INTERVAL = 20;
    private static final float LOW_HEALTH_RATIO = 0.25F;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) {
            return;
        }

        EntityPlayer player = event.player;
        IPlayerAwakeningData data = PlayerAwakeningDataProvider.get(player);
        if (data == null) {
            return;
        }

        this.updateAsuraHealthModifier(player, data);
        if (player.ticksExisted % TICK_INTERVAL != 0) {
            return;
        }

        this.applyAsuraBonuses(player, data);
        this.applyDualBloodlineBonuses(player, data);
    }

    private void updateAsuraHealthModifier(EntityPlayer player, IPlayerAwakeningData data) {
        IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if (attribute == null) {
            return;
        }

        attribute.removeModifier(ASURA_HEALTH_MODIFIER_ID);
        if (!data.hasAsura() || Configs.asuraHealthBonus <= 0.0D) {
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
            return;
        }

        attribute.applyModifier(new AttributeModifier(ASURA_HEALTH_MODIFIER_ID, "narutofix.asura.health", Configs.asuraHealthBonus, 2));
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private void applyAsuraBonuses(EntityPlayer player, IPlayerAwakeningData data) {
        if (!data.hasAsura()) {
            return;
        }

        if (Configs.asuraHealPerSecond > 0.0F && player.getHealth() < player.getMaxHealth()) {
            player.heal(Configs.asuraHealPerSecond*player.getMaxHealth());
        }
    }

    private void applyDualBloodlineBonuses(EntityPlayer player, IPlayerAwakeningData data) {
        if (!data.hasBothBloodlines() || !this.isLowHealth(player)) {
            return;
        }

        player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, Math.max(0, Configs.dualBloodlineResistanceAmplifier), false, false));
    }

    private boolean isLowHealth(EntityPlayer player) {
        return player.getHealth() > 0.0F && player.getHealth() / player.getMaxHealth() <= LOW_HEALTH_RATIO;
    }
}
