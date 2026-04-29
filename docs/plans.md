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
