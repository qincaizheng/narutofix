package com.qdd.narutofix.cap.awakening;

import com.qdd.narutofix.NarutoFix;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

public class PlayerAwakeningDataProvider implements ICapabilitySerializable<NBTBase> {
    public static final ResourceLocation NAME = new ResourceLocation(NarutoFix.MODID, "player_awakening_data");

    @CapabilityInject(IPlayerAwakeningData.class)
    public static final Capability<IPlayerAwakeningData> CAPABILITY = null;

    private final IPlayerAwakeningData instance = new PlayerAwakeningData();

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CAPABILITY ? CAPABILITY.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return CAPABILITY.getStorage().writeNBT(CAPABILITY, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, this.instance, null, nbt);
    }

    public static IPlayerAwakeningData get(EntityPlayer player) {
        return player.getCapability(CAPABILITY, null);
    }
}