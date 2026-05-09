# 新增功能说明

## 文档定位

本文用于说明 `feature/energy-system` 分支新增的玩家功能、配置入口、管理入口、验证状态和上线前检查点。项目仍基于 Minecraft 1.12.2 Forge 与 Java 8，运行时仍依赖原 `narutomod`，不通过删除 Gradle 运行时 mod 依赖来规避兼容问题。

## 功能目标

本轮新增功能的目标是把原本偏单点的查克拉、忍者经验、写轮眼成长和体质属性，整理成可配置、可同步、可验收的能量循环：

- 灵魂能量负责精神成长、查克拉恢复效率、忍术经验倍率、忍术蓄力速度和写轮眼进化。
- 肉体能量负责身体成长、战斗属性、低体能惩罚和兵粮丸恢复。
- 查克拉不再依赖原版睡觉或静止自然恢复；低查克拉时需要消耗灵魂能量与肉体能量转换恢复；静止时非低查克拉状态下按可配置速率自然恢复，不消耗灵魂或肉体能量。
- ninja XP 不再通过原版战斗路径增长，改为根据灵魂和肉体能量的共同成长贡献/派生。
- HUD、指令、网络同步、配置和 Mixin 覆盖形成完整闭环，方便后续上线验收。
- 创造模式下肉体能量未满即按独立可配置速率恢复，不消耗饱食度。

## 玩家可见变化

### HUD

游戏内新增三条 HUD：

- 灵魂能量
- 肉体能量
- 查克拉

HUD 使用 `assets/narutofix/textures/gui/hud.png` 的切片渲染，默认锚定在屏幕左侧与原版快捷栏左边缘之间。空间足够时显示标签、进度条和数值；窄屏或可用空间不足时只保留三条进度条，避免遮挡快捷栏和主体画面。原版 `narutomod` 查克拉 HUD 已通过 Mixin 屏蔽。

### 灵魂能量

灵魂能量包含当前值和上限，支持 NBT 持久化、死亡/克隆继承和服务端到客户端同步。

主要行为：

- 击杀非玩家生物或实体时提升灵魂能量当前值与上限。
- 玩家死亡时提升灵魂能量上限，同时按配置损失部分当前值。
- 灵魂能量影响查克拉恢复速度、忍术经验获取倍率和忍术蓄力速度。
- 灵魂能量低于配置比例时施加反胃效果。
- 睡觉或连续静止达到配置 tick 后，可以按配置缓慢恢复灵魂能量。
- 因陀罗血脉会按配置提高初始灵魂能量上限和当前值，并影响低灵魂能量恢复倍率。

### 肉体能量

肉体能量包含当前值和上限，支持 NBT 持久化、死亡/克隆继承和服务端到客户端同步。

主要行为：

- 攻击、受击、挖矿会推动肉体能量成长，并按配置消耗当前值。
- 肉体能量影响最大生命、护甲、生命回复、移动速度、攻击力和攻击速度。
- 肉体能量低于配置比例时施加缓慢、挖掘疲劳和虚弱。
- 饱和度和饱食度可以按配置转换为肉体能量恢复。
- 普通兵粮丸和金色兵粮丸改为恢复肉体能量。
- 创造模式下肉体能量未满即按独立可配置速率恢复，不消耗饱食度。
- 阿修罗血脉会按配置提高初始肉体能量上限和当前值，并影响低肉体能量恢复倍率。

### 查克拉恢复

原版 `Chakra.PathwayPlayer.onUpdate()` 中由睡觉和静止触发的查克拉自然恢复已通过 Mixin 禁用。查克拉低于配置比例时，紧急恢复流程会同时消耗灵魂能量与肉体能量，并按配置转化为查克拉。

当查克拉未低于阈值且玩家连续静止达到可配置的 tick 数后，按可配置速率自然恢复查克拉，不消耗灵魂或肉体能量，避免低查克拉阈值附近的断崖式变化。自然回复速率默认低于紧急恢复速率。

创造模式下 body 只要未满就会按独立可配置速率恢复，不受低体能阈值限制，也不消耗饱食度。

这意味着查克拉恢复不再是无成本自然增长，而是与灵魂、肉体两条资源的当前状态绑定，并在静止时获得温和自然恢复。兵粮丸也不再直接恢复查克拉，避免绕过新的能量转换规则。

### ninja XP 贡献/派生

原版战斗获取 ninja XP 的路径已禁用，原版 ninja XP 生命加成也已禁用。新的增长方式为：

- 记录 `min(灵魂当前值, 肉体当前值)` 的增长差值。
- 只在两种能量的较小当前值增长时贡献/派生 ninja XP。
- 派生路径不扣减灵魂能量或肉体能量，避免能量随时间自然流逝。
- `/addninjaxp` 的经验修改路径已禁用，避免绕过新系统。
- 管理员指令 `/soulenergy` 和 `/bodyenergy` 的 set/add 操作后自动重置基线，防止一次性补发巨量派生 XP。

### 写轮眼进化

写轮眼成长改为由灵魂能量阈值驱动，并新增配置 `sharinganEvolutionSource`（默认 `BOTH`）控制进化来源：

- `VANILLA` — 仅 narutomod 原版规则（BATTLEXP/查克拉阈值）触发进化，本 mod 灵魂阈值进化不运行。
- `SOUL` — 仅灵魂能量阈值触发进化，并尽量屏蔽原版进化入口。
- `BOTH` — 不拦截 narutomod 原版进化入口，同时运行灵魂能量阈值进化；原版规则或灵魂能量阈值任一满足即可触发进化，默认兼容现状。

进化逻辑覆盖头盔栏与 `narutofix` 虚拟瞳术槽，避免只在单一装备入口生效。

## 管理和调试入口

新增两个管理指令，用于读取或修改玩家的能量当前值和上限：

```text
/soulenergy <player> <get|set|add> <current|max> [value]
/bodyenergy <player> <get|set|add> <current|max> [value]
```

指令修改后会触发同步，客户端 HUD 应立即反映新的能量或查克拉状态。

## 配置范围

新增配置集中在 `Configs.java`，覆盖以下方面：

- 灵魂能量初始值、上限、成长、死亡损失、低量阈值、睡觉/静止恢复速度。
- 肉体能量初始值、上限、成长、战斗/挖矿消耗、低量阈值、饱食度恢复速度。
- 查克拉低量阈值、每 tick 能量消耗、灵魂和肉体到查克拉的转化率。
- 查克拉静止自然恢复速率与静止所需 tick。
- 创造模式肉体能量独立恢复速率（不消耗饱食度）。
- ninja XP 转化率和检测间隔。
- HUD 水平和垂直偏移。
- 写轮眼各阶段进化所需灵魂能量阈值。
- 因陀罗和阿修罗血脉的初始能量倍率与低能量恢复倍率。

新增配置注释已翻译为中文，`en_us.lang` 和 `zh_cn.lang` 已补齐新增配置、HUD 和提示文本。

## 技术实现范围

本轮新增功能包含以下技术闭环：

- 灵魂能量和肉体能量 Forge capability。
- NBT 持久化、玩家 clone 继承、登录/维度切换/重生同步。
- 服务端事件驱动的能量增长、消耗和属性刷新。
- 新增查克拉同步包，确保灵魂/肉体能量或 ninja XP 变化后客户端查克拉 HUD 能刷新。
- 通过 Mixin 覆盖原版查克拉 HUD、查克拉自然恢复、兵粮丸直接恢复、兵粮丸药水间接恢复、原版经验和写轮眼进化路径。
- HUD 使用贴图切片渲染，条高、条宽、快捷栏安全距离和文本间距已经按现有视觉反馈微调。

## 验证状态

已完成的构建验证：

- IDEA MCP `compileJava --stacktrace` 通过。
- IDEA MCP `compileJava --stacktrace --rerun-tasks` 通过。
- IDEA MCP `build --stacktrace` 通过。
- IDEA MCP `build --stacktrace --rerun-tasks` 通过。
- 本说明文档新增后，IDEA MCP `build --stacktrace` 再次通过，Gradle 任务均为 up-to-date。

本说明文档新增后未改变 Java 或资源逻辑；提交前已确认当前构建链路仍通过。

## 上线前验收重点

当前状态是“可构建候选”，还没有完成 `runClient` 实机冒烟验收。上线前至少需要确认：

1. 客户端启动日志中所有新增 Mixin apply 无警告和错误。
2. HUD 三条能量条位置、条高、条宽、文本隐藏逻辑和 `hud.png` 切片显示正确。
3. `/soulenergy`、`/bodyenergy` 修改后服务端与客户端 HUD 立即同步。
4. 击杀、死亡、攻击、受击、挖矿能按配置触发能量变化。
5. 静止不再触发原版无成本查克拉自然恢复；非低查克拉静止时可按配置速率自然恢复查克拉，不消耗灵魂或肉体能量。
6. 低查克拉通过消耗灵魂能量与肉体能量紧急恢复；查克拉未满且非低时按静止自然恢复。
7. 低灵魂能量和低肉体能量的负面效果、睡觉/静止恢复、饱食度恢复符合配置。
8. 创造模式下肉体能量未满即按独立配置速率恢复，不消耗饱食度。
9. 普通和金色兵粮丸只恢复肉体能量，不直接或间接恢复查克拉。
10. 因陀罗和阿修罗血脉的初始能量倍率与恢复倍率生效。
11. ninja XP 只通过新的能量增长差值转换，不再通过原版战斗或指令路径绕过。
12. 写轮眼按灵魂能量阈值从 1 勾玉到永恒万花筒完整进化。

## 与产品上线的关系

这份文档补齐了新增功能的上线说明和验收口径，降低交付时只看代码或零散 todo 的风险。代码层面已经达到可构建候选，产品上线仍卡在实机客户端验收：只有 `runClient` 确认 HUD、Mixin、同步、低能量循环、兵粮丸、血脉倍率、ninja XP 和写轮眼进化在游戏内全链路正确后，才应合并回 `2836` 并作为上线候选。

### 兵粮丸 tooltip 文案澄清（Plan 13.4 窄修）

兵粮丸（普通/金色）的 tooltip 在 narutofix 中以 `§7` 灰色替换原版查克拉恢复描述：
- 在 `MixinItemMilitaryRationsPillFood` 的 `addInformation` TAIL 注入中，先用 `I18n.translateToLocal` 查出 narutomod 原版 lang key（`tooltip.mrp.browntip` / `tooltip.mrp.goldtip`）的当前语言翻译值，通过 `List.remove()` 精准移除误导行。
- 再追加两行更正说明：首行明确"直接恢复肉体能量，不直接恢复查克拉"；第二行说明"低查克拉时可通过能量系统间接帮助查克拉恢复"。
- 无论游戏语言是中文还是英文，旧文案均被移除，不会与 narutofix 更正文本同时显示。不改动兵粮丸的数值与执行逻辑。

---

## P0: 查克拉回复口径诚实化

### 问题

旧系统在背包 GUI 和 HUD 中显示查克拉自然回复速率（如"10/s"），但实际上：
- 低查克拉时走的紧急回复路径
- 非低查克拉时旧版静止自然恢复不消耗任何能量，且与客户端显示不同步
- 玩家看到显示 10/s 但查克拉没有实际增长

### 修复

- `EnergyRecoveryCalculator.calculateChakraRecovery` 已移除旧版 `chakraStationary` 无成本静止自然恢复分支
- `EnergyRecoverySnapshot` 的 chakra `recoveryPerTick` 现在只反映低查克拉紧急恢复的实际可执行值
- 非低查克拉且条件不满足时，recovery=0，HUD/背包显示 0/s，不再产生虚假显示

### 配置

旧 `chakraStationary` 配置保留在 `narutofix.cfg` 中（标注 deprecated），不再默认生效。

---

## P1: 下蹲静止查克拉转化

### 行为

当玩家同时满足以下所有条件时，消耗灵魂能量和肉体能量恢复查克拉：
1. 玩家正在下蹲（按住 Shift）
2. 玩家站在地面上（`onGround == true`）
3. 玩家已连续静止超过 `requiredTicks` 刻（默认 40 tick = 2 秒）
4. 距上次触发已间隔 `triggerIntervalTicks` 刻（默认 20 tick = 1 秒）
5. 查克拉未满

### 每次触发默认消耗/获得

| 项目 | 默认值 |
|------|--------|
| 消耗灵魂能量 | 1 |
| 消耗肉体能量 | 3 |
| 获得查克拉 | 10 |

### 能量不足策略

- **SKIP（默认）**：任一能量不足时跳过本次触发
- **SCALE**：按实际可消耗比例缩放回复量

### 相关配置

路径：`narutofix.cfg` → Crouch Chakra Exchange Settings

| 配置键 | 默认值 | 说明 |
|--------|--------|------|
| `enabled` | `true` | 启用/禁用 |
| `requiredTicks` | `40` | 所需连续静止刻数 |
| `triggerIntervalTicks` | `20` | 触发间隔刻数 |
| `soulCostPerTrigger` | `1.0` | 每次消耗灵魂能量 |
| `bodyCostPerTrigger` | `3.0` | 每次消耗肉体能量 |
| `chakraGainPerTrigger` | `10.0` | 每次获得查克拉 |
| `insufficientPolicy` | `SKIP` | 能量不足策略 |

---

## P2: 饱食度恢复肉体能量修复

### 问题

旧代码中，当 `handleLowBody` 的 restore=0 时（如肉体能量已满或未低于阈值），`BODY_FOOD_DEBT` 不会被写回，导致已累积的欠债被丢弃。玩家下次进入低肉体状态时，之前累积的欠债已经丢失。

同时，旧代码每 tick 都 floor(debt) 从 foodLevel 扣减饥饿值，导致玩家即使饱和度高也会掉饥饿条。

### 修复

1. **无条件写回 debt**：`handleLowBody` 现在无论 restore 是否为 0，只要非创造模式都执行 `setDouble(BODY_FOOD_DEBT, debtAfter)`
2. **新增 `allowConsumeHunger` 配置**：默认 `false`，此时只消耗饱和度，不累积 debt，不扣 foodLevel。设为 `true` 恢复旧版行为
3. **创造模式豁免**：创造模式始终不消耗饱食度

### 相关配置

| 配置键 | 默认值 | 说明 |
|--------|--------|------|
| `body.allowConsumeHunger` | `false` | 是否允许直接扣减饥饿值（foodLevel） |

---

## P3: 睡觉一次性恢复

### 行为

玩家在床上睡觉自然醒来后（非 ESC/右键取消），立即恢复：
- 灵魂能量：上限的 30%
- 肉体能量：上限的 10%

### 限制

- 短睡（ESC 取消、被怪物惊醒等）通过 `PlayerWakeUpEvent.wakeImmediately()=true` 判定，不会触发恢复
- 一次性结算（不是每 tick 百分比增长）
- 不恢复原版睡觉查克拉
- 不恢复查克拉

### 实现

监听 `PlayerWakeUpEvent` 事件：
- `wakeImmediately()=true`（取消/短睡）→ 跳过恢复
- `wakeImmediately()=false`（自然过夜醒来）→ 立即计算并发放恢复

不使用 `player.ticksExisted` 差值检测，因为睡觉跳时不会推进 `ticksExisted`，旧 tick-count 方案在 `minimumSleepTicks=12510` 下永远不触发。

### 相关配置

路径：`narutofix.cfg` → Sleep Recovery Settings

| 配置键 | 默认值 | 说明 |
|--------|--------|------|
| `soulRecoveryPercent` | `0.30` | 回复灵魂能量百分比（基于上限） |
| `bodyRecoveryPercent` | `0.10` | 回复肉体能量百分比（基于上限） |
| `minimumSleepTicks` | `12510` | [已废弃] 不再生效，保留兼容旧配置文件 |

---

## 验证提醒

所有 P0-P3 功能都需要通过 `runClient` 实机确认。请参照 `docs/validation-checklist.md` 中的对应章节操作。

---

## 近期修正（2026-05-06）

### 查克拉上限公式变更

查克拉上限不再仅由 `BATTLEXP * 0.5` 决定，改为叠加灵魂/肉体能量的当前值：

```
chakraMax = BATTLEXP * 0.5 + soul.current + body.current
```

两处拦截：
- `resetMax()` — 构造函数路径，用 `@Inject(HEAD, cancellable)` 直接替换方法体
- `onUpdate()` — 每 tick 重算路径，用 `@Inject(INVOKE sendToClient)` 在 `sendToClient()` 发包前补上 soul+body 加成

实现类：`MixinPathwayPlayer`（target `Chakra.PathwayPlayer`，extends `Chakra.Pathway<EntityPlayer>` 避免 `@Shadow` 继承问题）

### 死亡后查克拉不丢失

`Chakra.PathwayPlayer.PlayerHook.onDeath` 在服务端分支会调用 `p.set(0)` 将查克拉置零并移除 `playerMap` 条目，导致 `PlayerEvent.Clone` 的 LOWEST 句柄找不到旧数据，复活后查克拉变成 0。

修复：新增 `MixinChakraPlayerHookOnDeath`，`@Inject(HEAD, cancellable)` 取消服务端分支的执行，保留查克拉值和 `playerMap` 引用。

### 低查克拉大幅上限触发 narutomod 原版负面效果

`Pathway.onUpdate()` 中有 `d < 10 && d1 > 150` 检查，当查克拉绝对值低于 10 且上限超过 150 时施加虚弱 3、缓慢 3、反胃 3。新的 chakraMax 公式将上限大幅抬高，频繁触发此检查。

修复：在已有 `mixinPathway`（target `Chakra.Pathway`）的 `addPotionEffect` 拦截注入中，直接将来自 `Pathway.onUpdate()` 的所有药水效果调用取消，低能量负面效果由 narutofix 的 `handleLowSoul`（反胃，阈值 20% 可配）和 `handleLowBody`（虚弱/缓慢/挖掘疲劳，阈值 20% 可配）统一控制。

### 因陀罗/阿修罗初始值改为固定值

因陀罗/阿修罗的初始能量加成从倍率改为独立固定值配置：

| 配置键 | 默认值 | 说明 |
|--------|--------|------|
| `soul.indraInitialSoulBonusMax` | `100.0` | 因陀罗额外灵魂能量上限 |
| `soul.indraInitialSoulBonusCurrent` | `100.0` | 因陀罗额外灵魂能量当前值 |
| `body.asuraInitialBodyBonusMax` | `100.0` | 阿修罗额外肉体能量上限 |
| `body.asuraInitialBodyBonusCurrent` | `100.0` | 阿修罗额外肉体能量当前值 |

旧 `indraInitialSoulMultiplier` / `asuraInitialBodyMultiplier` 已移除。

### 饱食度保留 9 格底线

`allowConsumeHunger=true` 时，饥饿值最多扣到 9 格为止，不扣穿。默认 `false` 只消耗饱和度，不动 `foodLevel`。

### 旧废弃配置清理

已移除以下配置项及对应 lang 键：

| 移除项 | 原因 |
|--------|------|
| `Configs.upgrade` | 旧写轮眼进化查克拉阈值，已被 soul energy 阈值替代 |
| `ChakraStationaryRecoveryConfig` | 旧无成本静止恢复，已被下蹲查克拉转化替代 |
| `SleepRecoveryConfig.minimumSleepTicks` | 旧睡觉 tick 数判定，已被 `PlayerWakeUpEvent` 替代 |
| `indraChakraRegenPerSecond` | 旧因陀罗直接恢复查克拉 |
| `dualBloodlineChakraBonus` | 旧双血脉直接恢复查克拉 |

### 登录同步补全

`EnergySyncHandler` 已补 `EntityJoinWorldEvent` 事件，登录/进世界/切维度/重生四个入口统一同步 soul/body/chakra。

---

## 四勾玉轮回眼 / 六勾玉轮回眼

### 头盔槽须佐激活
六勾玉轮回眼戴在头盔槽时也可以正常激活须佐。之前只支持虚拟瞳术槽（按 R 切换的槽位），现在头盔槽也由 mixin 接管，使用头盔槽的六勾玉作为有效眼须佐激活眼。

---

## 身体能量属性上限

身体能量成长到较高值时，属性加成（移速、攻速、攻击力、护甲）有可配置的上限，避免瞬移和秒杀。默认上限：

| 属性 | 上限 |
|------|------|
| 护甲 | 30 |
| 移动速度倍率 | 2.0 (3x 原速) |
| 攻击力倍率 | 10.0 (11x 伤害) |
| 攻击速度倍率 | 3.0 (4x 原速) |

**不限制**最大生命值和生命恢复速度。

---

## 紧凑血量进度条

当玩家最大生命值过高时，原版心形渲染会超出 HUD 边界。新增紧凑型血量进度条：

- 直接替换原版心形渲染
- 样式：120px × 8px 进度条，绿色→黄色→红色渐变填充
- 文本：居中显示 "当前HP / 最大HP"
- 开关：可在配置中关闭 `enableCompactHealthBar` 以恢复原版渲染

