# naruto_mod 0.3.1-beta 研究成果

## 项目基本信息

- **Mod ID**: `narutomod`
- **版本**: 0.3.1-beta
- **包路径**: `net.narutomod`
- **Forge版本**: 1.12.2-14.23.5.2855
- **映射**: snapshot_20171003-1.12
- **类型**: MCreator生成项目，使用自发现`@Tag`注解的ModElement系统
- **JDK**: Java 8
- **本地克隆路径**: `/root/workspace/naruto_mod_0.3.1`

## 整体包结构

```
/root/workspace/naruto_mod_0.3.1/src/main/java/net/narutomod/
├── NarutomodMod.java              # 主类, @Mod, PACKET_HANDLER
├── ElementsNarutomodMod.java      # ModElement框架, 自发现机制
├── ModConfig.java                 # @Config配置
├── NarutomodModVariables.java     # 常量、WorldSavedData
├── Chakra.java                    # 查克拉系统（核心）
├── PlayerTracker.java             # 忍者经验/战斗力系统（核心）
├── PlayerRender.java              # 玩家渲染器替换
├── PlayerInput.java               # 输入处理
├── Particles.java                 # 粒子系统
├── EntityTracker.java             # 实体追踪
├── SaveData.java                  # 存档系统
├── SpawnTailedBeasts.java         # 尾兽生成
├── IProxyNarutomodMod.java / ClientProxyNarutomodMod.java / ServerProxyNarutomodMod.java
├── block/                         # 自定义方块
├── command/                       # 命令
│   ├── CommandAddNinjaXp.java     # /addninjaxp - 添加忍者经验
│   ├── CommandAddXP2Jutsu.java    # /addxp2jutsu - 添加忍术经验
│   └── CommandLocateEntity.java   # /locateentity - 定位实体
├── creativetab/                   # 创造模式标签
├── entity/                        # 140+实体
├── event/                         # 事件工具
├── gui/                           # GUI (忍术卷轴GUI)
│   └── overlay/
│       ├── OverlayChakraDisplay.java  # 查克拉HUD
│       ├── OverlayByakuganView.java   # 白眼视图
│       └── OverlayDebugStats.java     # 调试统计(已注释)
├── item/
│   ├── ItemDojutsu.java           # 瞳术基类（抽象）
│   ├── ItemSharingan.java         # 写轮眼（基类含勾玉→万花筒进化触发）
│   ├── ItemMangekyoSharingan.java           # 万花筒写轮眼(佐助)
│   ├── ItemMangekyoSharinganObito.java       # 万花筒写轮眼(带土/神威)
│   ├── ItemMangekyoSharinganEternal.java     # 永恒万花筒写轮眼
│   ├── ItemByakugan.java          # 白眼
│   ├── ItemRinnegan.java          # 轮回眼
│   ├── ItemByakuRinnesharingan.java  # 轮墓写轮眼
│   ├── ItemJutsu.java             # 忍术系统(核心)
│   └── ...
├── keybind/
├── potion/
├── procedure/
│   ├── ProcedureUtils.java        # 工具类（getCDModifier等）
│   ├── ProcedureSync.java         # NBT数据同步
│   ├── ProcedureOnPlayerPostTick.java  # 玩家tick主逻辑
│   ├── ProcedureOnLivingUpdate.java    # 实体更新逻辑
│   ├── ProcedureSharinganHelmetTickEvent.java  # 写轮眼tick(含进化)
│   ├── ProcedureOnPlayerDeath.java     # 死亡处理
│   ├── ProcedureBasicNinjaSkills.java  # 基础忍者技能
│   ├── ProcedureAddNinjaXpCommandExecuted.java  # /addninjaxp执行
│   └── ...
└── world/
```

## 能力系统

**没有使用Forge的ICapability系统。** 所有玩家数据通过`EntityPlayer.getEntityData()`(持久化NBT)存储，通过自定义网络包同步。

### 查克拉系统 (`Chakra.java`)

- **本地路径**: `/root/workspace/naruto_mod_0.3.1/src/main/java/net/narutomod/Chakra.java`
- `Chakra.Pathway<T>` - 基础查克拉通路类
  - `amount` - 当前查克拉量
  - `max` - 最大查克拉量
  - `consume(double)` / `consume(float percent)` - 消耗查克拉
  - `onUpdate()` - 每tick更新（超限伤害、低查克拉负面效果）
- `Chakra.PathwayPlayer extends Pathway<EntityPlayer>` - 玩家查克拉通路
  - `max = battleXp * 0.5` （忍者经验决定最大查克拉）
  - 存储在玩家NBT: `"ChakraPathwaySystem"` (double)
  - 被动回复（静止>80ticks）：`ModConfig.CHAKRA_REGEN_RATE + 0.001*饱和度`
  - 同步: 通过`ServerMessage`(服务端→客户端)和`ConsumeMessage`(客户端→服务端)

### 忍者经验系统 (`PlayerTracker.java`)

- **本地路径**: `/root/workspace/naruto_mod_0.3.1/src/main/java/net/narutomod/PlayerTracker.java`
- NBT key: `"battle_experience"` (double), 上限 100,000
- `isNinja()`: `battleXp > 0`
- `getNinjaLevel()`: `MathHelper.sqrt(battleXp)`

**经验获取:**
1. **攻击命中** (`LivingDamageEvent`): 基于目标血量/抗性/护甲, 乘以`NINJAXP_MULTIPLIER`(默认0.5), 上限60
2. **被攻击** (`LivingDamageEvent`): `damage / sqrt(sqrt(battleXp))`, CD 20 ticks

**经验影响:**
- **最大血量**: `maxHealth += battleXp * 0.005` (AttributeModifier UUID: 84d6711b-c26d-4dfa-b0c5-1ff54395f4de)
- **查克拉上限**: `maxChakra = battleXp * 0.5`
- **忍术冷却**: `getCDModifier(level) = 1/(0.5 + 0.02*sqrt(battleXp))`

### 灵魂/肉体能量
**当前mod没有灵魂能量(Soul Energy)或肉体能量(Body Energy)系统。** 只有查克拉和忍者经验两套系统。

## HUD系统 (`OverlayChakraDisplay.java`)

- **本地路径**: `/root/workspace/naruto_mod_0.3.1/src/main/java/net/narutomod/gui/overlay/OverlayChakraDisplay.java`
- 渲染时机: `RenderGameOverlayEvent.ElementType.HELMET`
- 位置: 屏幕左侧, `left = sWidth/2 - 206, width = 80`
- 样式: 黄色竖条分格显示, 每格4像素高, 底部一格特殊颜色(红/青闪烁警告)
- 显示文字: `"当前值/最大值"`
- 仙人模式火焰覆盖层: `showSageBar` + `flames_green.png`
- 前置条件: `PlayerTracker.isNinja(entity) && Chakra.isInitialized(entity)`
- 网络包: `WarningMessage`(不足查克拉警告), `ShowFlamesMessage`(仙人模式)

## 写轮眼进化系统

### 写轮眼 → 万花筒写轮眼

- **本地路径**: `/root/workspace/naruto_mod_0.3.1/src/main/java/net/narutomod/procedure/ProcedureSharinganHelmetTickEvent.java`

**触发条件（每tick检查）:**
- 穿着`ItemSharingan.helmet` (普通写轮眼)
- `battleXp >= 1000`
- 附近40格内6000ticks(5分钟)内有玩家死亡记录
- 写轮眼未失明(`sharingan_blinded`标记不为true)

**进化结果:**
- 50% -> `ItemMangekyoSharingan.helmet` (佐助万花筒, 天照+须佐)
- 50% -> `ItemMangekyoSharinganObito.helmet` (带土万花筒, 神威+须佐)
- 清空旧写轮眼, 授予`narutomod:mangekyosharinganopened`进度

**写轮眼失明机制:**
- Durability <= 3时标记 `"sharingan_blinded" = true`
- 失明后: 给予`BLINDNESS` 1200ticks, Susanoo取消

### 万花筒 → 永恒万花筒
**当前mod中没有自动进化逻辑。** 永恒万花筒是独立`ItemMangekyoSharinganEternal.helmet`物品:
- 不消耗耐久度(`isDamageable = false`)
- 提供飞行能力(only Kamui维度)
- `onUpdate()`自动销毁同主人的低等级写轮眼
- 有EntityEquipmentSlot.HEAD盔甲模型

### 写轮眼物品层级:
```
Item (盔甲, 头盔位EntityEquipmentSlot.HEAD)
└── ItemDojutsu.Base (抽象基类, 有所有者系统/盔甲模型)
    └── ItemSharingan.Base (写轮眼基类, 颜色/失明/闪避/目标锁定)
        ├── [narutomod:sharinganhelmet] 普通写轮眼 (3勾玉)
        ├── [narutomod:mangekyosharinganhelmet] 佐助万花筒
        ├── [narutomod:mangekyosharinganobitohelmet] 带土万花筒
        ├── [narutomod:mangekyosharinganeternalhelmet] 永恒万花筒
```

## 配置文件 (`ModConfig.java`)

- **本地路径**: `/root/workspace/naruto_mod_0.3.1/src/main/java/net/narutomod/ModConfig.java`
- Forge `@Config(modid = "narutomod")` 注解, 自动生成`.cfg`文件

关键配置:
| 字段 | 默认值 | 说明 |
|------|--------|------|
| `CHAKRA_REGEN_RATE` | 0.006F | 查克拉静止回复率 |
| `NINJAXP_MULTIPLIER` | 0.5D | 忍者经验获取倍率 |
| `NARUTO_RUN` | true | 火影跑动画 |
| `REMOVE_CHEAT_DOJUTSUS` | false | 移除作弊瞳术 |
| `AMATERASU_BLOCK_DURATION` | 100 | 天照火焰持续tick |
| `AGGRESSIVE_BOSSES` | false | BOSS主动攻击 |
| `ITACHI_REAL_CHANCE` | 10 | 鼬真实分身概率 |

## 指令系统

**本地路径**: `/root/workspace/naruto_mod_0.3.1/src/main/java/net/narutomod/command/`

1. **`/addninjaxp <player> <amount>`** - OP4, 添加忍者经验
2. **`/addxp2jutsu`** - OP4, 添加忍术经验
3. **`/locateentity`** - 定位实体

## 网络数据包系统

- 通道: `SimpleNetworkWrapper` @ `"narutomod:a"`
- 所有消息通过`ElementsNarutomodMod.addNetworkMessage()`注册, 自增discriminator
- 数据同步: `ProcedureSync.EntityNBTTag.setAndSync()`, `sendToSelf()`, `sendToServer()`, `sendToTracking()`

## 忍术系统 (`ItemJutsu.java`)

- **本地路径**: `/root/workspace/naruto_mod_0.3.1/src/main/java/net/narutomod/item/ItemJutsu.java`
- `ItemJutsu.Base` 是所有忍术物品基类
- `JutsuEnum` 含索引/名称/等级(S/A/B/C/D)/所需XP/查克拉消耗/回调
- 蓄力加速: `power = basePower + (maxUseTime - timeLeft) / (powerupDelay * getModifier())`
- 冷却修正: `getModifier = chakraModifier * xpModifier`

## 实用函数 (`ProcedureUtils.java`)

- **本地路径**: `/root/workspace/naruto_mod_0.3.1/src/main/java/net/narutomod/procedure/ProcedureUtils.java`
- `getCDModifier(double modifier)` - `1.0d / (0.5d + 0.02d * modifier)` (line 1245)
- `getCooldownModifier(EntityPlayer)` - 调用上面用`getNinjaLevel()` (line 1249)
- `modifiedCooldown(double, EntityPlayer)` - 乘以冷却修正 (line 1253)

## 玩家死亡处理 (`ProcedureOnPlayerDeath.java`)

- **本地路径**: `/root/workspace/naruto_mod_0.3.1/src/main/java/net/narutomod/procedure/ProcedureOnPlayerDeath.java`
- 处理Rinnegan/KingOfHell复活逻辑
- 检查keepInventory gamerule
- 死亡时清除特定物品

## 关键事件处理器

1. **`PlayerTracker.PlayerHook`** (PlayerTracker.java 内):
   - `onTick`: 每tick血量加成(battleXp*0.005)
   - `onDeath`: 记录死亡到`Deaths`列表
   - `onDamaged`: 战斗经验获得逻辑
   - `onLogin/onRespawn/onClone`: 数据持久化

2. **`Chakra.PathwayPlayer.PlayerHook`** (Chakra.java 内):
   - `onDeath`: 死亡时保留少量查克拉或清空
   - `onTick`: 查克拉回复/溢出处理
   - `onChangeDimension/onLogin/onRespawn`: 同步

3. **`ProcedureOnPlayerPostTick`**:
   - 首次成为忍者(exp≥10且battleXp>0)发放忍术和写轮眼
   - KG随机觉醒(0.1%)
   - 基础忍者技能

4. **`ItemSharingan.PlayerHook`** (ItemSharingan.java 内):
   - `onAttacked`: 60%几率闪避, 目标锁定
   - `onPlayerTick`: 跟踪锁定目标

## Mixin情况
**当前mod没有任何Mixin文件。** 所有修改通过Forge Event Bus和反射(reflection)完成。
