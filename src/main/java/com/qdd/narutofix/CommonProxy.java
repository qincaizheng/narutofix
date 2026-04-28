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
import com.qdd.narutofix.handler.PlayerAwakeningHandler;
import com.qdd.narutofix.handler.NinjaXpConversionHandler;
import com.qdd.narutofix.network.PacketRegister;
import com.qdd.narutofix.world.modWorldProvider;
import com.qdd.narutofix.cap.body.BodyEnergyCapabilityHandler;

import net.minecraftforge.common.MinecraftForge;
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
        MinecraftForge.EVENT_BUS.register(new NinjaXpConversionHandler());
        MinecraftForge.EVENT_BUS.register(new JutsuXpGainHandler());
        MinecraftForge.EVENT_BUS.register(new DojutsuEyeHandler());
        modWorldProvider.preInit(event);
    }

    public void init(FMLInitializationEvent event) {
        new GuiElementLoader();
    }

    public void postInit(FMLPostInitializationEvent event) {
    }
}
