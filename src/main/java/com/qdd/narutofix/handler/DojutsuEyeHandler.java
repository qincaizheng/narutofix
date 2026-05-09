package com.qdd.narutofix.handler;

import com.qdd.narutofix.items.ItemSixTomoeRinnegan;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemSharingan;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class DojutsuEyeHandler {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.world.isRemote) {
            return;
        }

        EntityPlayer player = event.player;

        ItemStack eyeStack = DojutsuEyeHelper.getCapabilityEye(player);
        if (eyeStack.isEmpty() || eyeStack.equals(DojutsuEyeHelper.getHeadEye(player))) {
            return;
        }

        if (eyeStack.getItem() instanceof ItemSixTomoeRinnegan) {
            eyeStack.getItem().onArmorTick(player.world, player, eyeStack);
        }

        // Apply durability consumption for Sharingan-family eyes in the capability slot.
        // We cannot call onArmorTick for these (it causes eye duplication), so we
        // replicate only the durability logic from ItemSharingan.Base.onArmorTick.
        this.tickCapabilityEyeDurability(player, eyeStack);
    }

    @SubscribeEvent
    public void onItemToss(ItemTossEvent event) {
        ItemStack stack = event.getEntityItem().getItem();
        if (!DojutsuEyeHelper.isDojutsu(stack)) {
            return;
        }

        event.setCanceled(true);
    }

    /**
     * Replicates the durability consumption from ItemSharingan.Base.onArmorTick for
     * eyes held in the capability slot. We cannot call onArmorTick for non-SixTomoe
     * eyes (it causes eye duplication), so only the durability part is reproduced here.
     *
     * Mirrors the original logic: every 6 ticks, if amaterasu/susanoo/kamui is active,
     * damage by 3 (owner) or 9 (non-owner). Eternal Mangekyou owned by the player is exempt.
     */
    private void tickCapabilityEyeDurability(EntityPlayer player, ItemStack eyeStack) {
        if (!(eyeStack.getItem() instanceof ItemSharingan.Base) || eyeStack.getMaxDamage() <= 0) {
            return;
        }
        if (player.ticksExisted % 6 != 1) {
            return;
        }

        ItemSharingan.Base base = (ItemSharingan.Base) eyeStack.getItem();

        // EMS owned by the player is exempt from durability loss (same as original)
        if (eyeStack.getItem() == ItemMangekyoSharinganEternal.helmet && base.isOwner(eyeStack, player)) {
            return;
        }

        boolean abilityActive = player.getEntityData().getBoolean("amaterasu_active")
                || player.getEntityData().getBoolean("kamui_teleport");
        if (!abilityActive) {
            return;
        }

        int damageAmount = base.isOwner(eyeStack, player) ? 3 : 9;
        int newDamage = eyeStack.getItemDamage() + damageAmount;
        base.forceDamage(eyeStack, Math.min(newDamage, eyeStack.getMaxDamage()));
    }
}
