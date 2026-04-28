package com.qdd.narutofix.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import com.qdd.narutofix.NarutoFix;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.narutomod.creativetab.TabModTab;

@Mod.EventBusSubscriber(modid = NarutoFix.MODID)
public final class ModItems {
    private static final ItemArmor.ArmorMaterial SIX_TOMOE_RINNEGAN_MATERIAL = EnumHelper.addArmorMaterial(
            NarutoFix.MODID + ":six_tomoe_rinnegan",
            NarutoFix.MODID + ":item/rinnegantomoe",
            25,
            new int[]{2, 5, 6, 15},
            0,
            SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:dojutsu")),
            2.0F
    );

    public static final ItemSixTomoeRinnegan SIX_TOMOE_RINNEGAN = new ItemSixTomoeRinnegan(SIX_TOMOE_RINNEGAN_MATERIAL);
    public static final Item IRON_JAY_SEN_RIGAN = new Item().setRegistryName(NarutoFix.MODID, "ironjaysenrigan").setTranslationKey(NarutoFix.MODID + ".ironjaysenrigan");
    public static final Item MADARA_RINNE_EMS_ITEM = new Item().setRegistryName(NarutoFix.MODID, "madararinneemsitem").setTranslationKey(NarutoFix.MODID + ".madararinneemsitem");


    private ModItems() {
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(SIX_TOMOE_RINNEGAN);
        event.getRegistry().register(IRON_JAY_SEN_RIGAN);
        event.getRegistry().register(MADARA_RINNE_EMS_ITEM);
    }

    @SubscribeEvent
    public static void onRegisterModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(SIX_TOMOE_RINNEGAN, 0, new ModelResourceLocation(SIX_TOMOE_RINNEGAN.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(IRON_JAY_SEN_RIGAN, 0, new ModelResourceLocation(IRON_JAY_SEN_RIGAN.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(MADARA_RINNE_EMS_ITEM, 0, new ModelResourceLocation(MADARA_RINNE_EMS_ITEM.getRegistryName(), "inventory"));
    }
}