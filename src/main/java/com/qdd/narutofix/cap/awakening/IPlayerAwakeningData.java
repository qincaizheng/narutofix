package com.qdd.narutofix.cap.awakening;

import com.qdd.narutofix.awakening.Bloodline;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IPlayerAwakeningData {
    boolean hasBloodline(Bloodline bloodline);

    void setBloodline(Bloodline bloodline, boolean unlocked);

    boolean hasRinneganAwakened();

    void setRinneganAwakened(boolean awakened);

    ItemStack getEquippedEye();

    void setEquippedEye(ItemStack itemStack);

    NonNullList<ItemStack> getStoredEyes();

    void setStoredEye(int index, ItemStack itemStack);

    default int getStoredEyeSlotCount() {
        return this.getStoredEyes().size();
    }

    default boolean hasIndra() {
        return this.hasBloodline(Bloodline.INDRA);
    }

    default boolean hasAsura() {
        return this.hasBloodline(Bloodline.ASURA);
    }

    default boolean hasAnyBloodline() {
        return this.hasIndra() || this.hasAsura();
    }

    default boolean hasBothBloodlines() {
        return this.hasIndra() && this.hasAsura();
    }

    default boolean hasExactlyOneBloodline() {
        return this.hasIndra() ^ this.hasAsura();
    }

    void copyFrom(IPlayerAwakeningData other);
}