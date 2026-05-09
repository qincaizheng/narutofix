package com.qdd.narutofix.cap.soul;

import com.qdd.narutofix.Configs;
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
        compound.setInteger("dataVersion", instance.getDataVersion());
        compound.setInteger("bloodlineAppliedVersion", instance.getBloodlineAppliedVersion());
        return compound;
    }

    @Override
    public void readNBT(Capability<ISoulEnergyData> capability, ISoulEnergyData instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            double max = compound.hasKey("max") ? compound.getDouble("max") : Configs.soul.soulInitialMax;
            if (max <= 0.0D) {
                max = Configs.soul.soulInitialMax;
            }
            instance.setMax(max);
            double current = compound.hasKey("current") ? compound.getDouble("current") : Configs.soul.soulInitialCurrent;
            instance.setCurrent(current);
            int dataVersion = compound.hasKey("dataVersion") ? compound.getInteger("dataVersion") : 0;
            instance.setDataVersion(dataVersion);
            int bloodlineAppliedVersion = compound.hasKey("bloodlineAppliedVersion") ? compound.getInteger("bloodlineAppliedVersion") : 0;
            instance.setBloodlineAppliedVersion(bloodlineAppliedVersion);
        }
    }
}
