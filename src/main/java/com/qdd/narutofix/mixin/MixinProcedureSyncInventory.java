package com.qdd.narutofix.mixin;

import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import com.qdd.narutofix.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.narutomod.ModConfig;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.procedure.ProcedureSyncInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ProcedureSyncInventory.class)
public abstract class MixinProcedureSyncInventory {
    @Inject(method = "executeProcedure", at = @At("TAIL"), remap = false)
    private static void narutofix$syncVirtualEyes(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityPlayerMP) || entity.ticksExisted % 30 != 9 || ((EntityPlayer) entity).capabilities.isCreativeMode || !ModConfig.REMOVE_CHEAT_DOJUTSUS) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) entity;
        IPlayerAwakeningData data = PlayerAwakeningDataProvider.get(player);
        if (data == null) {
            return;
        }

        boolean removedAny = false;
        removedAny |= removeIfMissingAdvancement(player, data, data.getEquippedEye(), -1, ItemMangekyoSharinganEternal.helmet, "narutomod:mangekyosharinganopened");
        removedAny |= removeIfMissingAdvancement(player, data, data.getEquippedEye(), -1, ItemRinnegan.helmet, "narutomod:eternalmangekyoachieved");
        removedAny |= removeIfMissingAdvancement(player, data, data.getEquippedEye(), -1, ModItems.SIX_TOMOE_RINNEGAN, "narutomod:eternalmangekyoachieved");
        removedAny |= removeIfMissingAdvancement(player, data, data.getEquippedEye(), -1, ItemTenseigan.helmet, "narutomod:byakuganopened");

        NonNullList<ItemStack> storedEyes = data.getStoredEyes();
        for (int index = 0; index < storedEyes.size(); index++) {
            ItemStack stack = storedEyes.get(index);
            removedAny |= removeIfMissingAdvancement(player, data, stack, index, ItemMangekyoSharinganEternal.helmet, "narutomod:mangekyosharinganopened");
            removedAny |= removeIfMissingAdvancement(player, data, stack, index, ItemRinnegan.helmet, "narutomod:eternalmangekyoachieved");
            removedAny |= removeIfMissingAdvancement(player, data, stack, index, ModItems.SIX_TOMOE_RINNEGAN, "narutomod:eternalmangekyoachieved");
            removedAny |= removeIfMissingAdvancement(player, data, stack, index, ItemTenseigan.helmet, "narutomod:byakuganopened");
        }

        if (removedAny && !player.world.isRemote) {
            player.sendStatusMessage(new TextComponentString(TextFormatting.RED + "You obtained your advanced dojutsu illegally, it will be removed"), false);
        }
    }

    private static boolean removeIfMissingAdvancement(EntityPlayerMP player, IPlayerAwakeningData data, ItemStack stack, int storedIndex, Item item, String advancementId) {
        if (stack.isEmpty() || stack.getItem() != item || hasAdvancement(player, advancementId)) {
            return false;
        }

        if (storedIndex >= 0) {
            data.setStoredEye(storedIndex, ItemStack.EMPTY);
        } else {
            data.setEquippedEye(ItemStack.EMPTY);
        }
        return true;
    }

    private static boolean hasAdvancement(EntityPlayerMP player, String advancementId) {
        return player.world instanceof WorldServer
                && player.getAdvancements().getProgress(((WorldServer) player.world).getAdvancementManager().getAdvancement(new ResourceLocation(advancementId))).isDone();
    }
}