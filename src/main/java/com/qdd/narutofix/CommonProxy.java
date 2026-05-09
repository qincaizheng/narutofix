package com.qdd.narutofix;

import com.qdd.narutofix.cap.JutsuInventoryCapability;
import com.qdd.narutofix.handler.GuiElementLoader;
import com.qdd.narutofix.handler.JutsuXpGainHandler;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningCapabilityEvents;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningCapabilityHandler;
import com.qdd.narutofix.cap.soul.SoulEnergyCapabilityHandler;
import com.qdd.narutofix.handler.BodyAttributeHandler;
import com.qdd.narutofix.handler.BloodlineAbilityHandler;
import com.qdd.narutofix.handler.DojutsuEyeHandler;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import com.qdd.narutofix.handler.EnergyStateHandler;
import com.qdd.narutofix.handler.PlayerAwakeningHandler;
import com.qdd.narutofix.handler.NinjaXpConversionHandler;
import com.qdd.narutofix.network.PacketRegister;
import com.qdd.narutofix.world.modWorldProvider;
import com.qdd.narutofix.cap.body.BodyEnergyCapabilityHandler;
import com.qdd.narutofix.event.FirstJoinHandler;
import com.qdd.narutofix.entity.susanoo.SusanooSkeletonEntity;
import com.qdd.narutofix.entity.susanoo.SusanooClothedEntity;
import com.qdd.narutofix.entity.susanoo.SusanooWingedEntity;
import com.qdd.narutofix.entity.susanoo.SusanooMagatamaEntity;
import com.qdd.narutofix.entity.susanoo.SusanooSummonHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        new PacketRegister();
        JutsuInventoryCapability.register();
        PlayerAwakeningCapabilityHandler.register();
        SoulEnergyCapabilityHandler.register();
        BodyEnergyCapabilityHandler.register();
        MinecraftForge.EVENT_BUS.register(new PlayerAwakeningCapabilityEvents());
        MinecraftForge.EVENT_BUS.register(new SoulEnergyCapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new BodyEnergyCapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerAwakeningHandler());
        MinecraftForge.EVENT_BUS.register(new BloodlineAbilityHandler());
        MinecraftForge.EVENT_BUS.register(new BodyAttributeHandler());
        MinecraftForge.EVENT_BUS.register(new EnergyStateHandler());
        MinecraftForge.EVENT_BUS.register(new NinjaXpConversionHandler());
        MinecraftForge.EVENT_BUS.register(new JutsuXpGainHandler());
        MinecraftForge.EVENT_BUS.register(new DojutsuEyeHandler());
        MinecraftForge.EVENT_BUS.register(new com.qdd.narutofix.handler.ChakraSyncTickHandler());
        MinecraftForge.EVENT_BUS.register(new FirstJoinHandler());
        MinecraftForge.EVENT_BUS.register(new SusanooSummonHandler());
        modWorldProvider.preInit(event);

        // ---- entity registration ----------------------------------------
        int entityId = 0;
        EntityRegistry.registerModEntity(new ResourceLocation(NarutoFix.MODID, "susanoo_skeleton"), SusanooSkeletonEntity.class,
                "susanoo_skeleton", entityId++, NarutoFix.instance,
                64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(NarutoFix.MODID, "susanoo_clothed"), SusanooClothedEntity.class,
                "susanoo_clothed", entityId++, NarutoFix.instance,
                64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(NarutoFix.MODID, "susanoo_winged"), SusanooWingedEntity.class,
                "susanoo_winged", entityId++, NarutoFix.instance,
                64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(NarutoFix.MODID, "susanoo_magatama"), SusanooMagatamaEntity.class,
                "susanoo_magatama", entityId++, NarutoFix.instance,
                64, 1, true);
    }

    public void init(FMLInitializationEvent event) {
        new GuiElementLoader();
    }

    public void postInit(FMLPostInitializationEvent event) {
    }
}
