package com.qdd.narutofix.util;

import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import com.qdd.narutofix.items.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.narutomod.NarutomodModVariables;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.procedure.ProcedureUtils;

import java.util.UUID;

public final class DojutsuEyeHelper {
    private static final String LAST_WORN_FOREIGN_DOJUTSU = "lastWornForeignDojutsu";

    private DojutsuEyeHelper() {
    }

    public static ItemStack getCapabilityEye(EntityPlayer player) {
        IPlayerAwakeningData data = PlayerAwakeningDataProvider.get(player);
        if (data == null) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = data.getEquippedEye();
        return isDojutsu(stack) ? stack : ItemStack.EMPTY;
    }

    public static ItemStack getHeadEye(EntityLivingBase entity) {
        ItemStack headStack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        return isDojutsu(headStack) ? headStack : ItemStack.EMPTY;
    }

    public static ItemStack getVirtualEye(EntityLivingBase entity) {
        return entity instanceof EntityPlayer ? getCapabilityEye((EntityPlayer) entity) : ItemStack.EMPTY;
    }

    public static ItemStack getEffectiveEye(EntityPlayer player) {
        ItemStack virtualEye = getVirtualEye(player);
        if (!virtualEye.isEmpty()) {
            return virtualEye;
        }
        return getHeadEye(player);
    }

    public static ItemStack getMatchingEye(EntityLivingBase entity, Item... items) {
        ItemStack headEye = getHeadEye(entity);
        if (matchesItem(headEye, items)) {
            return headEye;
        }

        ItemStack virtualEye = getVirtualEye(entity);
        if (matchesItem(virtualEye, items)) {
            return virtualEye;
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack getMatchingEyeOfType(EntityLivingBase entity, Class<? extends Item> itemClass) {
        ItemStack headEye = getHeadEye(entity);
        if (matchesType(headEye, itemClass)) {
            return headEye;
        }

        ItemStack virtualEye = getVirtualEye(entity);
        if (matchesType(virtualEye, itemClass)) {
            return virtualEye;
        }

        return ItemStack.EMPTY;
    }

    public static boolean hasEffectiveEye(EntityPlayer player, Item item) {
        ItemStack effectiveEye = getEffectiveEye(player);
        return !effectiveEye.isEmpty() && effectiveEye.getItem() == item;
    }

    public static boolean hasEitherEye(EntityLivingBase entity, Item... items) {
        return !getMatchingEye(entity, items).isEmpty();
    }

    public static boolean hasEitherEyeOfType(EntityLivingBase entity, Class<? extends Item> itemClass) {
        return !getMatchingEyeOfType(entity, itemClass).isEmpty();
    }

    public static boolean hasVirtualEye(EntityPlayer player, Item item) {
        ItemStack equippedEye = getCapabilityEye(player);
        return !equippedEye.isEmpty() && equippedEye.getItem() == item && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() != item;
    }

    public static boolean isDojutsu(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemDojutsu.Base;
    }

    public static ItemStack getCompatibleSusanooEye(EntityLivingBase entity) {
        ItemStack directEye = getMatchingEye(entity,
                ItemMangekyoSharingan.helmet,
                ItemMangekyoSharinganObito.helmet,
                ItemMangekyoSharinganEternal.helmet);
        if (!directEye.isEmpty()) {
            return directEye;
        }

        ItemStack sixTomoe = getMatchingEye(entity, ModItems.SIX_TOMOE_RINNEGAN);
        if (sixTomoe.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack compatibleEye = new ItemStack(ItemMangekyoSharingan.helmet);
        if (sixTomoe.hasTagCompound()) {
            compatibleEye.setTagCompound(sixTomoe.getTagCompound().copy());
        }
        return compatibleEye;
    }

    public static void applyWornDojutsuState(EntityPlayer player, ItemStack stack) {
        if (!(stack.getItem() instanceof ItemDojutsu.Base)) {
            return;
        }

        ItemDojutsu.Base dojutsu = (ItemDojutsu.Base) stack.getItem();
        if (!dojutsu.isOwner(stack, player) && !player.isCreative()) {
            UUID ownerId = ProcedureUtils.getOwnerId(stack);
            if (ownerId != null && !ownerId.equals(player.getEntityData().getUniqueId(LAST_WORN_FOREIGN_DOJUTSU))) {
                player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 1200, 0, false, false));
                player.getEntityData().setUniqueId(LAST_WORN_FOREIGN_DOJUTSU, ownerId);
            }
        }
    }

    public static void markDojutsuAsWorn(EntityPlayer player, World world) {
        player.getEntityData().setLong(NarutomodModVariables.MostRecentWornDojutsuTime, world.getTotalWorldTime());
    }

    private static boolean matchesItem(ItemStack stack, Item... items) {
        if (stack.isEmpty()) {
            return false;
        }

        for (Item item : items) {
            if (stack.getItem() == item) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesType(ItemStack stack, Class<? extends Item> itemClass) {
        return !stack.isEmpty() && itemClass.isInstance(stack.getItem());
    }
}