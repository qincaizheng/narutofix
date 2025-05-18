package com.qdd.narutofix.handler;

import com.qdd.narutofix.items.ItemOgi;
import com.qdd.narutofix.items.ItemSealScroll;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.item.ItemJutsu;

import static com.qdd.narutofix.items.ItemOgi.PRETASHIELD;
import static net.narutomod.item.ItemIryoJutsu.MEDMODE;
import static net.narutomod.item.ItemSenjutsu.SAGEMODE;
import static net.narutomod.item.ItemSenjutsu.WOODBUDDHA;

@Mod.EventBusSubscriber
public class RegisterHadler {
    public static Item sealscroll =new ItemSealScroll();
    public static Item ogi=new ItemOgi();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().registerAll(sealscroll,ogi);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(sealscroll, 0, new ModelResourceLocation(sealscroll.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ogi, 0, new ModelResourceLocation("narutomod:senjutsu", "inventory"));
    }
}
