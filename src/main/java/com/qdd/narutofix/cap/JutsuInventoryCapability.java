package com.qdd.narutofix.cap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import com.qdd.narutofix.Configs;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.items.ItemStackHandler;
import net.narutomod.item.ItemSharingan;

import javax.annotation.Nullable;

public class JutsuInventoryCapability {
    @CapabilityInject(IJutsuInventory.class)
    public static Capability<IJutsuInventory> Jutsu_INV = null;

    private static final int JutsuSize=37;

    public static void register() {
        CapabilityManager.INSTANCE.register(IJutsuInventory.class, new Storage(), DefaultImpl::new);
    }

    private static class Storage implements Capability.IStorage<IJutsuInventory> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IJutsuInventory> capability, IJutsuInventory instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("Selected", instance.getSelected());
            nbt.setBoolean("isIzanagi", instance.isIzanagi());
            nbt.setInteger("IzanagiSize", instance.getIzanagiSize());
            // 直接序列化 ItemStackHandler
            nbt.setTag("JutsuItems", ((DefaultImpl)instance).itemHandler.serializeNBT());
            return nbt;
        }

        @Override
        public void readNBT(Capability<IJutsuInventory> capability, IJutsuInventory instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound tag = (NBTTagCompound) nbt;
            instance.setSelected(tag.hasKey("Selected") ? tag.getInteger("Selected") : 0);
            instance.setIzanagi(tag.getBoolean("isIzanagi"));
            instance.setIzanagiSize(tag.getInteger("IzanagiSize"));
            ((DefaultImpl)instance).itemHandler.deserializeNBT(tag.getCompoundTag("JutsuItems"));
        }

    }

    public static class DefaultImpl implements IJutsuInventory {
        private final ItemStackHandler  itemHandler  = new ItemStackHandler(JutsuSize);
        private int selected;
        private boolean isIzanagi;
        private int IzanagiSize;
        private int cd= Configs.cooldown;
        private int power=Configs.powertick;

        @Override
        public ItemStackHandler getItems() {
            return this.itemHandler ;
        }

        @Override
        public int getSelected() {
//            System.out.println(this.selected);
            return this.selected;
        }

        @Override
        public int getCd() {
            return cd;
        }

        @Override
        public void setCd(int cd) {
            this.cd = Math.abs(cd);
        }

        @Override
        public int getPower() {
            return power;
        }

        @Override
        public void setPower(int power) {
            this.power = Math.abs(power);
        }

        @Override
        public void setSelected(int index) {
            this.selected = MathHelper.clamp(index, 0, 8);
        }

        public ItemStack getCurrentItem() {
            return itemHandler.getStackInSlot(selected);
        }

        @Override
        public ItemStack getSharingan(){
            return itemHandler.getStackInSlot(27);
        }

        @Override
        public void setIzanagi(boolean isIzanagi) {
            if(!isIzanagi){
                this.isIzanagi=false;
            }else{
                for(int i = 28;i<28+IzanagiSize;i++){
                    if (this.itemHandler.getStackInSlot(i).getItem() instanceof ItemSharingan.Base) {
                        this.isIzanagi=true;
                    }
                }

            }
        }

        @Override
        public void Izanagi(){
            for(int i = IzanagiSize+27;i>27;i--){
                if (this.itemHandler.getStackInSlot(i).getItem() instanceof ItemSharingan.Base) {
                    this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }

        @Override
        public boolean isIzanagi(){
            for(int i = 28;i<28+IzanagiSize;i++){
                if (this.itemHandler.getStackInSlot(i).getItem() instanceof ItemSharingan.Base) {
                    return this.isIzanagi;
                }
            }
        return false;
        }

        @Override
        public int getIzanagiSize() {
            return this.IzanagiSize;
        }

        @Override
        public void setIzanagiSize(int size) {
            this.IzanagiSize=size;
        }


        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) Jutsu_INV.getStorage().writeNBT(Jutsu_INV, this, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            Jutsu_INV.getStorage().readNBT(Jutsu_INV, this, null, nbt);
        }

    }
}
