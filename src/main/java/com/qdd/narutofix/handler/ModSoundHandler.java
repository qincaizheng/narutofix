package com.qdd.narutofix.handler;

import com.qdd.narutofix.NarutoFix;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ModSoundHandler {
    public static final SoundEvent SUSANOOSOUND = new SoundEvent(new ResourceLocation(NarutoFix.MODID, "player.susanoo"));

    @SubscribeEvent
    public static void onSoundEvenrRegistration(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(SUSANOOSOUND.setRegistryName(new ResourceLocation(NarutoFix.MODID, "player.susanoo")));
//        System.out.println(event.getRegistry().getKeys()) ;

    }
}
