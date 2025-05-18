package com.qdd.narutofix.cap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;
import net.narutomod.item.ItemSharingan;

public interface IJutsuInventory extends INBTSerializable<NBTTagCompound> {
    ItemStackHandler getItems();
    int getSelected();

    int getCd();

    void setCd(int cd);

    int getPower();

    void setPower(int power);

    void setSelected(int index);
    void setIzanagi(boolean isIzanagi);
    boolean isIzanagi();
    ItemStack getSharingan();
    void setIzanagiSize(int IzanagiSize);
    int getIzanagiSize();
    void Izanagi();
}
