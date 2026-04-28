package com.qdd.narutofix.cap.soul;

import com.qdd.narutofix.NarutoFix;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

public class SoulEnergyDataProvider implements ICapabilitySerializable<NBTBase> {
    public static final ResourceLocation NAME = new ResourceLocation(NarutoFix.MODID, "soul_energy");

    private final ISoulEnergyData instance = SoulEnergyCapabilityHandler.SOUL_ENERGY_CAP.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == SoulEnergyCapabilityHandler.SOUL_ENERGY_CAP;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == SoulEnergyCapabilityHandler.SOUL_ENERGY_CAP
                ? SoulEnergyCapabilityHandler.SOUL_ENERGY_CAP.cast(this.instance)
                : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return SoulEnergyCapabilityHandler.SOUL_ENERGY_CAP.getStorage()
                .writeNBT(SoulEnergyCapabilityHandler.SOUL_ENERGY_CAP, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        SoulEnergyCapabilityHandler.SOUL_ENERGY_CAP.getStorage()
                .readNBT(SoulEnergyCapabilityHandler.SOUL_ENERGY_CAP, this.instance, null, nbt);
    }

    public static ISoulEnergyData get(EntityPlayer player) {
        if (player == null) return null;
        return player.getCapability(SoulEnergyCapabilityHandler.SOUL_ENERGY_CAP, null);
    }
}
