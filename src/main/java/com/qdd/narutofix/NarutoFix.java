package com.qdd.narutofix;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;


@Mod(modid = NarutoFix.MODID, name = NarutoFix.NAME, version = NarutoFix.VERSION, dependencies = "required-after:narutomod")
public class NarutoFix {

    public static final String MODID = "narutofix";
    public static final String NAME = "narutofix";
    public static final String VERSION = "0.1.0";
    public static final SimpleNetworkWrapper PACKET_HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel("narutofix:a");
    @SidedProxy(clientSide = "com.qdd.narutofix.ClientProxy",
            serverSide = "com.qdd.narutofix.CommonProxy")
    public static CommonProxy proxy;

    @Instance(NarutoFix.MODID)
    public static NarutoFix instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }
}