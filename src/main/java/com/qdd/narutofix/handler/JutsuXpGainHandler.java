package com.qdd.narutofix.handler;

import com.qdd.narutofix.util.EnergyMath;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.narutomod.item.ItemEightGates;
import net.narutomod.item.ItemJutsu;

public class JutsuXpGainHandler {
    private static final String LAST_JUTSU_XP_TICK = "narutofixLastJutsuXpTick";
    private static final int COOLDOWN_TICKS = 20;

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().world.isRemote || event.getAmount() <= 0.0F) {
            return;
        }

        EntityLivingBase victim = event.getEntityLiving();
        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            this.tryGrantJutsuXp((EntityPlayer) event.getSource().getTrueSource(), event.getAmount());
        }
        if (victim instanceof EntityPlayer) {
            this.tryGrantJutsuXp((EntityPlayer) victim, event.getAmount());
        }
    }

    private void tryGrantJutsuXp(EntityPlayer player, float damage) {
        int lastTick = player.getEntityData().getInteger(LAST_JUTSU_XP_TICK);
        if (player.ticksExisted - lastTick < COOLDOWN_TICKS) {
            return;
        }

        int xp = Math.max(1, (int) Math.ceil(EnergyMath.soulJutsuXpMultiplier(player)));
        boolean granted = this.tryGrantHeldItemXp(player, player.getHeldItemMainhand(), xp)
                | this.tryGrantHeldItemXp(player, player.getHeldItemOffhand(), xp);
        if (granted) {
            player.getEntityData().setInteger(LAST_JUTSU_XP_TICK, player.ticksExisted);
        }
    }

    private boolean tryGrantHeldItemXp(EntityPlayer player, ItemStack stack, int xp) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() == ItemEightGates.block) {
            ItemEightGates.addBattleXP(player, xp);
            return true;
        }
        if (stack.getItem() instanceof ItemJutsu.Base) {
            ItemJutsu.addBattleXP(player, xp);
            return true;
        }
        return false;
    }
}
