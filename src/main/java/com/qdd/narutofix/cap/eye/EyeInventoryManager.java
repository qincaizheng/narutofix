package com.qdd.narutofix.cap.eye;

import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.procedure.ProcedureUtils;

public final class EyeInventoryManager {
    private EyeInventoryManager() {
    }

    public static boolean isEyeItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemDojutsu.Base;
    }

    public static IPlayerAwakeningData getData(EntityPlayer player) {
        return PlayerAwakeningDataProvider.get(player);
    }

    public static boolean cycleEquippedEye(EntityPlayer player) {
        IPlayerAwakeningData data = getData(player);
        if (data == null) {
            return false;
        }

        compactStoredEyes(data);
        StoredEyesItemHandler storedEyes = new StoredEyesItemHandler(player);
        EquippedEyeItemHandler equippedEye = new EquippedEyeItemHandler(player);
        ItemStack nextEye = storedEyes.extractItem(0, 1, true);
        if (nextEye.isEmpty()) {
            return false;
        }

        ItemStack previousEquipped = data.getEquippedEye().copy();
        NonNullList<ItemStack> previousStoredEyes = copyStoredEyes(data);
        ItemStack currentEye = equippedEye.extractItem(0, 1, true);
        ItemStack removedEye = storedEyes.extractItem(0, 1, false);
        equippedEye.extractItem(0, 1, false);

        if (removedEye.isEmpty()) {
            restoreEyeState(data, previousEquipped, previousStoredEyes);
            player.sendStatusMessage(new TextComponentTranslation("message.narutofix.eye_cannot_equip"), true);
            return false;
        }

        claimEyeOwnership(player, removedEye);
        data.setEquippedEye(removedEye);

        if (!currentEye.isEmpty() && !storedEyes.insertItem(EyeInventoryConstants.STORAGE_SLOT_COUNT - 1, currentEye, false).isEmpty()) {
            restoreEyeState(data, previousEquipped, previousStoredEyes);
            player.sendStatusMessage(new TextComponentTranslation("message.narutofix.eye_cannot_equip"), true);
            return false;
        }

        player.sendStatusMessage(new TextComponentTranslation("message.narutofix.eye_swapped"), true);
        return true;
    }

    public static int findFirstEmptyStoredSlot(IPlayerAwakeningData data) {
        compactStoredEyes(data);
        for (int index = 0; index < data.getStoredEyes().size(); index++) {
            if (data.getStoredEyes().get(index).isEmpty()) {
                return index;
            }
        }
        return -1;
    }

    public static ItemStack removeStoredEyeAt(IPlayerAwakeningData data, int index) {
        if (index < 0 || index >= data.getStoredEyes().size()) {
            return ItemStack.EMPTY;
        }

        compactStoredEyes(data);
        ItemStack stack = data.getStoredEyes().get(index);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = stack.copy();
        for (int shift = index; shift < data.getStoredEyes().size() - 1; shift++) {
            data.setStoredEye(shift, data.getStoredEyes().get(shift + 1));
        }
        data.setStoredEye(data.getStoredEyes().size() - 1, ItemStack.EMPTY);
        return result;
    }


    /**
     * Claims ownership of an eye for the player if it has no owner yet.
     *
     * @param player player claiming the eye
     * @param stack eye item stack to claim
     */
    public static void claimEyeOwnership(EntityPlayer player, ItemStack stack) {
        if (!isEyeItem(stack)) {
            return;
        }
        if (ProcedureUtils.getOwnerId(stack) == null) {
            ((ItemDojutsu.Base) stack.getItem()).setOwner(stack, player);
        }
    }

    public static void compactStoredEyes(IPlayerAwakeningData data) {
        int writeIndex = 0;
        for (int readIndex = 0; readIndex < data.getStoredEyes().size(); readIndex++) {
            ItemStack stack = data.getStoredEyes().get(readIndex);
            if (!stack.isEmpty()) {
                if (writeIndex != readIndex) {
                    data.setStoredEye(writeIndex, stack);
                }
                writeIndex++;
            }
        }

        while (writeIndex < data.getStoredEyes().size()) {
            data.setStoredEye(writeIndex, ItemStack.EMPTY);
            writeIndex++;
        }
    }

    private static NonNullList<ItemStack> copyStoredEyes(IPlayerAwakeningData data) {
        NonNullList<ItemStack> copy = NonNullList.withSize(data.getStoredEyes().size(), ItemStack.EMPTY);
        for (int index = 0; index < data.getStoredEyes().size(); index++) {
            copy.set(index, data.getStoredEyes().get(index).copy());
        }
        return copy;
    }

    private static void restoreEyeState(IPlayerAwakeningData data, ItemStack equippedEye, NonNullList<ItemStack> storedEyes) {
        data.setEquippedEye(equippedEye);
        for (int index = 0; index < data.getStoredEyes().size(); index++) {
            data.setStoredEye(index, index < storedEyes.size() ? storedEyes.get(index) : ItemStack.EMPTY);
        }
    }
}
