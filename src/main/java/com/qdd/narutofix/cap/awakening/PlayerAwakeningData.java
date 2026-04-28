package com.qdd.narutofix.cap.awakening;

import com.qdd.narutofix.awakening.Bloodline;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class PlayerAwakeningData implements IPlayerAwakeningData {
    public static final int STORED_EYE_SLOTS = 3;

    private boolean indraUnlocked;
    private boolean asuraUnlocked;
    private boolean rinneganAwakened;
    private ItemStack equippedEye = ItemStack.EMPTY;
    private final NonNullList<ItemStack> storedEyes = NonNullList.withSize(STORED_EYE_SLOTS, ItemStack.EMPTY);

    @Override
    public boolean hasBloodline(Bloodline bloodline) {
        switch (bloodline) {
            case INDRA:
                return this.indraUnlocked;
            case ASURA:
                return this.asuraUnlocked;
            default:
                return false;
        }
    }

    @Override
    public void setBloodline(Bloodline bloodline, boolean unlocked) {
        switch (bloodline) {
            case INDRA:
                this.indraUnlocked = unlocked;
                break;
            case ASURA:
                this.asuraUnlocked = unlocked;
                break;
            default:
                break;
        }
    }

    @Override
    public boolean hasRinneganAwakened() {
        return this.rinneganAwakened;
    }

    @Override
    public void setRinneganAwakened(boolean awakened) {
        this.rinneganAwakened = awakened;
    }

    @Override
    public ItemStack getEquippedEye() {
        return this.equippedEye;
    }

    @Override
    public void setEquippedEye(ItemStack itemStack) {
        this.equippedEye = itemStack == null ? ItemStack.EMPTY : itemStack.copy();
    }

    @Override
    public NonNullList<ItemStack> getStoredEyes() {
        return this.storedEyes;
    }

    @Override
    public void setStoredEye(int index, ItemStack itemStack) {
        if (index < 0 || index >= this.storedEyes.size()) {
            return;
        }
        this.storedEyes.set(index, itemStack == null ? ItemStack.EMPTY : itemStack.copy());
    }

    @Override
    public void copyFrom(IPlayerAwakeningData other) {
        this.indraUnlocked = other.hasIndra();
        this.asuraUnlocked = other.hasAsura();
        this.rinneganAwakened = other.hasRinneganAwakened();
        this.setEquippedEye(other.getEquippedEye());
        NonNullList<ItemStack> otherEyes = other.getStoredEyes();
        for (int index = 0; index < this.storedEyes.size(); index++) {
            this.setStoredEye(index, index < otherEyes.size() ? otherEyes.get(index) : ItemStack.EMPTY);
        }
    }
}