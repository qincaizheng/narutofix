package com.qdd.narutofix.handler;

import com.qdd.narutofix.NarutoFix;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = NarutoFix.MODID)
public final class ModSounds {
    public static final SoundEvent AMENOTEJIKARA = create("amenotejikara");
    public static final SoundEvent SUSANOO = create("player.susanoo");

    private ModSounds() {
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(AMENOTEJIKARA);
        event.getRegistry().register(SUSANOO);
    }

    private static SoundEvent create(String path) {
        ResourceLocation id = new ResourceLocation(NarutoFix.MODID, path);
        return new SoundEvent(id).setRegistryName(id);
    }
}