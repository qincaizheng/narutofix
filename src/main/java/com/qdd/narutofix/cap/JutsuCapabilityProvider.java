package com.qdd.narutofix.cap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

import static com.qdd.narutofix.cap.JutsuInventoryCapability.Jutsu_INV;

public class JutsuCapabilityProvider implements INBTSerializable<NBTTagCompound>, ICapabilityProvider {
    private final IJutsuInventory instance = new JutsuInventoryCapability.DefaultImpl();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return Jutsu_INV.equals(capability);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
//        System.out.println(instance);
        if (Jutsu_INV.equals(capability))
        {
            @SuppressWarnings("unchecked")
            T result = (T) instance;
            return result;
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        instance.deserializeNBT(nbt);
    }
}
