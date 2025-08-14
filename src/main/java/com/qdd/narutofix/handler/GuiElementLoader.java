package com.qdd.narutofix.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.container.JutsuContainer;
import com.qdd.narutofix.gui.GuiJutsuCntainer;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiElementLoader implements IGuiHandler {
    public static final int GUI_Custom = 1;
    public static final int GUI_Jutsu = 2;

    public GuiElementLoader()
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(NarutoFix.instance, this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {

            case GUI_Jutsu:
                return new JutsuContainer(player,!world.isRemote);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case GUI_Jutsu:
                return new GuiJutsuCntainer(new JutsuContainer(player,!world.isRemote));
            default:
                return null;
        }
    }
}
