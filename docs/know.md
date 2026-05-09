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


---

## 9项新Todo入口点分析（2026-04-29）

### Todo 1 — HUD条上移（查克拉被吞）
- **文件**: `event/onOverlayEvent.java`
- **方法**: `renderEnergyHUD()`, `getHudTop()`, `getHudLeft()`, `drawEnergyBar()`
- **说明**: 三条HUD堆叠（灵魂/肉体/查克拉），总高度 `HUD_ROW_HEIGHT*2 + HUD_BAR_HEIGHT=32px`，锚点在快捷栏上方。若原版其他HUD元素向下挤占，查克拉条可能被覆盖。
- **配置**: `Configs.hudYOffset`（默认0）

### Todo 2 — 计算左边距到快捷栏距离，不足时隐藏文本
- **文件**: `event/onOverlayEvent.java`
- **方法**: `renderEnergyHUD()`, `drawEnergyBar()`
- **说明**: `getHudLeft()` 固定 `HUD_TOTAL_WIDTH=182`。需改为动态计算 `hotbarLeft - x`，若 `＜HUD_TOTAL_WIDTH` 则跳过文字（仅保留条）
- **风险**: 低分辨率可能完全无空间，需保底只渲染条。

### Todo 3 — 查克拉自然恢复检查
- **文件**: narutomod `Chakra.java` → `PathwayPlayer.onUpdate()`
- **说明**: 原版逻辑被动回复 `CHAKRA_REGEN_RATE + 0.001*饱和度`（静止>80 ticks）。当前 `mixinPathway.java` 仅叠加 soul 加成，未禁用原版自然恢复→双重恢复。
- **风险**: 需 Mixin 禁用原版自然恢复分支。

### Todo 4 — 查克拉<10%时消耗灵魂+肉体恢复查克拉
- **入口**: 新 `handler/ChakraEmergencyHandler.java` 或扩展 `mixinPathway.java`
- **配置**: 需新增 `chakraEmergencyThreshold`(默认0.10)、`soulToChakraRate`、`bodyToChakraRate`
- **风险**: 需间隔控制，防死循环。

### Todo 5 — 灵魂<20%时反胃Buff + 睡觉/静止恢复
- **入口**: 新 `handler/SoulLowHandler.java` 或扩展 `SoulEnergyEventHandler.java`
- **配置**: 需新增 `lowSoulThreshold`(0.20)、`soulRegenSleeping`、`soulRegenStanding`
- **风险**: 静止判定需排除骑乘/坠落。

### Todo 6 — 肉体<20%时缓慢+疲劳+虚弱 + 饱食度恢复
- **入口**: 扩展 `handler/BodyAttributeHandler.java`（已有 per-tick）
- **配置**: 需新增 `lowBodyThreshold`(0.20)、`hungerToBodyRate`、`hungerConsumePerTick`
- **风险**: 饱食度过低（＜6）时不应无限扣减。

### Todo 7 — 兵粮丸改为恢复肉体能量
- **入口**: `MixinProcedureWhiteZetsuFleshFoodEaten.java`（当前仅改概率→0）
- **说明**: 需要反编译 narutomod 0.3.1-beta 的 `ProcedureWhiteZetsuFleshFoodEaten.executeProcedure()` 确定原版 chakra 注入点，改为增加 body.current
- **阻塞**: 本地无 narutomod jar 反编译/源码，需 gradle build 后从 `build/tmp/recompileMc` 或 `.gradle/caches` 获取。

### Todo 8 — 因陀罗/阿修罗血脉Buff ×2初始值与恢复
- **入口**: `handler/BloodlineAbilityHandler.java` + `SoulEnergyData`/`BodyEnergyData` 初始化
- **配置**: 需新增 `indraSoulMultiplier`(2.0)、`indraSoulRegenMultiplier`(2.0)、`asuraBodyMultiplier`(2.0)、`asuraBodyRegenMultiplier`(2.0)
- **风险**: 觉醒后通过 `EntityJoinWorldEvent` 对已有血统玩家补偿。

### Todo 9 — 原版涉及以上逻辑全部修改
- **范围**: 以上8个todo的所有 narutomod 原版代码路径
- **已知已覆盖**: mixinPathway(查克拉恢复/写轮眼进化)、MixinProcedureWhiteZetsuFleshFoodEaten、MixinPlayerTracker(经验)、MixinOverlayChakraDisplay
- **遗漏风险**: narutomod 食物类、查克拉耗尽事件、战斗经验绕过路径
- **建议**: 反编译 jar 搜索 chakra/regen/consume/exp，确保全覆盖。

## Executor 推荐切片（保持9个todo粒度）

| Plan ID | 对应todo | 主要文件 | 依赖 |
|---------|----------|----------|------|
| 11.5 | Todo 1+2 | `onOverlayEvent.java`, `Configs.java` | 无 |
| 11.6 | Todo 3 | `mixinPathway.java` + `Chakra.java` 反编译 | 无 |
| 11.7 | Todo 4 | 新 `ChakraEmergencyHandler`, `Configs` | 2.1+2.2(已完) |
| 11.8 | Todo 5 | 新 `SoulLowHandler`, `Configs` | 2.1(已完) |
| 11.9 | Todo 6 | 扩展 `BodyAttributeHandler`, `Configs` | 2.2(已完) |
| 11.10 | Todo 7 | 扩展 `MixinProcedureWhiteZetsuFleshFoodEaten` | 反编译narutomod |
| 11.11 | Todo 8 | `BloodlineAbilityHandler`, `Soul/BodyEnergyData` | 1.1+2.1+2.2(已完) |
| 11.12 | Todo 9 | 全覆盖审查 | 11.5~11.11完成后 |

---

## 背包信息展示 Scout 发现（2026-04-29）

### 本轮基线
- 当前分支：`.git/HEAD` 为 `ref: refs/heads/feature/energy-system`。
- `docs/project.md` / `docs/diff.md` 显示新增 todo 尚未实现：需要在玩家背包页面顶部或底部插入血脉、身体能量、灵魂能量、查克拉能量及实时回复速度，并适配原版缩放。
- `/root/.codex/RTK.md` 位于项目外，IDEA MCP 拒绝读取：`outside of the project directory`。

### 背包 GUI 插入点
- `src/main/java/com/qdd/narutofix/mixin/MixinGuiInventory.java`
  - 已 Mixin 到 `net.minecraft.client.gui.inventory.GuiInventory`。
  - 当前注入 `drawGuiContainerBackgroundLayer` 的 `TAIL`，用于绘制虚拟瞳术槽背景。
  - 推荐复用该类增加背包信息绘制；展示文字更适合注入 `drawGuiContainerForegroundLayer` 的 `TAIL`，坐标可相对 `guiLeft/guiTop` 和原版 `xSize/ySize` 计算。
- `build/rfg/minecraft-src/java/net/minecraft/client/gui/inventory/GuiInventory.java`
  - 原版背包 `xSize=176,ySize=166` 由 `GuiContainer` 管理；`drawGuiContainerForegroundLayer()` 只在 `(97,8)` 绘制 `container.crafting`。
  - `initGui()` 会因配方书和窄屏更新 `guiLeft`，因此新增信息必须每帧使用当前 `guiLeft/guiTop`，不要固定屏幕绝对坐标。

### 现有 HUD / 缩放处理参考
- `src/main/java/com/qdd/narutofix/event/onOverlayEvent.java`
  - `renderEnergyHUD()` 通过 `new ScaledResolution(mc)` 取缩放后的宽高，按原版快捷栏位置计算 HUD。
  - 使用 `Minecraft.fontRenderer.getStringWidth()` 动态计算标签列宽，并在空间不足时隐藏文字。
  - 背包内绘制已经处于 GUI 缩放坐标系，通常应直接使用 `guiLeft/guiTop/width/height/fontRenderer`，仅在做屏幕外侧附着布局时参考 `ScaledResolution`。

### 能量 / 查克拉数据来源
- 灵魂能量：`src/main/java/com/qdd/narutofix/cap/soul/ISoulEnergyData.java`，通过 `SoulEnergyDataProvider.get(player)` 取得；当前/最大值 API 为 `getCurrent()` / `getMax()`。
- 身体能量：`src/main/java/com/qdd/narutofix/cap/body/IBodyEnergyData.java`，通过 `BodyEnergyDataProvider.get(player)` 取得；当前/最大值 API 为 `getCurrent()` / `getMax()`。
- 查克拉：narutomod `Chakra.pathway(player)`，现有代码使用 `getAmount()` / `getMax()`，并通过 `PacketSyncChakra` 同步到客户端。
- 现有三条能量 HUD 已在 `onOverlayEvent.renderEnergyHUD()` 同时读取以上三个来源，可直接作为背包展示的数据读取参考。

### 回复速度数据来源
- 当前没有独立的“实时回复速度”字段或 capability API。
- 已发现的实际回复/变化逻辑在 `src/main/java/com/qdd/narutofix/handler/EnergyStateHandler.java`：
  - 低灵魂：低于阈值时，睡觉用 `Configs.soul.soulSleepRecoveryPerTick`，静止达到要求后用 `Configs.soul.soulIdleRecoveryPerTick`，因陀罗乘 `Configs.soul.indraSoulRecoveryMultiplier`。
  - 低身体：低于阈值时，消耗饱食/饱和恢复，速度受 `Configs.body.bodyFoodCostPerTick`、`Configs.body.foodToBodyRate`、阿修罗倍率和当前食物状态限制。
  - 低查克拉：低于阈值时，同时消耗灵魂/身体恢复，速度受 `Configs.chakraEmergency.chakraEmergencyEnergyCostPerTick`、`soulToChakraRate + bodyToChakraRate`、当前灵魂/身体能量和缺口限制。
- 若背包页面要显示真正“实时回复速度”，需要复用或抽出这些计算，避免 GUI 端重复一套易漂移公式。

### 血脉数据来源
- `src/main/java/com/qdd/narutofix/cap/awakening/IPlayerAwakeningData.java`
  - `hasIndra()`、`hasAsura()`、`hasBothBloodlines()`、`hasAnyBloodline()`。
- `src/main/java/com/qdd/narutofix/cap/awakening/PlayerAwakeningDataProvider.java`
  - 通过 `PlayerAwakeningDataProvider.get(player)` 读取。
- `src/main/java/com/qdd/narutofix/awakening/Bloodline.java`
  - 当前枚举值：`INDRA`、`ASURA`。

### 上线风险
- 需要确认背包文字不会覆盖原版配方书按钮、合成标题、玩家模型、物品槽、虚拟瞳术槽；窄屏和配方书打开时尤其要测。
- 如果采用 `drawGuiContainerBackgroundLayer` 绘制文字，可能被物品槽/后续前景层遮挡；文字类信息更建议前景层绘制。
- “实时回复速度”若只显示配置常量，会与食物不足、能量不足、阈值未触发、血脉倍率等实际状态不一致；上线前需实机确认数值和状态切换。
- 当前文档状态仍是可构建候选但缺少最新背包展示功能的实现、编译验证和 `runClient` 实机冒烟，验证通过后才可合回 `2836`。
