package com.qdd.narutofix;

import com.qdd.narutofix.event.AmenotejikaraOverlayHandler;
import com.qdd.narutofix.event.EquippedEyeRenderLayer;
import com.qdd.narutofix.event.SusanooMouseFireHandler;
import com.qdd.narutofix.keybind.EyeKeyHandler;
import com.qdd.narutofix.keybind.KeyLoader;
import com.qdd.narutofix.entity.susanoo.RenderSusanooSkeleton;
import com.qdd.narutofix.entity.susanoo.RenderSusanooClothed;
import com.qdd.narutofix.entity.susanoo.RenderSusanooWinged;
import com.qdd.narutofix.entity.susanoo.RenderSusanooMagatama;
import com.qdd.narutofix.entity.susanoo.SusanooSkeletonEntity;
import com.qdd.narutofix.entity.susanoo.SusanooClothedEntity;
import com.qdd.narutofix.entity.susanoo.SusanooWingedEntity;
import com.qdd.narutofix.entity.susanoo.SusanooMagatamaEntity;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    private final EyeKeyHandler eyeKeyHandler = new EyeKeyHandler();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        RenderingRegistry.registerEntityRenderingHandler(SusanooSkeletonEntity.class, RenderSusanooSkeleton::new);
        RenderingRegistry.registerEntityRenderingHandler(SusanooClothedEntity.class, RenderSusanooClothed::new);
        RenderingRegistry.registerEntityRenderingHandler(SusanooWingedEntity.class, RenderSusanooWinged::new);
        RenderingRegistry.registerEntityRenderingHandler(SusanooMagatamaEntity.class, RenderSusanooMagatama::new);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        new KeyLoader();
        EquippedEyeRenderLayer.registerLayers();
        this.eyeKeyHandler.register();
        MinecraftForge.EVENT_BUS.register(this.eyeKeyHandler);
        MinecraftForge.EVENT_BUS.register(new AmenotejikaraOverlayHandler());
        MinecraftForge.EVENT_BUS.register(new SusanooMouseFireHandler());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
