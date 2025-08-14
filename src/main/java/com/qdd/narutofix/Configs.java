package com.qdd.narutofix;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import com.qdd.narutofix.items.ItemSealScroll;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;



@Config(modid = NarutoFix.MODID)
public class Configs {
    @Config.LangKey("narutofix.powertick")
    @Config.Comment("Set this to how ticks for quick use jutsu & jutsu's Cooldown if setting < 0")
    @Config.Name("powertick")
    @Config.RangeInt(min = 1, max = 72000)
    public static int powertick=40;

    @Config.LangKey("narutofix.sealscroll.blacklist")
    @Config.Comment("Set this to cannot be seal")
    @Config.Name("blacklist")
    public static String[] blacklist = new String[] { "" };

    @Config.LangKey("narutofix.cooldown")
    @Config.Comment("Set this to how ticks for jutsu's Cooldown , Cooldown = powertick if this set < 0")
    @Config.Name("cooldown")
    @Config.RangeInt(min = -72000, max = 72000)
    public static int cooldown=-1;

    @Config.LangKey("narutofix.ninjarealm.seed")
    @Config.Comment("Set Seed for NinjaRealm World")
    @Config.Name("ninjarealmseed")
    @Config.RequiresMcRestart
    public static String ninjarealmseed="0";

    @Config.LangKey("narutofix.miss1")
    @Config.Comment("Set this to how Amount can miss when you have sharingan-1")
    @Config.Name("miss1")
    @Config.RangeInt(min = 0, max = 1000)
    public static int miss1=5;

    @Config.LangKey("narutofix.miss2")
    @Config.Comment("Set this to how Amount can miss when you have sharingan-2")
    @Config.Name("miss2")
    @Config.RangeInt(min = 0, max = 1000)
    public static int miss2=20;

    @Config.LangKey("narutofix.susanoo.unride")
    @Config.Comment("You can unride susanoo if True")
    @Config.Name("unride")
    @Config.RequiresWorldRestart
    public static boolean unride=false;

    @Config.LangKey("narutofix.sharingan.upgrade")
    @Config.Comment("How Chakra can upgrade Sharingan, only for 1,2,3")
    @Config.Name("upgrade")
    @Config.RangeInt(min = 0, max = 10000)
    @Config.RequiresWorldRestart
    public static int upgrade=1000;


    @Mod.EventBusSubscriber()
    public static class ConfigChangeListener {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
            ConfigManager.sync(NarutoFix.MODID, Config.Type.INSTANCE);
            for (String s : blacklist) {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
                if (block != null) {
                    ItemSealScroll.ALLOWED_TILES.clear();
                    ItemSealScroll.ALLOWED_TILES.add(s);
                }
            }
        }
    }
}
