package com.qdd.narutofix;

import com.qdd.narutofix.gui.GuiElementLoader;
import com.qdd.narutofix.network.PacketOpenCustomInventory;
import com.qdd.narutofix.network.PacketRegister;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        new PacketRegister();
    }

    public void init(FMLInitializationEvent event)
    {
        new GuiElementLoader();

    }

    public void postInit(FMLPostInitializationEvent event)
    {

    }
}