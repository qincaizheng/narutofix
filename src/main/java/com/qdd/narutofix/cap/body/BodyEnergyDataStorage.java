package com.qdd.narutofix.cap.body;

import com.qdd.narutofix.Configs;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class BodyEnergyDataStorage implements Capability.IStorage<IBodyEnergyData> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IBodyEnergyData> capability, IBodyEnergyData instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setDouble("current", instance.getCurrent());
        compound.setDouble("max", instance.getMax());
        return compound;
    }
    
    @Override
    public void readNBT(Capability<IBodyEnergyData> capability, IBodyEnergyData instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            double max = compound.hasKey("max") ? compound.getDouble("max") : Configs.body.bodyInitialMax;
            if (max <= 0.0D) {
                max = Configs.body.bodyInitialMax;
            }
            instance.setMax(max);
            double current = compound.hasKey("current") ? compound.getDouble("current") : Configs.body.bodyInitialCurrent;
            instance.setCurrent(current);
        }
    }
}
