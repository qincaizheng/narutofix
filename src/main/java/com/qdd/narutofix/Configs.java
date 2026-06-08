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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



@Config(modid = NarutoFix.MODID)
public class Configs {
    private static final Logger LOGGER = LogManager.getLogger("NarutoFix-Config");
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


    @Config.LangKey("narutofix.sharingan.evolution.source")
    @Config.Comment("写轮眼进化来源：VANILLA（仅原版 narutomod 规则触发）、SOUL（仅灵魂能量阈值触发并尽量屏蔽原版进化）、BOTH（原版规则或灵魂能量任一满足即可触发）。")
    @Config.Name("sharinganEvolutionSource")
    public static SharinganEvolutionSource sharinganEvolutionSource = SharinganEvolutionSource.BOTH;

    public enum SharinganEvolutionSource {
        VANILLA,
        SOUL,
        BOTH
    }

    @Config.LangKey("narutofix.tails.spawn")
    @Config.Comment("尾兽生成使用的距离。")
    @Config.Name("distance")
    @Config.RequiresWorldRestart
    @Config.RangeInt(min = 100, max = 100000)
    public static int distance= 1000;

    @Config.Name("Dual Bloodline Resistance Amplifier")
    @Config.Comment("玩家同时拥有因陀罗和阿修罗血统且处于低生命值时，抗性效果的等级。")
    public static int dualBloodlineResistanceAmplifier = 0;

    @Config.Name("Asura Health Bonus")
    @Config.Comment("阿修罗血统提供的固定最大生命值加成比例。")
    public static double asuraHealthBonus = 0.1D;

    @Config.Name("Asura Heal Per Second")
    @Config.Comment("玩家拥有阿修罗血统且受伤时，每秒恢复的生命值比例。")
    public static float asuraHealPerSecond = 0.01F;

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
    public static int hudYOffset = 30;

    @Config.Name("Susanoo Settings")
    @Config.Comment("须佐能乎设置")
    public static SusanooConfig susanoo = new SusanooConfig();

    public static class SusanooConfig {
        @Config.Name("BATTLEXP Required L0")
        @Config.Comment("召唤须佐L0所需的BATTLEXP下限")
        public double bxpRequiredL0 = 2000.0;

        @Config.Name("BATTLEXP Required L1")
        @Config.Comment("升级至L1（骨架完整上身）所需的BATTLEXP下限")
        public double bxpRequiredL1 = 5000.0;

        @Config.Name("BATTLEXP Required L2")
        @Config.Comment("升级至L2（着铠无腿）所需的BATTLEXP下限")
        public double bxpRequiredL2 = 10000.0;

        @Config.Name("BATTLEXP Required L3")
        @Config.Comment("升级至L3（着铠有腿）所需的BATTLEXP下限")
        public double bxpRequiredL3 = 20000.0;

        @Config.Name("BATTLEXP Required L4")
        @Config.Comment("升级至L4（完成体·翼）所需的BATTLEXP下限")
        public double bxpRequiredL4 = 40000.0;

        @Config.Name("Base Chakra Usage")
        @Config.Comment("召唤/升级消耗的基础查克拉量")
        public double baseChakraUsage = 500.0;
    }

    @Config.Name("Chakra Emergency Settings")
    @Config.Comment("查克拉低量时消耗灵魂能量和肉体能量恢复查克拉的配置。")
    public static final ChakraEmergencyConfig chakraEmergency = new ChakraEmergencyConfig();

    public static class ChakraEmergencyConfig {
        @Config.Name("Chakra Emergency Threshold")
        @Config.Comment("查克拉低于最大值的该比例时触发能量转化恢复。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double chakraEmergencyThreshold = 0.10;

        @Config.Name("Chakra Emergency Energy Cost Per Tick")
        @Config.Comment("触发时每游戏刻最多从灵魂能量和肉体能量各消耗的百分比（0~1 表示 0%~100%）。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double chakraEmergencyEnergyCostPercent = 0.0025;

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

        @Config.Name("Jutsu XP Log Base")
        @Config.Comment("灵魂能量→忍术经验倍率的对数底数。数值越大，前期增长越慢。默认 e（自然对数）。")
        @Config.RangeDouble(min = 1.01, max = 10000.0)
        public double jutsuXpLogBase = 2.718281828459045;

        @Config.Name("Jutsu XP Max Multiplier")
        @Config.Comment("灵魂能量→忍术经验倍率的上限封顶值。")
        @Config.RangeDouble(min = 1.0, max = 100000.0)
        public double jutsuXpMaxMultiplier = 5.0;

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

        @Config.Name("Indra Initial Soul Bonus Max")
        @Config.Comment("拥有因陀罗血脉时，额外发放的初始灵魂能量上限固定值。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double indraInitialSoulBonusMax = 100.0;

        @Config.Name("Indra Initial Soul Bonus Current")
        @Config.Comment("拥有因陀罗血脉时，额外发放的初始灵魂能量当前值固定值。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double indraInitialSoulBonusCurrent = 100.0;

        @Config.Name("Indra Soul Recovery Multiplier")
        @Config.Comment("拥有因陀罗血脉时，灵魂能量恢复速度倍率。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double indraSoulRecoveryMultiplier = 2.0;
    }

    @Config.Name("Soul Environment Recovery Settings")
    @Config.Comment("玩家站在特定方块上时自动恢复灵魂能量的配置。")
    public static final SoulEnvironmentConfig soulEnvironment = new SoulEnvironmentConfig();

    public static class SoulEnvironmentConfig {
        @Config.Name("Enabled")
        @Config.Comment("启用环境灵魂恢复。")
        public boolean enabled = true;

        @Config.Name("Recovery Blocks")
        @Config.Comment("触发恢复的方块注册名列表。默认仅 minecraft:soul_sand。")
        public String[] recoveryBlocks = new String[]{"minecraft:soul_sand"};

        @Config.Name("Recovery Percent Per Second")
        @Config.Comment("每秒恢复灵魂能量上限的百分比（0~1 表示 0%~100%）。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double recoveryPercentPerSecond = 0.01;

        @Config.Name("Check Interval Ticks")
        @Config.Comment("每次检测之间的游戏刻间隔。默认 20 tick = 1 秒。")
        @Config.RangeInt(min = 1, max = 72000)
        public int checkIntervalTicks = 20;
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

        @Config.Name("Max Armor")
        @Config.Comment("肉体能量提供的护甲值上限。")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double maxArmor = 30.0;

        @Config.Name("HP Regen Per Body")
        @Config.Comment("每 1 点肉体能量每游戏刻恢复的生命值。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double hpRegenPerBody = 0.001;

        @Config.Name("Move Speed Per Body")
        @Config.Comment("每 1 点肉体能量提供的移动速度倍率。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double moveSpeedPerBody = 0.0005;

        @Config.Name("Max Move Speed Multiplier")
        @Config.Comment("肉体能量提供的移动速度倍率上限。")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double maxMoveSpeedMultiplier = 2.0;

        @Config.Name("Attack Damage Per Body")
        @Config.Comment("每 1 点肉体能量提供的攻击力倍率。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double attackDamagePerBody = 0.01;

        @Config.Name("Max Attack Damage Multiplier")
        @Config.Comment("肉体能量提供的攻击力倍率上限。")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double maxAttackDamageMultiplier = 10.0;

        @Config.Name("Attack Speed Per Body")
        @Config.Comment("每 1 点肉体能量提供的攻击速度倍率。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double attackSpeedPerBody = 0.005;

        @Config.Name("Max Attack Speed Multiplier")
        @Config.Comment("肉体能量提供的攻击速度倍率上限。")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double maxAttackSpeedMultiplier = 3.0;
        @Config.Name("Body Log Base")
        @Config.Comment("肉体能量→属性增幅的对数底数。数值越大，越晚进入衰减。默认 e（自然对数）。")
        @Config.RangeDouble(min = 1.01, max = 10000.0)
        public double bodyLogBase = 2.718281828459045;

        @Config.Name("Body Log Max Multiplier")
        @Config.Comment("肉体能量→属性增幅的对数倍率上限封顶值。")
        @Config.RangeDouble(min = 1.0, max = 100000.0)
        public double bodyLogMaxMultiplier = 10.0;


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

        @Config.Name("Allow Consume Hunger")
        @Config.Comment("是否允许消耗饱食度（foodLevel）恢复肉体能量。默认 true：统一消耗 foodLevel，受 minFoodLevel 保护其剩余值。")
        public boolean allowConsumeHunger = true;

        @Config.Name("Min Food Level for Low Body")
        @Config.Comment("低肉体能量阈值模式下，饱食度最低保留值（0-20），默认 2.0 保留一格。")
        @Config.RangeDouble(min = 0.0, max = 20.0)
        public double minFoodLevelForLowBody = 2.0;

        @Config.Name("Military Rations Body Restore")
        @Config.Comment("普通兵粮丸食用后恢复的肉体能量。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double militaryRationsBodyRestore = 40.0;

        @Config.Name("Gold Military Rations Body Restore")
        @Config.Comment("金色兵粮丸食用后恢复的肉体能量。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double goldMilitaryRationsBodyRestore = 100.0;

        @Config.Name("Asura Initial Body Bonus Max")
        @Config.Comment("拥有阿修罗血脉时，额外发放的初始肉体能量上限固定值。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double asuraInitialBodyBonusMax = 100.0;

        @Config.Name("Asura Initial Body Bonus Current")
        @Config.Comment("拥有阿修罗血脉时，额外发放的初始肉体能量当前值固定值。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double asuraInitialBodyBonusCurrent = 100.0;

        @Config.Name("Asura Body Recovery Multiplier")
        @Config.Comment("拥有阿修罗血脉时，肉体能量恢复速度倍率。")
        @Config.RangeDouble(min = 0.0, max = 1000.0)
        public double asuraBodyRecoveryMultiplier = 2.0;

        @Config.Name("Enable Compact Health Bar")
        @Config.Comment("启用紧凑型血量条（替换心形渲染），关闭则使用原版心形渲染。")
        public boolean enableCompactHealthBar = true;
    }



    @Config.Name("Tailed Beast Boss Bar Settings")
    @Config.Comment("尾兽 Boss 条显示与坐标播报配置。")
    public static final TailsConfig tails = new TailsConfig();

    public static class TailsConfig {
        @Config.Name("Boss Bar Range")
        @Config.Comment("尾兽 Boss 条可见范围（方块）。玩家在此距离内才显示 Boss 条，超出则隐藏。")
        @Config.RangeInt(min = 1, max = 10000)
        public int bossBarRange = 100;

        @Config.Name("Coordinate Broadcast Interval")
        @Config.Comment("尾兽坐标聊天栏播报间隔（游戏刻）。默认 600 = 30 秒。")
        @Config.RangeInt(min = 1, max = 72000)
        public int coordinateBroadcastInterval = 600;
    }

    @Config.Name("Crouch Chakra Exchange Settings")
    @Config.Comment("玩家静止下蹲时消耗灵魂/肉体能量恢复查克拉的配置。替换旧版无成本静止查克拉自然恢复。")
    public static final CrouchChakraExchangeConfig chakraCrouchExchange = new CrouchChakraExchangeConfig();

    public static class CrouchChakraExchangeConfig {
        @Config.Name("Enabled")
        @Config.Comment("启用静止下蹲查克拉转化。")
        public boolean enabled = true;

        @Config.Name("Required Stationary Ticks")
        @Config.Comment("玩家需要连续静止多少游戏刻后开始触发查克拉转化。")
        @Config.RangeInt(min = 0, max = 72000)
        public int requiredTicks = 40;

        @Config.Name("Trigger Interval Ticks")
        @Config.Comment("每次触发之间的间隔游戏刻数。")
        @Config.RangeInt(min = 1, max = 72000)
        public int triggerIntervalTicks = 20;

        @Config.Name("Soul Cost Per Trigger")
        @Config.Comment("每次触发消耗当前灵魂能量的百分比（0~1 表示 0%~100%）。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double soulCostPercentPerTrigger = 0.01;

        @Config.Name("Body Cost Per Trigger")
        @Config.Comment("每次触发消耗当前肉体能量的百分比（0~1 表示 0%~100%）。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double bodyCostPercentPerTrigger = 0.03;

        @Config.Name("Chakra Gain Per Trigger")
        @Config.Comment("每次触发恢复查克拉上限的百分比（0~1 表示 0%~100%）。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double chakraGainPercentPerTrigger = 0.05;

        @Config.Name("Insufficient Policy")
        @Config.Comment("能量不足时的处理策略：SKIP（跳过本次触发）、SCALE（按实际可消耗比例缩放回复）。")
        public InsufficientPolicy insufficientPolicy = InsufficientPolicy.SKIP;

        public enum InsufficientPolicy {
            SKIP,
            SCALE
        }
    }

    @Config.Name("Sleep Recovery Settings")
    @Config.Comment("玩家睡觉醒来时一次性恢复灵魂能量和肉体能量的配置。")
    public static final SleepRecoveryConfig sleep = new SleepRecoveryConfig();

    public static class SleepRecoveryConfig {
        @Config.Name("Soul Recovery Percent")
        @Config.Comment("睡觉醒来时恢复的灵魂能量百分比（基于上限）。0 表示不恢复。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double soulRecoveryPercent = 0.30;

        @Config.Name("Body Recovery Percent")
        @Config.Comment("睡觉醒来时恢复的肉体能量百分比（基于上限）。0 表示不恢复。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double bodyRecoveryPercent = 0.10;
        @Config.Name("Soul Recovery Percent Per Tick")
        @Config.Comment("睡觉从夜晚持续到白天后，每个有效睡眠游戏刻恢复的灵魂能量百分比（基于上限）。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double soulRecoveryPercentPerTick = 0.003;

        @Config.Name("Body Recovery Percent Per Tick")
        @Config.Comment("睡觉从夜晚持续到白天后，每个有效睡眠游戏刻恢复的肉体能量百分比（基于上限）。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double bodyRecoveryPercentPerTick = 0.001;

        @Config.Name("Max Recovery Ticks")
        @Config.Comment("单次睡觉最多结算多少个有效睡眠游戏刻，防止多人服务器等待其他玩家时累计过高。")
        @Config.RangeInt(min = 0, max = 24000)
        public int maxRecoveryTicks = 100;

    }

    @Config.Name("Creative Mode Body Recovery Settings")
    @Config.Comment("创造模式下肉体能量独立恢复的配置。不消耗饱食度。")
    public static final CreativeBodyRecoveryConfig creativeBody = new CreativeBodyRecoveryConfig();

    public static class CreativeBodyRecoveryConfig {
        @Config.Name("Creative Body Recovery Per Tick")
        @Config.Comment("创造模式下每游戏刻恢复的肉体能量量。")
        @Config.RangeDouble(min = 0.0, max = 10000.0)
        public double perTick = 0.5;
    }


    @Config.Name("Chakra Fruit Settings")
    @Config.Comment("查克拉果实食用后增加灵魂能量和肉体能量的配置。")
    public static final ChakraFruitConfig chakraFruit = new ChakraFruitConfig();

    public static class ChakraFruitConfig {
        @Config.Name("Soul Amount")
        @Config.Comment("食用查克拉果实后增加的灵魂能量（当前值和上限同时增加）。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double soulAmount = 500.0;

        @Config.Name("Body Amount")
        @Config.Comment("食用查克拉果实后增加的肉体能量（当前值和上限同时增加）。")
        @Config.RangeDouble(min = 0.0, max = 100000.0)
        public double bodyAmount = 500.0;
    }
    @Config.Name("XP Conversion Settings")
    @Config.Comment("根据灵魂能量当前值和肉体能量当前值中的较小值的增长贡献/派生忍者经验。派生路径不消耗灵魂或肉体能量。")
    public static final XpConversionConfig xpConversion = new XpConversionConfig();

    public static class XpConversionConfig {
        /** @deprecated Use {@link #ninjaXpContributionRate} instead. Kept for config file compatibility. */
        @Deprecated
        @Config.Name("Ninja XP Conversion Rate")
        @Config.Comment("旧字段，保留用于兼容旧配置文件。请使用 Ninja XP Contribution Rate。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double ninjaXpConversionRate = 0.1;

        @Config.Name("Ninja XP Contribution Rate")
        @Config.Comment("每个检测间隔内，根据灵魂能量和肉体能量当前值中较小值的增长派生忍者经验的比例。派生路径不消耗灵魂或肉体能量。")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double ninjaXpContributionRate = 0.1;

        /** @deprecated Use {@link #ninjaXpContributionIntervalTicks} instead. Kept for config file compatibility. */
        @Deprecated
        @Config.Name("XP Conversion Interval")
        @Config.Comment("旧字段，保留用于兼容旧配置文件。请使用 XP Contribution Interval。")
        @Config.RangeInt(min = 1, max = 1200)
        public int ninjaXpConversionIntervalTicks = 20;

        @Config.Name("XP Contribution Interval")
        @Config.Comment("每次忍者经验贡献检测之间的游戏刻间隔。")
        @Config.RangeInt(min = 1, max = 1200)
        public int ninjaXpContributionIntervalTicks = 20;

        /**
         * @return The effective contribution rate, preferring the new field.
         * If the new field is at default (0.1) and the old deprecated field was customized,
         * the old field's value is used instead.
         */
        public double getEffectiveContributionRate() {
            if (this.ninjaXpContributionRate != 0.1D)
                return this.ninjaXpContributionRate;
            if (this.ninjaXpConversionRate != 0.1D) {
                LOGGER.info("Migrating old config 'Ninja XP Conversion Rate'={} to new 'Ninja XP Contribution Rate'", this.ninjaXpConversionRate);
                this.ninjaXpContributionRate = this.ninjaXpConversionRate;
                return this.ninjaXpContributionRate;
            }
            return 0.1D;
        }

        /**
         * @return The effective contribution interval in ticks, preferring the new field.
         * Falls back to old deprecated field if the new one is at default.
         */
        public int getEffectiveContributionInterval() {
            if (this.ninjaXpContributionIntervalTicks != 20)
                return this.ninjaXpContributionIntervalTicks;
            if (this.ninjaXpConversionIntervalTicks != 20) {
                LOGGER.info("Migrating old config 'XP Conversion Interval'={} to new 'XP Contribution Interval'", this.ninjaXpConversionIntervalTicks);
                this.ninjaXpContributionIntervalTicks = this.ninjaXpConversionIntervalTicks;
                return this.ninjaXpContributionIntervalTicks;
            }
            return 20;
        }
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
