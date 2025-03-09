package com.qdd.narutofix;

import com.qdd.narutofix.cap.JutsuInventoryCapability;
import com.qdd.narutofix.handler.GuiElementLoader;
import com.qdd.narutofix.network.PacketRegister;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        new PacketRegister();
        JutsuInventoryCapability.register();
    }

    public void init(FMLInitializationEvent event)
    {
        new GuiElementLoader();

    }

    public void postInit(FMLPostInitializationEvent event)
    {

    }
}