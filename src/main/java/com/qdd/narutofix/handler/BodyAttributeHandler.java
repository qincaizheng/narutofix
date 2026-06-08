package com.qdd.narutofix.handler;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.cap.body.BodyEnergyDataProvider;
import com.qdd.narutofix.cap.body.IBodyEnergyData;
import com.qdd.narutofix.util.EnergyMath;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

public class BodyAttributeHandler {
    private static final UUID HEALTH_MODIFIER_ID = UUID.fromString("6c9b0a0d-5e32-4cde-8f38-720ea57ef39c");
    private static final UUID ARMOR_MODIFIER_ID = UUID.fromString("5157db03-22a5-4478-92fe-4352cc18fa0c");
    private static final UUID MOVE_SPEED_MODIFIER_ID = UUID.fromString("2321b80a-f889-4ab2-a927-350108bf5dc0");
    private static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("2ae1d0b3-1b27-4b06-8dfb-40fd5e72b8e8");
    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("7ee0cc88-6609-4d17-a35e-137f31f56a50");
    private static final int TICK_INTERVAL = 20;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) {
            return;
        }

        EntityPlayer player = event.player;
        IBodyEnergyData data = BodyEnergyDataProvider.get(player);
        if (data == null) {
            return;
        }

        this.applyBodyAttributes(player, data);
        if (player.ticksExisted % TICK_INTERVAL == 0) {
            this.applyBodyRegen(player, data);
        }
    }

    private void applyBodyAttributes(EntityPlayer player, IBodyEnergyData data) {
        double body = 100.0D * EnergyMath.bodyMultiplier(player);
        this.applyModifier(player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH),
                HEALTH_MODIFIER_ID, "narutofix.body.max_health",
                body * Configs.body.hpMultiplierPerBody, 2);
        this.applyModifier(player.getEntityAttribute(SharedMonsterAttributes.ARMOR),
                ARMOR_MODIFIER_ID, "narutofix.body.armor",
                Math.min(body * Configs.body.armorPerBody, Configs.body.maxArmor), 0);
        this.applyModifier(player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED),
                MOVE_SPEED_MODIFIER_ID, "narutofix.body.move_speed",
                Math.min(body * Configs.body.moveSpeedPerBody, Configs.body.maxMoveSpeedMultiplier), 2);
        this.applyModifier(player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE),
                ATTACK_DAMAGE_MODIFIER_ID, "narutofix.body.attack_damage",
                Math.min(body * Configs.body.attackDamagePerBody, Configs.body.maxAttackDamageMultiplier), 2);
        this.applyModifier(player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED),
                ATTACK_SPEED_MODIFIER_ID, "narutofix.body.attack_speed",
                Math.min(body * Configs.body.attackSpeedPerBody, Configs.body.maxAttackSpeedMultiplier), 2);

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private void applyModifier(IAttributeInstance attribute, UUID uuid, String name, double amount, int operation) {
        if (attribute == null) {
            return;
        }
        attribute.removeModifier(uuid);
        if (amount != 0.0D) {
            attribute.applyModifier(new AttributeModifier(uuid, name, amount, operation));
        }
    }

    private void applyBodyRegen(EntityPlayer player, IBodyEnergyData data) {
        if (player.getHealth() >= player.getMaxHealth()) {
            return;
        }
        float heal = (float) (100.0D * EnergyMath.bodyMultiplier(player) * Configs.body.hpRegenPerBody);
        if (heal > 0.0F) {
            player.heal(heal);
        }
    }
}
