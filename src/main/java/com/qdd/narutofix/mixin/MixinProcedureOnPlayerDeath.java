package com.qdd.narutofix.mixin;

import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.narutomod.PlayerTracker;
import net.narutomod.item.ItemByakugan;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.procedure.ProcedureOnPlayerDeath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureOnPlayerDeath.class)
public abstract class MixinProcedureOnPlayerDeath {
    @Inject(method = "executeProcedure", at = @At("HEAD"), remap = false)
    private static void narutofix$handleVirtualEyeDeath(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) entity;
        IPlayerAwakeningData data = PlayerAwakeningDataProvider.get(player);
        if (data == null) {
            return;
        }

        boolean keepInventory = player.world.getGameRules().getBoolean("keepInventory");
        ItemStack virtualEye = data.getEquippedEye();
        if (virtualEye.isEmpty()) {
            return;
        }

        if (isKingOfHellProtected(virtualEye)) {
            player.setHealth(2.0F);
            cancelEvent(dependencies.get("event"));
            return;
        }

        if (virtualEye.getItem() == ItemByakugan.helmet) {
            if (!virtualEye.hasTagCompound()) {
                virtualEye.setTagCompound(new NBTTagCompound());
            }
            virtualEye.getTagCompound().setBoolean("RinnesharinganActivated", false);
            data.setEquippedEye(virtualEye);
        }

        if (!keepInventory && shouldClearOnDeath(virtualEye.getItem())) {
            data.setEquippedEye(ItemStack.EMPTY);
        }

        if (player.world.getGameRules().getBoolean(PlayerTracker.FORCE_DOJUTSU_DROP_RULE) && shouldForceDrop(virtualEye.getItem())) {
            data.setEquippedEye(ItemStack.EMPTY);
        }
    }

    private static boolean isKingOfHellProtected(ItemStack stack) {
        Item item = stack.getItem();
        return (item == ItemRinnegan.helmet || item == ItemTenseigan.helmet || item == ModItems.SIX_TOMOE_RINNEGAN)
                && stack.hasTagCompound() && stack.getTagCompound().hasUniqueId("KoH_id");
    }

    private static boolean shouldClearOnDeath(Item item) {
        return item == ItemRinnegan.helmet || item == ItemTenseigan.helmet || item == ModItems.SIX_TOMOE_RINNEGAN || item == ItemMangekyoSharinganEternal.helmet;
    }

    private static boolean shouldForceDrop(Item item) {
        return item == ItemByakugan.helmet || item == ItemSharingan.helmet || item == ItemMangekyoSharingan.helmet || item == ItemMangekyoSharinganObito.helmet;
    }

    private static void cancelEvent(Object event) {
        if (event instanceof net.minecraftforge.fml.common.eventhandler.Event) {
            net.minecraftforge.fml.common.eventhandler.Event forgeEvent = (net.minecraftforge.fml.common.eventhandler.Event) event;
            if (forgeEvent.isCancelable()) {
                forgeEvent.setCanceled(true);
            }
        }
    }
}