package com.qdd.narutofix.gui;

import com.qdd.narutofix.gui.GuiEyeStorage;
import com.qdd.narutofix.cap.eye.EyeInventoryConstants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class EyeGuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == EyeInventoryConstants.STORAGE_GUI_ID) {
            return new ContainerEyeStorage(player.inventory);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == EyeInventoryConstants.STORAGE_GUI_ID) {
            return new GuiEyeStorage(new ContainerEyeStorage(player.inventory));
        }
        return null;
    }
}