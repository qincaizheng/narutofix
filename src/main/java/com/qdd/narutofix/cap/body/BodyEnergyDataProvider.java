package com.qdd.narutofix.cap.body;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BodyEnergyDataProvider implements ICapabilitySerializable<NBTTagCompound> {
    public static final net.minecraft.util.ResourceLocation NAME = new net.minecraft.util.ResourceLocation("narutofix", "body_energy");
    
    private final IBodyEnergyData instance = BodyEnergyCapabilityHandler.BODY_ENERGY_CAP.getDefaultInstance();
    
    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == BodyEnergyCapabilityHandler.BODY_ENERGY_CAP;
    }
    
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == BodyEnergyCapabilityHandler.BODY_ENERGY_CAP ? BodyEnergyCapabilityHandler.BODY_ENERGY_CAP.cast(this.instance) : null;
    }
    
    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) BodyEnergyCapabilityHandler.BODY_ENERGY_CAP.getStorage()
            .writeNBT(BodyEnergyCapabilityHandler.BODY_ENERGY_CAP, this.instance, null);
    }
    
    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        BodyEnergyCapabilityHandler.BODY_ENERGY_CAP.getStorage()
            .readNBT(BodyEnergyCapabilityHandler.BODY_ENERGY_CAP, this.instance, null, nbt);
    }
    
    public static IBodyEnergyData get(net.minecraft.entity.player.EntityPlayer player) {
        if (player == null) return null;
        return player.getCapability(BodyEnergyCapabilityHandler.BODY_ENERGY_CAP, null);
    }
}
