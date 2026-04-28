package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.NinjaXpHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.narutomod.PlayerTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerTracker.PlayerHook.class)
public abstract class MixinPlayerTrackerHook {
    private static final UUID NARUTOMOD_HP_UUID = UUID.fromString("84d6711b-c26d-4dfa-b0c5-1ff54395f4de");
    private static final String FORCE_SEND = "forceSendBattleXP2self";
    private static final String UPDATE_HEALTH = "forceUpdateHealth";

    @Inject(method = "onDamaged", at = @At("HEAD"), cancellable = true, remap = false)
    private void narutofix$disableOriginalBattleXpGain(LivingDamageEvent event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true, remap = false)
    private void narutofix$disableBattleXpHealthBonus(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof EntityPlayerMP)) {
            return;
        }

        IAttributeInstance maxHealthAttr = event.player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            maxHealthAttr.removeModifier(NARUTOMOD_HP_UUID);
        }
        if (event.player.getEntityData().getBoolean(FORCE_SEND)) {
            event.player.getEntityData().removeTag(FORCE_SEND);
            NinjaXpHelper.set(event.player, NinjaXpHelper.get(event.player), false);
        }
        if (event.player.getEntityData().getBoolean(UPDATE_HEALTH)) {
            event.player.getEntityData().removeTag(UPDATE_HEALTH);
            event.player.setHealth(Math.min(event.player.getHealth(), event.player.getMaxHealth()));
        }
        ci.cancel();
    }
}
