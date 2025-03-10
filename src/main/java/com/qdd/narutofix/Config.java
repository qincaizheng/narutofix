package com.qdd.narutofix;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class Config {
    public static Configuration config;
    public static int powertick=40;

    public static void initialize(File file)
    {
        config = new Configuration(file);
        config.load();

        load();

        MinecraftForge.EVENT_BUS.register(ConfigChangeListener.class);
    }

    public static void load() {
        String desc = "Set this to how ticks for quick use jutsu";
        powertick = config.get(Configuration.CATEGORY_GENERAL,"narutofix.powertick"
                , powertick, desc).getInt();

        if(config.hasChanged())	config.save();
    }

    public static void save()
    {
        config.save();
    }

    public static class ConfigChangeListener {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
            if(eventArgs.getModID().equals(NarutoFix.MODID))
                load();
        }
    }
}
