package com.qdd.narutofix.handler;

import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.container.JutsuContainer;
import com.qdd.narutofix.gui.GuiJutsuCntainer;
import com.qdd.narutofix.gui.GuiEyeStorage;
import com.qdd.narutofix.cap.eye.EyeInventoryConstants;
import com.qdd.narutofix.gui.ContainerEyeStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiElementLoader implements IGuiHandler {
    public static final int GUI_Custom = 1;
    public static final int GUI_Jutsu = 2;
    public static final int GUI_EYE_STORAGE = EyeInventoryConstants.STORAGE_GUI_ID;

    public GuiElementLoader() {
        NetworkRegistry.INSTANCE.registerGuiHandler(NarutoFix.instance, this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GUI_Jutsu:
                return new JutsuContainer(player, !world.isRemote);
            case GUI_EYE_STORAGE:
                return new ContainerEyeStorage(player.inventory);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GUI_Jutsu:
                return new GuiJutsuCntainer(new JutsuContainer(player, !world.isRemote));
            case GUI_EYE_STORAGE:
                return new GuiEyeStorage(new ContainerEyeStorage(player.inventory));
            default:
                return null;
        }
    }
}
