package com.qdd.narutofix.cap.soul;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SoulEnergyDataStorage implements Capability.IStorage<ISoulEnergyData> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<ISoulEnergyData> capability, ISoulEnergyData instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setDouble("current", instance.getCurrent());
        compound.setDouble("max", instance.getMax());
        return compound;
    }

    @Override
    public void readNBT(Capability<ISoulEnergyData> capability, ISoulEnergyData instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            instance.setCurrent(compound.getDouble("current"));
            instance.setMax(compound.getDouble("max"));
        }
    }
}
