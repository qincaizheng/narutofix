package com.qdd.narutofix.cap.awakening;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraft.inventory.ItemStackHelper;

public class PlayerAwakeningDataStorage implements Capability.IStorage<IPlayerAwakeningData> {
    private static final String INDRA_KEY = "IndraUnlocked";
    private static final String ASURA_KEY = "AsuraUnlocked";
    private static final String RINNEGAN_KEY = "RinneganAwakened";
    private static final String EQUIPPED_EYE_KEY = "EquippedEye";
    private static final String STORED_EYES_KEY = "StoredEyes";

    @Override
    public NBTBase writeNBT(Capability<IPlayerAwakeningData> capability, IPlayerAwakeningData instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean(INDRA_KEY, instance.hasIndra());
        compound.setBoolean(ASURA_KEY, instance.hasAsura());
        compound.setBoolean(RINNEGAN_KEY, instance.hasRinneganAwakened());

        if (!instance.getEquippedEye().isEmpty()) {
            compound.setTag(EQUIPPED_EYE_KEY, instance.getEquippedEye().serializeNBT());
        }

        NBTTagCompound eyeInventory = new NBTTagCompound();
        ItemStackHelper.saveAllItems(eyeInventory, instance.getStoredEyes());
        compound.setTag(STORED_EYES_KEY, eyeInventory);
        return compound;
    }

    @Override
    public void readNBT(Capability<IPlayerAwakeningData> capability, IPlayerAwakeningData instance, EnumFacing side, NBTBase nbt) {
        if (!(nbt instanceof NBTTagCompound)) {
            return;
        }

        NBTTagCompound compound = (NBTTagCompound) nbt;
        instance.setBloodline(com.qdd.narutofix.awakening.Bloodline.INDRA, compound.getBoolean(INDRA_KEY));
        instance.setBloodline(com.qdd.narutofix.awakening.Bloodline.ASURA, compound.getBoolean(ASURA_KEY));
        instance.setRinneganAwakened(compound.getBoolean(RINNEGAN_KEY));

        if (compound.hasKey(EQUIPPED_EYE_KEY)) {
            instance.setEquippedEye(new ItemStack(compound.getCompoundTag(EQUIPPED_EYE_KEY)));
        } else {
            instance.setEquippedEye(ItemStack.EMPTY);
        }

        NonNullList<ItemStack> storedEyes = NonNullList.withSize(instance.getStoredEyes().size(), ItemStack.EMPTY);
        if (compound.hasKey(STORED_EYES_KEY)) {
            ItemStackHelper.loadAllItems(compound.getCompoundTag(STORED_EYES_KEY), storedEyes);
        }
        for (int index = 0; index < storedEyes.size(); index++) {
            instance.setStoredEye(index, storedEyes.get(index));
        }
    }
}