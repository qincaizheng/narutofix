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
    @Config.Comment("快速使用忍术的蓄力时长（游戏刻）。当冷却配置小于 0 时也作为冷却时间使用。")
    @Config.Name("powertick")
    @Config.RangeInt(min = 1, max = 72000)
    public static int powertick=40;

    @Config.LangKey("narutofix.sealscroll.blacklist")
    @Config.Comment("封印卷轴不能封印的方块注册名列表。")
    @Config.Name("blacklist")
    public static String[] blacklist = new String[] { "" };

    @Config.LangKey("narutofix.cooldown")
    @Config.Comment("忍术冷却时间（游戏刻）。如果设置为小于 0，则使用蓄力时长。")
    @Config.Name("cooldown")
    @Config.RangeInt(min = -72000, max = 72000)
    public static int cooldown=-1;

    @Config.LangKey("narutofix.ninjarealm.seed")
    @Config.Comment("忍者领域世界使用的种子。")
    @Config.Name("ninjarealmseed")
    @Config.RequiresMcRestart
    public static String ninjarealmseed="0";

    @Config.LangKey("narutofix.miss1")
    @Config.Comment("一勾玉写轮眼可闪避的最大伤害数值。")
    @Config.Name("miss1")
    @Config.RangeInt(min = 0, max = 1000)
    public static int miss1=5;

    @Config.LangKey("narutofix.miss2")
    @Config.Comment("二勾玉写轮眼可闪避的最大伤害数值。")
    @Config.Name("miss2")
    @Config.RangeInt(min = 0, max = 1000)
    public static int miss2=20;

    @Config.LangKey("narutofix.susanoo.unride")
    @Config.Comment("启用后允许玩家手动离开须佐能乎。")
    @Config.Name("unride")
    @Config.RequiresWorldRestart
    public static boolean unride=false;

    @Config.LangKey("narutofix.sharingan.upgrade")
    @Config.Comment("旧版写轮眼勾玉进化的查克拉阈值。新的能量进化逻辑使用灵魂能量配置。")
    @Config.Name("upgrade")
    @Config.RangeInt(min = 0, max = 10000)
    @Config.RequiresWorldRestart
    public static int upgrade=1000;

    @Config.LangKey("narutofix.tails.spawn")
    @Config.Comment("尾兽生成使用的距离。")
    @Config.Name("distance")
    @Config.RequiresWorldRestart
    @Config.RangeInt(min = 100, max = 100000)
    public static int distance= 1000;

    @Config.Name("Indra Chakra Regen")
    @Config.Comment("旧版因陀罗直接恢复查克拉配置；当前能量系统不再使用，保留用于兼容旧配置文件。")
    public static double indraChakraRegenPerSecond = 6.0D;

    @Config.Name("Asura Health Bonus")
    @Config.Comment("阿修罗血统提供的固定最大生命值加成比例。")
    public static double asuraHealthBonus = 0.1D;

    @Config.Name("Asura Heal Per Second")
    @Config.Comment("玩家拥有阿修罗血统且受伤时，每秒恢复的生命值比例。")
    public static float asuraHealPerSecond = 0.01F;

    @Config.Name("Dual Bloodline Resistance Amplifier")
    @Config.Comment("玩家同时拥有因陀罗和阿修罗血统且处于低生命值时，抗性效果的等级。")
    public static int dualBloodlineResistanceAmplifier = 0;

    @Config.Name("Dual Bloodline Chakra Bonus")
    @Config.Comment("旧版双血脉直接恢复查克拉配置；当前能量系统不再使用，保留用于兼容旧配置文件。")
    public static double dualBloodlineChakraBonus = 4.0D;

    @Config.Name("Required Chakra for Bloodline Awaken")
    @Config.Comment("玩家觉醒血统所需的查克拉数量。")
    public static double requiredChakraForBloodlineAwaken = 1000.0D;

    @Config.Name("Second Bloodline Awaken Chance")
    @Config.Comment("玩家觉醒第二血统的概率。")
    public static double SecondBloodlineAwakenChance = 0.1D;

    @Config.Name("Rinnegan Awaken Chance")
    @Config.Comment("玩家觉醒轮回眼的概率。")
    public static double RinneganAwakenChance = 0.0001D;

    @Config.Name("Hardcore Rinnegan Awaken Chance")
    @Config.Comment("玩家在硬核模式下觉醒轮回眼的概率。")
    public static double hardcoreRinneganAwakenChance = 0.001D;

    @Config.Name("Low Health Rinnegan Awaken Chance")
    @Config.Comment("玩家处于低生命值时觉醒轮回眼的概率。")
    public static double lowHealthRinneganAwakenChance = 0.1D;

    @Config.Name("Low Health Threshold")
    @Config.Comment("低生命值判定阈值。")
    public static float lowHealthThreshold = 0.05F;

    @Config.Name("HUD X Offset")
    @Config.Comment("能量显示条在屏幕左侧与快捷栏之间的水平微调偏移。")
    @Config.RangeInt(min = -1000, max = 1000)
    public static int hudXOffset = 0;

    @Config.Name("HUD Y Offset")
    @Config.Comment("能量显示条相对快捷栏上方位置的垂直微调偏移，负数向上。")
    @Config.RangeInt(min = -1000, max = 1000)
    public static int hudYOffset = -6;

    @Config.Name("Chakra Emergency Settings")
    @Config.Comment("查克拉低量时消耗灵魂能量和肉体能量恢复查克拉的配置。")
    public static final ChakraEmergencyConfig chakraEmergency = new ChakraEmergencyConfig();

    public static class ChakraEmergencyConfig {
        @Config.Name("Chakra Emergency Threshold")
        @Config.Comment("查克拉低于最大值的该比例时触发能量转化恢复。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double chakraEmergencyThreshold = 0.10;

        @Config.Name("Chakra Emergency Energy Cost Per Tick")
        @Config.Comment("触发时每游戏刻最多从灵魂能量和肉体能量各消耗的数量。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double chakraEmergencyEnergyCostPerTick = 0.25;

        @Config.Name("Soul to Chakra Rate")
        @Config.Comment("低查克拉恢复时，每 1 点灵魂能量转化的查克拉数量。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double soulToChakraRate = 1.0;

        @Config.Name("Body to Chakra Rate")
        @Config.Comment("低查克拉恢复时，每 1 点肉体能量转化的查克拉数量。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double bodyToChakraRate = 1.0;
    }

    @Config.Name("Soul Energy Settings")
    @Config.Comment("灵魂能量配置。")
    public static final SoulEnergyConfig soul = new SoulEnergyConfig();

    public static class SoulEnergyConfig {
        @Config.Name("Soul Gain on Kill")
        @Config.Comment("玩家击杀生物时获得的当前灵魂能量。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double soulGainOnKill = 1.0;

        @Config.Name("Soul Max Gain on Kill")
        @Config.Comment("玩家击杀生物时获得的灵魂能量上限。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double soulMaxGainOnKill = 1.0;

        @Config.Name("Soul Max Gain on Death")
        @Config.Comment("玩家死亡时获得的灵魂能量上限（只增加上限，不增加当前值）。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double soulMaxGainOnDeath = 5.0;

        @Config.Name("Soul Loss Percent on Death")
        @Config.Comment("玩家死亡时损失的当前灵魂能量比例（0.0-1.0）。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double soulLossPercentOnDeath = 0.10;

        @Config.Name("Soul Initial Max")
        @Config.Comment("新玩家初始的灵魂能量上限。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double soulInitialMax = 100.0;

        @Config.Name("Soul Initial Current")
        @Config.Comment("新玩家初始的当前灵魂能量。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double soulInitialCurrent = 100.0;

        @Config.Name("Chakra Growth Per Soul")
        @Config.Comment("每 1 点灵魂能量提供的查克拉恢复倍率。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double chakraGrowthMultiplierPerSoul = 0.001;

        @Config.Name("Jutsu XP Per Soul")
        @Config.Comment("每 1 点灵魂能量提供的忍术经验获取倍率。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double jutsuXpMultiplierPerSoul = 0.001;

        @Config.Name("Jutsu Charge Per Soul")
        @Config.Comment("每 1 点灵魂能量提供的忍术蓄力速度倍率。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double jutsuChargeMultiplierPerSoul = 0.001;

        @Config.Name("Sharingan Tomoe Upgrade Soul")
        @Config.Comment("写轮眼勾玉进化所需的灵魂能量（1->2、2->3）。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double sharinganTomoeUpgradeSoul = 500.0;

        @Config.Name("Mangekyo Upgrade Soul")
        @Config.Comment("三勾玉写轮眼进化为万花筒所需的灵魂能量。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double mangekyoUpgradeSoul = 2000.0;

        @Config.Name("Eternal Mangekyo Upgrade Soul")
        @Config.Comment("万花筒进化为永恒万花筒所需的灵魂能量。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double eternalMangekyoUpgradeSoul = 5000.0;

        @Config.Name("Soul Low Threshold")
        @Config.Comment("当前灵魂能量低于上限的该比例时触发反胃和恢复逻辑。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double soulLowThreshold = 0.20;

        @Config.Name("Soul Idle Recovery Per Tick")
        @Config.Comment("灵魂能量过低时，玩家静止后每游戏刻恢复的灵魂能量。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double soulIdleRecoveryPerTick = 0.02;

        @Config.Name("Soul Sleep Recovery Per Tick")
        @Config.Comment("灵魂能量过低时，玩家睡觉时每游戏刻恢复的灵魂能量。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double soulSleepRecoveryPerTick = 0.10;

        @Config.Name("Soul Idle Required Ticks")
        @Config.Comment("玩家需要连续静止多少游戏刻后开始恢复灵魂能量。")
        @Config.RangeInt(min = 0, max = 72000)
        public int soulIdleRequiredTicks = 80;

        @Config.Name("Indra Initial Soul Multiplier")
        @Config.Comment("拥有因陀罗血脉时，初始灵魂能量上限和当前值相对默认值的倍率。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double indraInitialSoulMultiplier = 2.0;

        @Config.Name("Indra Soul Recovery Multiplier")
        @Config.Comment("拥有因陀罗血脉时，灵魂能量恢复速度倍率。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double indraSoulRecoveryMultiplier = 2.0;
    }

    @Config.Name("Body Energy Settings")
    @Config.Comment("肉体能量配置。")
    public static final BodyEnergyConfig body = new BodyEnergyConfig();

    public static class BodyEnergyConfig {
        @Config.Name("Body Max Gain on Hit")
        @Config.Comment("玩家攻击实体时获得的肉体能量上限。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double bodyMaxGainOnHit = 0.5;

        @Config.Name("Body Max Gain on Hurt")
        @Config.Comment("玩家受到伤害时获得的肉体能量上限。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double bodyMaxGainOnHurt = 0.5;

        @Config.Name("Body Max Gain on Mine")
        @Config.Comment("玩家挖掘方块时获得的肉体能量上限。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double bodyMaxGainOnMine = 0.2;

        @Config.Name("Body Cost on Hit")
        @Config.Comment("玩家攻击实体时消耗的当前肉体能量。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double bodyCostOnHit = 1.0;

        @Config.Name("Body Cost on Hurt")
        @Config.Comment("玩家受到伤害时消耗的当前肉体能量。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double bodyCostOnHurt = 1.0;

        @Config.Name("Body Cost on Mine")
        @Config.Comment("玩家挖掘方块时消耗的当前肉体能量。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double bodyCostOnMine = 0.5;

        @Config.Name("Body Initial Max")
        @Config.Comment("新玩家初始的肉体能量上限。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double bodyInitialMax = 100.0;

        @Config.Name("Body Initial Current")
        @Config.Comment("新玩家初始的当前肉体能量。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double bodyInitialCurrent = 100.0;

        @Config.Name("HP Multiplier Per Body")
        @Config.Comment("每 1 点肉体能量提供的最大生命值倍率。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double hpMultiplierPerBody = 0.01;

        @Config.Name("Armor Per Body")
        @Config.Comment("每 1 点肉体能量增加的护甲值。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double armorPerBody = 0.01;

        @Config.Name("HP Regen Per Body")
        @Config.Comment("每 1 点肉体能量每游戏刻恢复的生命值。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double hpRegenPerBody = 0.001;

        @Config.Name("Move Speed Per Body")
        @Config.Comment("每 1 点肉体能量提供的移动速度倍率。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double moveSpeedPerBody = 0.0005;

        @Config.Name("Attack Damage Per Body")
        @Config.Comment("每 1 点肉体能量提供的攻击力倍率。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double attackDamagePerBody = 0.01;

        @Config.Name("Attack Speed Per Body")
        @Config.Comment("每 1 点肉体能量提供的攻击速度倍率。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double attackSpeedPerBody = 0.005;

        @Config.Name("Body Low Threshold")
        @Config.Comment("当前肉体能量低于上限的该比例时触发负面效果和饱食度转化。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double bodyLowThreshold = 0.20;

        @Config.Name("Body Food Cost Per Tick")
        @Config.Comment("肉体能量过低时，每游戏刻最多消耗的饱食度或饱和度。")
        @Config.RangeDouble(min = 0.0, max = 20.0)
        public double bodyFoodCostPerTick = 0.10;

        @Config.Name("Food to Body Rate")
        @Config.Comment("每消耗 1 点饱食度或饱和度恢复的肉体能量。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double foodToBodyRate = 5.0;

        @Config.Name("Military Rations Body Restore")
        @Config.Comment("普通兵粮丸食用后恢复的肉体能量。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double militaryRationsBodyRestore = 40.0;

        @Config.Name("Gold Military Rations Body Restore")
        @Config.Comment("金色兵粮丸食用后恢复的肉体能量。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double goldMilitaryRationsBodyRestore = 100.0;

        @Config.Name("Asura Initial Body Multiplier")
        @Config.Comment("拥有阿修罗血脉时，初始肉体能量上限和当前值相对默认值的倍率。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double asuraInitialBodyMultiplier = 2.0;

        @Config.Name("Asura Body Recovery Multiplier")
        @Config.Comment("拥有阿修罗血脉时，肉体能量恢复速度倍率。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double asuraBodyRecoveryMultiplier = 2.0;
    }

    @Config.Name("XP Conversion Settings")
    @Config.Comment("根据灵魂能量当前值和肉体能量当前值中的较小值，将两种能量转化为忍者经验的配置。")
    public static final XpConversionConfig xpConversion = new XpConversionConfig();

    public static class XpConversionConfig {
        @Config.Name("Ninja XP Conversion Rate")
        @Config.Comment("每个间隔内，灵魂能量当前值和肉体能量当前值中的较小值转化为忍者经验的比例。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double ninjaXpConversionRate = 0.1;

        @Config.Name("XP Conversion Interval")
        @Config.Comment("每次忍者经验转化之间的游戏刻间隔。")
        @Config.RangeInt(min = 1, max = 1200)
        public int ninjaXpConversionIntervalTicks = 20;
    }

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
