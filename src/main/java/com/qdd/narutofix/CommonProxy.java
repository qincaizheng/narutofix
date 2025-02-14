package com.qdd.narutofix;

import com.qdd.narutofix.gui.GuiElementLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {

    }

    public void init(FMLInitializationEvent event)
    {
        new GuiElementLoader();

    }

    public void postInit(FMLPostInitializationEvent event)
    {

    }
}