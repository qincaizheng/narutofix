package com.qdd.narutofix.handler;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import com.qdd.narutofix.items.CopyJutsuScroll;
import com.qdd.narutofix.items.ItemSealScroll;
import com.qdd.narutofix.items.ObsidianChokuto;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.narutomod.item.ItemJutsu;

@Mod.EventBusSubscriber
public class RegisterHadler {
    public static Item sealscroll =new ItemSealScroll();
    public static Item obsidianchokuto = new ObsidianChokuto();
    public static Item COPY_JUTSU_SCROLL = new CopyJutsuScroll();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().registerAll(sealscroll, obsidianchokuto, COPY_JUTSU_SCROLL);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(sealscroll, 0, new ModelResourceLocation(sealscroll.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(obsidianchokuto, 0, new ModelResourceLocation(obsidianchokuto.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(COPY_JUTSU_SCROLL, 0, new ModelResourceLocation(COPY_JUTSU_SCROLL.getRegistryName(), "inventory"));
    }

}
