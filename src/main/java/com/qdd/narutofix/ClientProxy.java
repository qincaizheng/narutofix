package com.qdd.narutofix;

import com.qdd.narutofix.event.AmenotejikaraOverlayHandler;
import com.qdd.narutofix.event.EquippedEyeRenderLayer;
import com.qdd.narutofix.keybind.EyeKeyHandler;
import com.qdd.narutofix.keybind.KeyLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    private final EyeKeyHandler eyeKeyHandler = new EyeKeyHandler();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        new KeyLoader();
        EquippedEyeRenderLayer.registerLayers();
        this.eyeKeyHandler.register();
        MinecraftForge.EVENT_BUS.register(this.eyeKeyHandler);
        MinecraftForge.EVENT_BUS.register(new AmenotejikaraOverlayHandler());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
