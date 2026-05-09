# narutofix 开发计划 (plans.md)

本计划面向产品上线（1.12.2 Forge mod，narutofix 依赖 narutomod 0.3.1-beta）。每个 plan item 是一个 Executor 子代理可以独立完成的最小派发单元；按依赖顺序执行，前置项目完成后再进入下一项。所有改动遵守 AGENTS.md 强制约束（idea mcp 执行读取/编译/测试，禁止删除 gradle 运行时 mod 依赖，新功能先在新分支开发，验证通过再合回 2836 主分支）。

---

## 0. 准备阶段

### Plan 0.1 — 拉取 naruto_mod 0.3.1-beta 源码作为参考依赖
- 在仓库同级 `../naruto_mod` 目录使用 git clone `https://github.com/AHZNB/naruto_mod`，checkout 分支 `0.3.1-beta`。
- 不改动 `dependencies.gradle`（运行时仍引用 `curse.maven:ahznbs-naruto-mod-831157:5967126`），仅作为 Mixin/反编译/方法名/字段名查阅来源。
- 在 `docs/project.md` 记录 commit hash 和参考路径。
- 网络异常时使用 7890 端口代理。
- **新分支**：从 `2836` 切出 `feature/energy-system` 作为后续所有 plan 的开发分支。

---

## 1. 配置层

### Plan 1.1 — 在 Configs.java 增加灵魂能量与肉体能量配置类
修改文件：`src/main/java/com/qdd/narutofix/Configs.java`

新增子类（@Config.Comment + @Config.RangeDouble/Int）：
- `SoulEnergyConfig`：
  - `double soulGainOnKill = 1.0`（击杀时当前+上限增量）
  - `double soulMaxGainOnKill = 1.0`
  - `double soulMaxGainOnDeath = 5.0`（被击杀仅提升上限）
  - `double soulLossPercentOnDeath = 0.10`（被击杀损失当前 10%）
  - `double soulInitialMax = 100.0`
  - `double chakraGrowthMultiplierPerSoul = 0.001`（每点灵魂能量提升查克拉恢复速度）
  - `double jutsuXpMultiplierPerSoul = 0.001`
  - `double jutsuChargeMultiplierPerSoul = 0.001`
  - `double sharinganTomoeUpgradeSoul = 500.0`
  - `double mangekyoUpgradeSoul = 2000.0`
  - `double eternalMangekyoUpgradeSoul = 5000.0`
- `BodyEnergyConfig`：
  - `double bodyMaxGainOnHit = 0.5`
  - `double bodyMaxGainOnHurt = 0.5`
  - `double bodyMaxGainOnMine = 0.2`
  - `double bodyCostOnHit = 1.0`
  - `double bodyCostOnHurt = 1.0`
  - `double bodyCostOnMine = 0.5`
  - `double bodyInitialMax = 100.0`
  - `double hpMultiplierPerBody = 0.01`
  - `double armorPerBody = 0.01`
  - `double hpRegenPerBody = 0.001`
  - `double moveSpeedPerBody = 0.0005`
  - `double attackDamagePerBody = 0.01`
  - `double attackSpeedPerBody = 0.005`
- `XpConversionConfig`：
  - `double ninjaXpConversionRate = 0.1`（min(soul,body) 每秒按比例转化）
  - `int ninjaXpConversionIntervalTicks = 20`

要求：保持现有 `upgrade` 字段（兼容性），但 Sharingan 进化逻辑后续改为读 SoulEnergyConfig 中的字段。

---

## 2. Capability 系统

### Plan 2.1 — 创建灵魂能量 Capability
参考 `cap/awakening/` 模式，新建包 `cap/soul/`：
- `ISoulEnergyData.java`：接口含 `getCurrent/setCurrent/addCurrent`、`getMax/setMax/addMax`、`copyFrom(IPlayerAwakeningData... 类型相对应)`。
- `SoulEnergyData.java`：实现类，字段 `current`, `max`，初始 `max = Configs.soul.soulInitialMax`，`current = 0`。
- `SoulEnergyDataProvider.java`：实现 `ICapabilitySerializable<NBTTagCompound>`。
- `SoulEnergyDataStorage.java`：NBT read/write `current`, `max`。
- `SoulEnergyCapabilityHandler.java`：
  - `@SubscribeEvent AttachCapabilitiesEvent<Entity>`：仅 `EntityPlayer` 附加。
  - `@SubscribeEvent PlayerEvent.Clone`：跨死亡复制（保留 max，按配置 lossPercent 削减 current）。
  - `CapabilityManager.INSTANCE.register(...)` 通过静态 `register()` 由 `CommonProxy.preInit` 调用。
- 在 `CommonProxy.preInit` 中调用 `SoulEnergyCapabilityHandler.register()`，并 `MinecraftForge.EVENT_BUS.register(new SoulEnergyCapabilityHandler())`。

### Plan 2.2 — 创建肉体能量 Capability
同 Plan 2.1，新建 `cap/body/`：
- `IBodyEnergyData.java` / `BodyEnergyData.java` / `BodyEnergyDataProvider.java` / `BodyEnergyDataStorage.java` / `BodyEnergyCapabilityHandler.java`。
- 字段：`current`, `max`；初始 `max = Configs.body.bodyInitialMax`，`current = 0`。
- Clone 事件：保留 max 与 current（不衰减）。
- 同样在 `CommonProxy.preInit` 注册。

---

## 3. 网络同步

### Plan 3.1 — 灵魂/肉体能量 数据包同步到客户端
参考 `network/` 现有 SimpleNetworkWrapper：
- 新建 `network/PacketSyncSoulEnergy.java`、`network/PacketSyncBodyEnergy.java`：MessageHandler 在客户端写入 `EntityPlayerSP` 的 capability。
- 在 `NarutoFix.NETWORK.registerMessage(...)` 注册（NarutoFix.java 或专门的 NetworkRegistry）。
- 服务端触发同步的时机：`PlayerLoggedInEvent`、`PlayerChangedDimensionEvent`、`PlayerRespawnEvent`、capability 数值变化时（提供工具方法 `SoulEnergyHelper.sync(EntityPlayerMP)`、`BodyEnergyHelper.sync(...)`）。

---

## 4. 能量获取与消耗事件

### Plan 4.1 — 灵魂能量事件处理
新建 `event/SoulEnergyEventHandler.java`，订阅：
- `LivingDeathEvent`：
  - 若 `event.getSource().getTrueSource() instanceof EntityPlayer` 且死亡实体非该玩家：current += `soulGainOnKill`，max += `soulMaxGainOnKill`，并同步。
  - 若死亡实体是 `EntityPlayer`：max += `soulMaxGainOnDeath`，current = current * (1 - `soulLossPercentOnDeath`)。
- 在 `EventLoader` 注册。

### Plan 4.2 — 肉体能量事件处理
新建 `event/BodyEnergyEventHandler.java`：
- `LivingHurtEvent`（攻击侧 EntityPlayer 作为 source）：max += `bodyMaxGainOnHit`，current -= `bodyCostOnHit`（不低于 0）。
- `LivingHurtEvent`（受击侧为 EntityPlayer）：max += `bodyMaxGainOnHurt`，current -= `bodyCostOnHurt`。
- `BlockEvent.BreakEvent`：max += `bodyMaxGainOnMine`，current -= `bodyCostOnMine`。
- 同步包发送。
- 在 `EventLoader` 注册。

---

## 5. 能量影响属性

### Plan 5.1 — 肉体能量驱动的属性 Modifier
新建 `handler/BodyAttributeHandler.java`：
- `PlayerTickEvent`（每 N tick）根据 body.current/max 应用/刷新属性 modifier：MAX_HEALTH（multiplier）、ARMOR（addition）、MOVEMENT_SPEED（multiplier）、ATTACK_DAMAGE（multiplier）、ATTACK_SPEED（multiplier）。
- HP 回复速度：通过自定义 tick 给玩家 heal（按 `hpRegenPerBody * body.current`）。
- 使用固定 UUID 常量，避免重复叠加；先 remove 再 apply。
- 与现有 `BloodlineAbilityHandler` 风格保持一致。
- 在 `EventLoader` 注册。

### Plan 5.2 — 灵魂能量驱动的查克拉/忍术效果（Mixin）
通过 Mixin 修改 narutomod 中：
- 查克拉自然恢复速率：定位 narutomod 中 chakra regen 方法（参考 Plan 0.1 拉取的源码），新建 `mixin/MixinChakraRegen.java`，在 `@ModifyVariable`/`@Inject` 处按 `1 + soul.current * chakraGrowthMultiplierPerSoul` 缩放。
- 忍术 XP 获取：定位 jutsu xp 增加点（参考源码 `EntityNinjaMob$Base.addJutsuXp` 或类似），Mixin 缩放。
- 忍术蓄力速度：定位蓄力 tick 累加处，Mixin 缩放。
- 在 `mixins.narutofix.json` 注册新 Mixin 类。

---

## 6. HUD 改造

### Plan 6.1 — 屏蔽 narutomod 原版查克拉 HUD
- 在 `event/onOverlayEvent.java`（或新文件 `event/HudHandler.java`）订阅 `RenderGameOverlayEvent.Pre`，匹配 narutomod 的查克拉 HUD type/identifier 并 `event.setCanceled(true)`；如 narutomod 使用 `RenderGameOverlayEvent.Post` 自绘则需 Mixin 屏蔽。
- 通过 Plan 0.1 源码定位 narutomod 查克拉渲染类（如 `ClientProxy` 中的 overlay 注册），必要时新增 `mixin/MixinNarutomodChakraOverlay.java` 取消渲染。

### Plan 6.2 — 新增三个 HUD：灵魂能量、肉体能量、查克拉
修改 `event/onOverlayEvent.java`：
- 在 `RenderGameOverlayEvent.Post`(ELementType.ALL/HOTBAR) 中绘制三条横向条：灵魂（紫）、肉体（红橙）、查克拉（蓝）。
- 数值来源：Soul/Body capability + narutomod 公开的 chakra 接口（PlayerData / IChakra）。
- 文字显示 `current/max`。
- 位置可在 Configs 中配置 X/Y 偏移（增加 `HudConfig` 子类）。

---

## 7. 指令

### Plan 7.1 — 新增灵魂/肉体能量指令
参考 `command/CommandAwakeKekkeiGenkai.java` 模式：
- `command/CommandSoulEnergy.java`：`/soulenergy <player> <get|set|add> <current|max> [value]`。
- `command/CommandBodyEnergy.java`：同上。
- 在 `NarutoFix.serverStarting` 中注册。
- 权限等级 2。

### Plan 7.2 — 移除/废弃原版及 narutomod 的 addninjaxp 指令
- 通过 Mixin 阻止 narutomod 的 `addninjaxp`（或同名）指令注册：定位 narutomod 指令注册类，Mixin `@Inject` 取消 register 调用，或在 `FMLServerStartingEvent` 中从 `CommandHandler` 移除该指令。
- narutofix 内已有的 ninja XP 指令一并删除（若存在）。

---

## 8. 忍者经验系统重写

### Plan 8.1 — 移除原版忍者经验自然增长
- 通过 Mixin 拦截 narutomod 中所有自然增加 ninjaXp 的位置（击杀、使用忍术等触发的 addNinjaXp 调用），在入口 `@Inject(cancellable=true)` 直接 `ci.cancel()`。
- 列表通过 Plan 0.1 源码 grep `ninjaXp|setNinjaXp|addExp` 得出，逐一 Mixin。

### Plan 8.2 — 移除原版忍者经验对 HP/护甲的加成
- Mixin narutomod 中读取 ninjaXp 并增加 maxHealth / armor 的逻辑，使其返回 0 或不应用 modifier。
- 同时移除 narutofix 自身 `BloodlineAbilityHandler` 中基于 ninjaXp 的 HP/护甲逻辑（如果存在），以免重复。

### Plan 8.3 — 新增 min(soul,body) 比例转化为忍者 XP
新建 `handler/NinjaXpConversionHandler.java`：
- `PlayerTickEvent`，每 `ninjaXpConversionIntervalTicks` 触发一次。
- 取 `min(soul.current, body.current)`，按 `ninjaXpConversionRate` 转换为 ninjaXp 增量；扣减相应 soul/body current。
- 通过 narutomod 公开 API（PlayerData 或 capability）调用 `setNinjaXp(current + delta)`。
- 在 `EventLoader` 注册。

---

## 9. 写轮眼进化重做

### Plan 9.1 — 替换三勾玉前的进化判定为灵魂能量阈值
- 定位现有进化逻辑（`PlayerAwakeningCapabilityHandler` 或 `BloodlineAbilityHandler` / `EventLoader` 中调用 `Configs.upgrade` 的位置）。
- 将判定从「查克拉 ≥ Configs.upgrade」改为「soul.current ≥ Configs.soul.sharinganTomoeUpgradeSoul」，进化时扣减对应 soul.current。
- 保留旧字段做向后兼容但停用其判定路径。

### Plan 9.2 — 新增 三勾玉 → 万花筒 进化
- 在 `IPlayerAwakeningData` 增加 `mangekyoUnlocked` 字段（boolean）+ NBT 存储 + provider 序列化。
- Tick 检查：当玩家已是三勾玉写轮眼且 `soul.current ≥ mangekyoUpgradeSoul` 时，扣减 soul，设置 `mangekyoUnlocked=true`，并在 narutomod 中切换玩家眼模型（通过其 API/ capability）。
- 同步包：扩展现有 awakening 同步包或新建一个。

### Plan 9.3 — 新增 万花筒 → 永恒万花筒 进化
- 在 `IPlayerAwakeningData` 增加 `eternalMangekyoUnlocked`。
- 条件：`mangekyoUnlocked && soul.current ≥ eternalMangekyoUpgradeSoul`。
- 同 Plan 9.2 同步与 narutomod 模型切换。

---

## 10. 验证与发布

### Plan 10.1 — 集成验证
- 通过 idea mcp 执行 `gradle build` 与 `runClient`。
- 测试清单：
  1. 击杀生物 → soul current+max 增长，HUD 同步。
  2. 死亡 → soul max 增长、current 损失 10%、body 保留。
  3. 攻击/受击/挖矿 → body max 增长，current 消耗。
  4. 灵魂高 → 查克拉/忍术 XP/蓄力速度变快。
  5. 肉体高 → HP/护甲/速度/攻击力/攻速/HP 回复变化。
  6. min(soul,body) 自动转化为 ninjaXp。
  7. 原版 addninjaxp 指令已无效；ninjaXp 不再自然增长；不再加 HP/armor。
  8. soul 满阈值进化：1→2→3 勾玉 → 万花筒 → 永恒万花筒。
  9. 三个 HUD 显示，narutomod 原查克拉 HUD 已隐藏。
  10. 所有 `/soulenergy` `/bodyenergy` 子指令工作。
- 写入 `docs/diff.md`：todo 与已完成项的差异。

### Plan 10.2 — 合并回主分支并打 tag
- 验证全部通过后，将 `feature/energy-system` 合并到 `2836`。
- 打 tag 例如 `v0.2.0-energy`，更新 `changelog.txt`、`docs/project.md`。
- 与产品上线对齐：确认与 narutomod 0.3.1-beta 运行兼容、HUD/指令/进化全链路冒烟通过，方可视为可上线。

---

## 依赖关系总览

```
0.1
 └── 1.1
       ├── 2.1 ──┐
       └── 2.2 ──┤
                 ├── 3.1
                 ├── 4.1, 4.2
                 ├── 5.1, 5.2
                 ├── 6.1, 6.2
                 ├── 7.1, 7.2
                 ├── 8.1, 8.2, 8.3
                 └── 9.1 → 9.2 → 9.3
                                  └── 10.1 → 10.2
```

所有子计划是 Executor 子代理可承接的最小派发单位（不再向下拆分）。

---

## 11. 增量补充计划（2026-04-28 用户更新 todo）

以下补充项基于 AGENTS.md 中最新 todo，与第 1–10 节并行/后续推进；新工作仍在 `feature/energy-system` 分支或从 `2836` 切出的小分支进行，验证通过后再合回 `2836`。每项保持一个 plan = 一条用户 todo 的粒度，不再向下拆分派发给 subagent。

### Plan 11.1 — 修复 HUD 绝对定位与进度条位置
- 对应 todo：修复 hud 的绝对定位，模仿原版 hud 渲染，仅占用左屏幕与快捷栏中间区域；当前值进度条位置当前偏下，需要校正。
- 范围：`event/onOverlayEvent.java`（或 Plan 6.2 引入的 HudHandler）以及 `Configs` 中 HudConfig 的默认 X/Y 偏移。
- 要求：参考 vanilla `GuiIngameForge` 的 `left_height`/锚点写法，使用 `ScaledResolution` 计算屏幕坐标，三条能量条按从下到上堆叠在快捷栏左侧、上沿不越过快捷栏中线；进度条与文字基线对齐，避免视觉上整体下沉。
- 验证：IDEA MCP `gradle :compileJava`，再 `runClient` 进入存档目视确认（runClient 仍是上线门禁）。

### Plan 11.2 — 移除两种能量的自然流逝
- 对应 todo：身体能量与灵魂能量未设定自然流逝，但当前会自动减少，需要修复。
- 排查路径：`event/SoulEnergyEventHandler`、`event/BodyEnergyEventHandler`、`handler/BodyAttributeHandler`、`handler/NinjaXpConversionHandler`、以及任何 `PlayerTickEvent` 中对 `soul.addCurrent(-x)` / `body.addCurrent(-x)` 的调用；同时检查 narutomod 是否通过其自身 tick 影响到我们的 capability。
- 修复：仅在显式触发事件（击杀/受击/挖矿/转化为 ninjaXp）时扣减 current；删除任何无条件 tick 衰减分支；NinjaXp 转化必须真实需要 min(soul,body) > 0 才扣减，且不得超出当前值。
- 验证：IDEA MCP `gradle build`；`runClient` 静置 2 分钟观察 HUD，两条能量值不应下降。

### Plan 11.3 — 灵魂/肉体能量与忍者经验增长时同步刷新查克拉
- 对应 todo：当前 soul/body/ninjaXp 增长后，narutomod 查克拉上限/当前值未同步刷新。
- 实现：在 SoulEnergy/BodyEnergy/NinjaXp 任一变更点（事件处理器与转化 handler）调用统一工具 `ChakraSyncHelper.refresh(EntityPlayerMP)`，内部根据 narutomod PlayerData/IChakra 接口重新计算 max（如沿用 Plan 5.2 的乘子逻辑）并将 current 按比例保留，再通过 narutomod 自带同步包或我们自定义 PacketSyncSoulEnergy/BodyEnergy 触发客户端刷新。
- 注意：避免与 Plan 5.2 的 Mixin 重复触发；统一收口在 helper。
- 验证：IDEA MCP `gradle build`；`runClient` 内击杀/受击/挖矿后立即读取 `/chakra` 或 HUD，确认数值同步变化。

### Plan 11.4 — 身体/灵魂能量默认初始值 100 且可配置
- 对应 todo：给 body 与 soul 相同默认初始值 100，可配置。
- 修改：`Configs.SoulEnergyConfig.soulInitialMax` 与 `Configs.BodyEnergyConfig.bodyInitialMax` 默认值确认为 `100.0`（已规划，复核当前代码实际默认）；同时初始化 `current = soulInitialMax`、`current = bodyInitialMax`（而非 0），保证玩家首次加入即拥有满值。
- 兼容：对老存档，若读到 `max <= 0` 则回填为配置初值；`current > max` 时夹取。
- 验证：IDEA MCP `gradle build`；`runClient` 新建世界确认 HUD 起始 100/100；修改 config 后重启确认生效。

### 上线对齐
- 11.1–11.4 全部完成且 `runClient` 冒烟通过后，纳入 Plan 10.1 集成验证清单，再走 Plan 10.2 合并 `2836` + 打 tag 流程，方可视为可上线。

---

## 12. 增量补充计划（2026-04-29 玩家背包信息展示）

### 本轮基线确认
- 已用 IDEA MCP 读取 `docs/diff.md`、`docs/project.md`、`docs/know.md`、`.git/HEAD`、`docs/plans.md`、`MixinGuiInventory.java`、`onOverlayEvent.java`、`EnergyStateHandler.java`、`Configs.java`、原版 `GuiInventory.java`、灵魂/肉体/觉醒 capability 接口与中英文 lang 文件。
- 当前分支仍为 `ref: refs/heads/feature/energy-system`，不是主分支 `2836`；本轮新内容继续在功能分支完成，验证通过后才允许合回 `2836`。
- Scout 结论仍成立：背包信息推荐在 `src/main/java/com/qdd/narutofix/mixin/MixinGuiInventory.java` 追加 `drawGuiContainerForegroundLayer` 的 `TAIL` 注入，布局必须基于当前 `guiLeft/guiTop/xSize/ySize/width/height/fontRenderer`，不能写死屏幕绝对坐标。
- 数据源仍成立：灵魂 `SoulEnergyDataProvider.get(player)`，肉体 `BodyEnergyDataProvider.get(player)`，查克拉 `Chakra.pathway(player)`，血脉 `PlayerAwakeningDataProvider.get(player)`。
- 当前没有独立的“实时回复速度”API；实际变化逻辑集中在 `EnergyStateHandler.java`，因此必须先抽出统一的只读计算入口，再让服务端逻辑和背包 GUI 共用，避免 GUI 复制公式后与真实恢复漂移。

### Plan 12.1 — 抽出实时能量回复快照计算
- 目标：提供一个无副作用的统一计算入口，返回玩家当前灵魂、肉体、查克拉三类能量的真实每 tick 回复/恢复变化快照，并保留 `EnergyStateHandler` 作为唯一实际扣减/恢复执行者。
- 写入范围：
  - 新增 `src/main/java/com/qdd/narutofix/util/EnergyRecoverySnapshot.java`，保存 soul/body/chakra 三类当前值、最大值、每 tick 净回复值、是否正在触发恢复、血脉倍率说明所需的只读字段。
  - 新增 `src/main/java/com/qdd/narutofix/util/EnergyRecoveryCalculator.java`，集中计算低灵魂睡觉/静止恢复、低肉体饱食/饱和恢复、低查克拉消耗灵魂和肉体后的查克拉恢复量；计算必须考虑阈值、缺口、当前能量余额、食物状态、血脉倍率和配置值，方法不得修改 capability、NBT、FoodStats 或 Chakra。
  - 修改 `src/main/java/com/qdd/narutofix/handler/EnergyStateHandler.java`，把现有 `handleLowSoul`、`handleLowBody`、`handleLowChakra` 中的数值计算改为复用上述 calculator 的结果，再由原 handler 执行 add/consume/sync，确保服务端实际行为与 GUI 展示口径一致。
  - 仅在确有必要时微调 `Configs.java` 的注释或字段命名兼容，不新增与本 todo 无关的配置项，不删除 gradle 运行时 mod 依赖。
- 依赖：依赖现有 `Configs.soul`、`Configs.body`、`Configs.chakraEmergency`、`SoulEnergyDataProvider`、`BodyEnergyDataProvider`、`PlayerAwakeningDataProvider`、`Chakra.pathway(player)`；不依赖背包 GUI 渲染。
- 验证范围/命令：用 IDEA MCP 执行针对新增 util 与 `EnergyStateHandler.java` 的 IDE 问题检查，然后执行 Gradle `compileJava --stacktrace --rerun-tasks`；Executor 自检需说明低灵魂、低肉体、低查克拉三条路径的旧行为是否保持，只把计算入口收口，不改变同步时机。
- 上线差距：完成后只解决“实时回复速度有可信数据源”，玩家仍看不到背包页面信息；还需要 Plan 12.2 的 GUI 渲染、完整 `build` 和 `runClient` 背包实机验收。

### Plan 12.2 — 在玩家背包页面渲染血脉与三类能量信息
- 目标：在原版玩家背包页面顶部或底部插入血脉信息、身体/灵魂/查克拉当前值与最大值、实时回复速度；布局适配原版 GUI 缩放、配方书开关和窄屏，不覆盖玩家模型、合成标题、物品槽、配方书按钮、虚拟瞳术槽。
- 写入范围：
  - 修改 `src/main/java/com/qdd/narutofix/mixin/MixinGuiInventory.java`，新增 `drawGuiContainerForegroundLayer` 的 `TAIL` 注入，仍保留现有虚拟瞳术槽背景注入。
  - 新增或修改一个客户端渲染辅助类，例如 `src/main/java/com/qdd/narutofix/client/gui/InventoryEnergyInfoRenderer.java`，负责读取 Plan 12.1 的快照、格式化血脉与能量文本、计算顶部/底部可用空间并绘制文本；渲染使用当前 GUI 坐标系，可在 `y < 0` 或 `y > ySize` 的外侧区域绘制，但必须先用 `guiTop/guiLeft/width/height/xSize/ySize` 判断屏幕内可见。
  - 布局策略必须完整实现：优先选择背包 GUI 下方外侧空间，其次选择上方外侧空间；单列四行空间不足时切换为两列两行紧凑布局；仍不足时只显示最短数值版，但不能遮挡原版控件。
  - 更新 `src/main/resources/assets/narutofix/lang/zh_cn.lang` 与 `src/main/resources/assets/narutofix/lang/en_us.lang`，补齐背包血脉、无血脉、因陀罗、阿修罗、双血脉、能量行、回复速度单位等本地化 key。显示建议统一为每秒速度（由每 tick 值乘 20），例如 `+1.20/s`；未触发恢复时显示 `+0.00/s`。
- 依赖：必须在 Plan 12.1 完成后执行，避免 GUI 端复制 `EnergyStateHandler` 的回复公式；依赖现有 `MixinGuiInventory`、中英文 lang、Soul/Body/Awakening capability 和 narutomod `Chakra` 客户端同步结果。
- 验证范围/命令：用 IDEA MCP 执行相关文件 IDE 问题检查、`compileJava --stacktrace --rerun-tasks`、`build --stacktrace`；随后 `runClient` 打开玩家背包，在 GUI Scale Auto/Normal/Large、配方书打开/关闭、宽屏与窄屏下目视确认文字不越界、不遮挡槽位和按钮、数值随 `/soulenergy`、`/bodyenergy` 及低能量恢复状态实时变化。
- 上线差距：完成并通过编译/构建后，功能达到可候选状态；距离上线仍需 Supervisor 正式复跑 `runClient` 冒烟、记录 `docs/diff.md`，确认 Mixin runtime 无警告并完成与 `2836` 合并前验收。

### 派发约束与上线对齐
- Plan 12.1 和 Plan 12.2 是本轮最小派发粒度，Executor 不得再把任一 plan item 拆成更小子任务派发；若执行中发现范围过大或新增阻塞，必须回到 Initiator 追加新的兄弟 plan item，而不是在 Executor 内自行拆分。
- 当前两个 plan item 不能并行：Plan 12.2 依赖 Plan 12.1 的统一回复快照计算。完成顺序为 12.1 -> 12.2 -> Supervisor 验证 -> 必要时补充计划。
- 与产品上线对比：现阶段新增背包信息仍是“已计划、未实现、未验证”。上线前必须完成 12.1/12.2、IDEA MCP `compileJava` 与 `build`、`runClient` 背包实机验收、`docs/diff.md` 差异更新，并在验证通过后才可合回 `2836`。

## 13. 冲突修正实施计划（2026-05-05 用户方案选择）

### 前置说明（本轮不修改项）
- 血脉觉醒门槛 1000：保持现状，不修改 pathway max 触发口径。
- soul/body → ninja XP → 查克拉上限闭环（BATTLEXP→pathway max）：保持现状，不修改链路结构。
- HUD 与背包信息重复展示：保持现状，本轮不做去重或互斥。
- 同步包 `runClient` 确认项：保持现状，本轮不在代理侧做实机刷新验证（见 Plan 13.7）。

上述四项在本轮 Plan 13 中均不进入写入范围；任何 Executor 在执行 13.1–13.7 时不得顺手改动这四项相关逻辑或文档结论。

### Plan 13.1 — 血脉初始加成立即发放、版本化对齐与旧存档迁移
- 目标：在血脉觉醒事件触发的同一时刻，立即发放对应血脉的初始能量/容量加成（B），并新增 `dataVersion` / `bloodlineAppliedVersion` 版本字段，对旧存档只做 max 上限对齐（C），不覆盖玩家当前 current 值，尊重玩家已获得的成长；`EnergyStateHandler` 内 tick 路径仅作为兜底（覆盖事件丢失场景），不再作为初始加成的主入口。
- 写入范围：
  - `src/main/java/com/qdd/narutofix/handler/EnergyStateHandler.java`：移除/弱化“tick 内首次发放初始加成”的主路径，改为仅在版本字段缺失或 max 落后于目标 max 时做兜底对齐。
  - `src/main/java/com/qdd/narutofix/handler/PlayerAwakeningHandler.java`（或等价觉醒事件 handler）：在觉醒成功的同一事件中调用统一的“apply bloodline initial bonus”入口，立即写入 max 与初始 current。
  - `src/main/java/com/qdd/narutofix/capability/soul/SoulEnergyData.java` / `SoulEnergyStorage.java`：新增 `dataVersion` 与 `bloodlineAppliedVersion` 字段、读写 NBT、默认值与迁移读路径。
  - `src/main/java/com/qdd/narutofix/capability/body/BodyEnergyData.java` / `BodyEnergyStorage.java`：同上。
  - 必要的常量/工具类（如新增 `BloodlineBonusApplier` 或在现有工具类中增加方法），集中“根据血脉计算目标 max 与初始 current 加成”的纯函数，供觉醒事件与兜底 tick 共用。
  - `src/main/resources/assets/narutofix/lang/zh_cn.lang` / `en_us.lang`：如新增提示文案则补齐 key。
  - `docs/diff.md` / `docs/new-features.md`：必要的差异说明更新。
- 关键实现要求：
  - 觉醒事件路径必须是“立即生效”，玩家在觉醒后下一帧即可看到 max 与 current 反映加成，无需等待 tick。
  - 旧存档迁移仅做 max 对齐：若 `bloodlineAppliedVersion < CURRENT_VERSION`，则把 max 提升到目标 max（取 `Math.max(currentMax, targetMax)`），并把 current 维持原值（不下调、不上调），随后写入新版本号。
  - 版本字段递增由常量集中管理；读路径必须容忍 NBT 中缺字段的旧存档（默认 0）。
  - 不得在迁移路径里发放“初始 current 加成”，避免老玩家被二次加成。
- 验证要求：
  - 用 IDEA MCP 对上述修改文件执行 IDE 问题检查（quality_get_file_problems）。
  - 用 IDEA MCP 执行 Gradle `compileJava --stacktrace --rerun-tasks`。
  - Executor 自检需在报告中说明：新角色觉醒、老角色升级（无版本字段）、已应用最新版本三种情形下的字段流转。
- 上线对齐：完成后“血脉初始加成发放口径冲突”与“旧存档迁移缺口”两项关闭，但仍需 Plan 13.7 用户自验清单实机验证后才能算具备合回 `2836` 条件。

### Plan 13.2 — ninja XP 派生命名修正与指令 reset baseline
- 目标：将 `ninjaXpConversionRate` 相关命名/文案从“转化（conversion）”改为“增长派生/贡献（contribution/derivation）”语义（A），明确 soul/body 不会因为派生 ninja XP 而被扣减；同时让管理员指令 `/soulenergy`、`/bodyenergy` 在修改玩家 soul/body 之后 reset `NinjaXpConversionHandler` 的 baseline，避免一次性补发巨量派生 XP（A）。
- 写入范围：
  - `src/main/java/com/qdd/narutofix/Configs.java`：新增新命名字段（如 `ninjaXpContributionRate`），保留旧字段名以兼容已有配置（读旧字段时记日志并赋值给新字段，不得直接破坏旧配置）。
  - `src/main/java/com/qdd/narutofix/handler/NinjaXpConversionHandler.java`：内部使用新命名；新增 `resetBaseline(EntityPlayer)` 公开入口供指令调用。
  - `src/main/java/com/qdd/narutofix/command/CommandSoulEnergy.java` / `CommandBodyEnergy.java`：在 set/add 类操作成功后调用 `NinjaXpConversionHandler.resetBaseline`。
  - `src/main/resources/assets/narutofix/lang/zh_cn.lang` / `en_us.lang`：更新相关 key 文案，去除“转化”表述，改为“贡献/派生”。
  - `docs/new-features.md` 或 `docs/diff.md`：相应说明更新。
- 关键实现要求：
  - 旧配置兼容：旧的 `ninjaXpConversionRate` 字段必须仍可被读取并迁移到新字段，迁移失败不得抛异常导致 mod 不可加载。
  - 派生路径不得对 soul/body 产生扣减副作用；所有原“转化扣能量”的代码路径必须移除或证明本身就没有扣减。
  - `resetBaseline` 必须将 baseline 设为玩家当前 soul/body 值（或等价语义），保证指令后不再回算历史增长。
- 验证要求：
  - IDEA MCP IDE 问题检查 + `compileJava --stacktrace --rerun-tasks`。
  - Executor 自检说明旧配置文件加载、指令 set 后 baseline 是否被正确重置。
- 上线对齐：完成后“soul/body→ninja XP 语义冲突”与“管理员指令副作用”两项关闭；上线仍依赖 Plan 13.7 自验。

### Plan 13.3 — 静止自然回复查克拉与创造模式 body 独立恢复
- 目标：允许玩家在静止（非战斗、非高消耗）状态下自然回复查克拉，避免低查克拉阈值断崖式跳变；创造/调试模式下，body 按独立可配置速率恢复，不消耗饱食度（B）。
- 写入范围：
  - `src/main/java/com/qdd/narutofix/Configs.java`：新增配置项（自然查克拉回复速率、触发条件阈值、创造模式 body 恢复速率），均带默认值与注释。
  - `src/main/java/com/qdd/narutofix/util/EnergyRecoveryCalculator.java`：在不破坏 Plan 12.1 现有快照口径的前提下，新增“静止自然查克拉回复”分支与“创造模式 body 独立恢复”分支，仍是无副作用纯计算。
  - `src/main/java/com/qdd/narutofix/util/EnergyRecoverySnapshot.java`：必要时补充字段，描述当前回复来源（自然 / 紧急 / 创造模式）以便 GUI 展示。
  - `src/main/java/com/qdd/narutofix/handler/EnergyStateHandler.java`：根据新分支执行实际加值，并保持唯一执行者地位。
  - `src/main/java/com/qdd/narutofix/client/gui/InventoryEnergyInfoRenderer.java`：消费新快照字段，确保背包页面回复速度与服务端实际行为一致。
- 关键实现要求：
  - “静止”判定必须明确（如近 N tick 未消耗查克拉、未受伤、未挥剑等），由配置控制阈值，不得硬编码。
  - 自然回复速率必须低于紧急恢复速率，阈值切换平滑，避免新的断崖。
  - 创造模式 body 恢复独立于饱食度，且不与生存模式恢复路径冲突；切换模式必须立即生效。
- 验证要求：
  - IDEA MCP IDE 问题检查 + `compileJava --stacktrace --rerun-tasks`。
  - Executor 自检：低查克拉静止、低查克拉战斗、创造模式 body 三种情形下快照与实际值一致。
- 上线对齐：完成后“低查克拉断崖”与“创造/调试模式 body 行为”两项关闭；上线仍依赖 Plan 13.7 自验。

### Plan 13.4 — 兵粮丸 BODY_ONLY 语义文案修正
- 目标：保留兵粮丸“只直接恢复肉体能量”的现有行为（A），通过 tooltip / lang / docs 明确语义为“恢复肉体能量；当查克拉处于低位时，可经由系统间接影响查克拉恢复”，消除玩家对“吃了兵粮丸不回查克拉”的预期误差。
- 写入范围：
  - `src/main/resources/assets/narutofix/lang/zh_cn.lang` / `en_us.lang`：更新兵粮丸 tooltip 与描述 key。
  - 必要时新增/调整 tooltip 注入相关 mixin 或 item handler（仅文案/tooltip 行为，不改实际效果）。
  - `docs/new-features.md` 或 `docs/diff.md`：补充语义说明。
- 关键实现要求：
  - 不得修改兵粮丸的实际数值效果与触发逻辑。
  - 文案必须中英文同步更新，避免一种语言仍显示旧描述。
- 验证要求：
  - IDEA MCP IDE 问题检查 + `compileJava --stacktrace --rerun-tasks`（若仅改 lang/docs，可只跑 lang 资源校验，仍建议跑一次 compileJava 兜底）。
- 上线对齐：完成后“兵粮丸语义冲突”关闭；上线仍依赖 Plan 13.7 自验。

### Plan 13.5 — 写轮眼进化来源配置化
- 目标：新增配置 `sharinganEvolutionSource`，取值 `VANILLA` / `SOUL` / `BOTH`，默认 `BOTH`（C）；当配置为 `BOTH` 时，原版（narutomod）规则或本 mod 灵魂能量规则任一满足即可触发进化，降低与 narutomod 原始规则耦合带来的不进化风险。
- 写入范围：
  - `src/main/java/com/qdd/narutofix/Configs.java`：新增枚举型配置 `sharinganEvolutionSource`，默认 `BOTH`，附注释。
  - 写轮眼进化相关 handler 或 mixin（视现有实现而定）：根据配置组合判定是否触发进化，并保留原有进化效果实现。
  - `src/main/resources/assets/narutofix/lang/zh_cn.lang` / `en_us.lang`：必要的提示/配置说明 key。
  - `docs/new-features.md` 或 `docs/diff.md`：行为差异说明。
- 关键实现要求：
  - 三种取值都必须有明确分支：`VANILLA` 仅原版规则；`SOUL` 仅灵魂规则；`BOTH` 任一满足即可。
  - 不得移除已有原版规则的兼容性；当 narutomod 不存在某些 API 时必须有降级保护，避免抛 ClassNotFoundException。
- 验证要求：
  - IDEA MCP IDE 问题检查 + `compileJava --stacktrace --rerun-tasks`。
  - Executor 自检：三种配置取值下分支选择是否正确。
- 上线对齐：完成后“写轮眼进化耦合冲突”关闭；上线仍依赖 Plan 13.7 自验。

### Plan 13.6 — ChakraSyncHelper 脏标记合并同步
- 目标：在 `ChakraSyncHelper` 内引入“脏标记 + server tick 末尾统一发送”模式（C），消除同一 tick 内多个分支重复调用 `PacketSyncChakra` 的网络浪费与潜在乱序问题。
- 写入范围：
  - `src/main/java/com/qdd/narutofix/sync/ChakraSyncHelper.java`（按实际路径调整）：新增 per-player 脏标记集合与 `markDirty(EntityPlayerMP)` 接口；保留现有 `refresh` 入口但内部改为只 mark dirty。
  - 现有调用点（`EnergyStateHandler`、`NinjaXpHelper`、各类指令 handler 等）：调用 `refresh` 的地方继续保留调用，但实际网络发送由统一 flush 完成。
  - `EventLoader` 或新增 `ChakraSyncTickHandler`：注册 `ServerTickEvent.Phase.END`，在每个 server tick 末尾对所有 dirty 玩家执行一次 `PacketSyncChakra` 发送并清空脏标记。
- 关键实现要求：
  - 必须保证“一个 tick 至多一次发包”；玩家下线/维度切换时清理脏标记，避免内存泄漏。
  - 不得改变现有调用方语义：调用方不感知合并行为，依旧调用 `refresh`。
  - 在玩家加入服务器、切维度、首次同步等关键路径上，仍需立即发送一次（可通过显式 `flushNow` 入口或在 mark dirty 后立即 flush 当前玩家）。
- 验证要求：
  - IDEA MCP IDE 问题检查 + `compileJava --stacktrace --rerun-tasks`。
  - Executor 自检：单 tick 多次 mark 是否合并为一次发送、跨 tick 是否各发一次、玩家下线后是否清理。
- 上线对齐：完成后“同步重复调用”关闭；上线仍依赖 Plan 13.7 自验。

### Plan 13.7 — 用户自验清单文档化
- 目标：本轮不要求代理负责 `runClient` 实机验证；改为产出一份用户可直接执行的自验清单，覆盖背包 GUI、HUD、血脉、能量、查克拉、兵粮丸、写轮眼、指令、同步刷新、旧存档迁移等所有本轮变更面，供用户自行在客户端验证。
- 写入范围：
  - 新增 `docs/validation-checklist.md`：分章节列出每个 Plan（13.1–13.6 + 既有 12.x）需要用户在客户端确认的具体步骤、预期现象、失败回退指示。
  - 必要时在 `docs/diff.md` 增加一行表格说明，指向 `validation-checklist.md`。
- 关键实现要求：
  - 清单条目必须可操作：每条包含“操作步骤 / 预期 / 异常如何反馈”。
  - 覆盖项至少包括：
    - 背包 GUI：原版缩放 Auto/Normal/Large、配方书开关、宽屏/窄屏下血脉与三类能量显示。
    - HUD：与背包信息共存时无错位（不去重，仅检查不破坏既有 HUD）。
    - 血脉：新觉醒立即出现初始加成；老存档登录后只 max 对齐、current 不变。
    - 能量：低灵魂/低肉体/低查克拉、静止自然回复查克拉、创造模式 body 恢复。
    - 查克拉：阈值切换无断崖；同步刷新（背包打开、跨维度、登录）。
    - 兵粮丸：tooltip 中英文文案描述明确。
    - 写轮眼：`sharinganEvolutionSource` 三种取值下的进化行为。
    - 指令：`/soulenergy`、`/bodyenergy` set/add 后 ninja XP 不再补发巨量派生。
    - 旧存档迁移：旧 `ninjaXpConversionRate` 配置仍可加载；旧 NBT 缺版本字段时不报错。
- 关键约束：本计划只定义“清单需要包含什么”，不要求 Executor 在本计划阶段直接运行 `runClient`；后续实现该计划时也只负责落地文档，不负责执行客户端。
- 验证要求：
  - IDEA MCP 对新增 markdown 文件做基本可读性检查（路径、命名）。
  - 不要求 `compileJava` / `build` / `runClient`。
- 上线对齐：完成本计划后，用户依据清单自行验证；只有在用户确认全部通过后，才视为具备合回 `2836` 的客户端验收条件。代理侧不再代行 `runClient`。

### 派发与上线对齐
- Plan 13.1–13.7 均为最小派发单元，Executor 不得再向更细粒度子任务派发；范围超出请回到主代理追加兄弟计划。
- 顺序建议：13.1 → 13.2 → 13.3 → 13.6 → 13.4 → 13.5 → 13.7。13.6 涉及共享同步逻辑，完成后建议主代理再触发一次完整 `build`；13.7 仅文档不阻塞构建。
- 上线门禁：所有代码 Plan 必须通过 IDEA MCP `compileJava`，共享逻辑变更后再跑 `build`；`runClient` 由用户依据 Plan 13.7 清单自验通过后，方可合回 `2836`。
