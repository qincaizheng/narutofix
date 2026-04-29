# Todo Diff

## 已完成

- 拉取 `https://github.com/AHZNB/naruto_mod` 的 `0.3.1-beta` 分支到 `/root/workspace/naruto_mod_0.3.1`，commit `2c0dec2a48da09f206a447413a9847e1dc533d4d`。
- 新增灵魂能量 capability：
  - 当前值、最大值、NBT 持久化、玩家 clone 数据继承、死亡当前值衰减。
- 新增肉体能量 capability：
  - 当前值、最大值、NBT 持久化、玩家 clone 数据继承。
- 新增配置：
  - 灵魂能量成长/损失/倍率/写轮眼阈值。
  - 肉体能量成长/消耗/属性倍率。
  - ninja XP 转化率和间隔。
  - HUD 偏移。
- 新增指令：
  - `/soulenergy <player> <get|set|add> <current|max> [value]`
  - `/bodyenergy <player> <get|set|add> <current|max> [value]`
- 新增 HUD：
  - 灵魂能量、肉体能量、查克拉三条显示。
  - 三条 HUD 已改用 `textures/gui/hud.png` 切片渲染，默认位于屏幕左侧与快捷栏左边缘之间。
  - HUD 文本在左侧，数值在右侧。
  - 已通过 Mixin 屏蔽 narutomod 原版查克拉 HUD。
- 灵魂能量系统：
  - 击杀非玩家生物/实体增加当前值和上限。
  - 玩家死亡增加上限并损失当前值百分比。
- 肉体能量系统：
  - 攻击、受击、挖矿增加上限并消耗当前值。
- 灵魂能量影响：
  - 查克拉恢复速度。
  - 忍术经验获取倍率。
  - 忍术蓄力速度。
- 肉体能量影响：
  - 最大生命、护甲、生命回复、移动速度、攻击力、攻击速度。
- 忍者经验：
  - 禁用原版战斗获取 ninja XP。
  - 禁用原版 ninja XP 生命加成。
  - 禁用 `/addninjaxp` 的经验修改路径。
  - 新增按 `min(soul.current, body.current)` 比例自动转化为 ninja XP。
- 写轮眼进化：
  - 原 1/2/3 勾玉进化改为灵魂能量阈值。
  - 新增 3 勾玉到万花筒、万花筒到永恒万花筒的灵魂能量阈值进化。
  - 覆盖头盔栏与 narutofix 虚拟瞳术槽。
- 验证：
  - IDEA MCP `compileJava --stacktrace` 通过。
  - IDEA MCP `build --stacktrace` 通过。
- 翻译：
  - `Configs.java` 中所有 `@Config.Comment` 已统一翻译为中文。
  - `en_us.lang` / `zh_cn.lang` 已补齐新增配置与 HUD/提示文本翻译。
  - 翻译补齐后 IDEA MCP `compileJava --stacktrace` 通过。
- HUD 贴图调整：
  - `onOverlayEvent.java` 使用 `hud.png` 渲染三条能量条。
  - `Configs.java` 的 HUD 偏移项作为相对快捷栏锚点的微调值。
  - 调整后 IDEA MCP `compileJava --stacktrace` 与 `build --stacktrace` 均通过。
- 最新 todo 修复：
  - HUD 改为按原版快捷栏锚点计算，默认位于屏幕左侧与快捷栏左边缘之间，配置项作为微调偏移。
  - HUD 当前值进度条改在贴图边框内部绘制，避免填充条偏下。
  - ninja XP 转化不再周期性扣减灵魂/肉体当前值，只在两种能量较小当前值增长后按增长差值增加 ninja XP。
  - 新增查克拉同步包，灵魂/肉体能量或 ninja XP 变化后同步客户端查克拉显示。
  - 灵魂/肉体能量新增初始当前值配置，默认均为 `100.0`；初始上限也默认为 `100.0`。
  - 最新 todo 调整后 IDEA MCP `compileJava --stacktrace` 与 `build --stacktrace` 均通过。
- 2026-04-29 最新 todo 补充：
  - HUD 三条能量条默认上移，避免查克拉条被快捷栏吞掉。
  - HUD 会按“屏幕左边框到快捷栏左边缘”的可用宽度判断是否渲染文本；空间不足时只渲染三条条形进度。
  - 通过 `MixinPathwayPlayer` 禁用原版 `Chakra.PathwayPlayer.onUpdate()` 中睡觉和静止触发的查克拉自然恢复。
  - 新增低查克拉恢复：查克拉低于可配置比例时，同时消耗灵魂能量和肉体能量恢复查克拉，阈值、单 tick 消耗量、两种转化率均可配置。
  - 新增低灵魂能量状态：低于可配置比例时施加反胃；睡觉或连续静止后缓慢恢复，睡觉/静止速度和静止 tick 均可配置。
  - 新增低肉体能量状态：低于可配置比例时施加缓慢、挖掘疲劳、虚弱；快速消耗饱和度/饱食度恢复肉体能量，消耗速度和转化率可配置。
  - 普通/金色兵粮丸现在恢复肉体能量；通过 `MixinItemMilitaryRationsPillFood` 禁用直接查克拉恢复，通过 `MixinProcedureChakraRegenerationOnPotionActiveTick` 禁用兵粮丸药水带来的间接查克拉恢复。
  - 因陀罗血脉按配置将初始灵魂能量上限/当前值提高到默认值倍率，阿修罗血脉按配置将初始肉体能量上限/当前值提高到默认值倍率；对应低能量恢复倍率也可配置。
  - `BloodlineAbilityHandler` 不再给因陀罗直接每秒恢复查克拉，避免绕过新的低查克拉能量转化规则。
  - IDEA MCP `compileJava --stacktrace --rerun-tasks` 通过。
  - IDEA MCP `build --stacktrace --rerun-tasks` 通过。
- HUD 微调：
  - 底部到快捷栏的默认净距按 `HUD_HOTBAR_VERTICAL_GAP - hudYOffset` 从约 10px 调整到约 20px，整体上移 10px。
  - 快捷栏左侧安全距离从 4px 调整到 16px，避免能量条过于贴近快捷栏。
  - HUD 在左侧可用空隙内不再取 1/2 居中，而是取剩余空隙的 2/5 作为左边距，整体向左但仍保留边距。
  - 文本到能量条距离改为“当前语言最长 HUD 标签宽度 + 6px”，替代原固定 42px 文本列，中文环境下“查克拉”到条的间距约为 6px。
  - HUD 条高度按贴图切片 V 坐标 `0/10/20/30` 从 8px 调整为 10px，避免每条底部 2px 被裁掉；12px 行距保留 2px 条间间隔。
  - HUD 条宽度从 80px 调整为 82px，补足右侧被截断的 2px；内部满值填充宽度仍按左右各 1px 边框计算为 80px。
  - IDEA MCP `compileJava --stacktrace --rerun-tasks` 通过。
  - IDEA MCP `build --stacktrace` 通过。
- 文档补充：
  - 新增 `docs/new-features.md`，集中说明能量系统、HUD、低能量循环、兵粮丸、血脉倍率、ninja XP 和写轮眼进化等新增功能。
  - 文档补齐管理指令、配置范围、技术实现范围、验证状态和上线前验收重点，方便后续 runClient 实机验收与合并回 `2836` 前评估。
  - 新增文档后 IDEA MCP `build --stacktrace` 再次通过，Gradle 任务均为 up-to-date。

## 上线前剩余风险

- 尚未执行最新改动后的 `runClient` 冒烟测试，无法确认客户端 HUD 实际左上微调位置、条高和条宽是否完整显示、窄屏只渲染条逻辑、`hud.png` 三条切片视觉效果、查克拉同步包、低能量状态、兵粮丸改造、Mixin runtime 注入、虚拟瞳术槽进化和属性同步在游戏内全链路无误。
- `MixinPathwayPlayer`、`MixinItemMilitaryRationsPillFood`、`MixinProcedureChakraRegenerationOnPotionActiveTick` 编译通过，但仍必须在 runClient 日志中确认 Mixin apply 无 runtime 警告。
- `MixinProcedureSharinganHelmetTickEvent` 通过定点 redirect 禁用原版万花筒进化，编译通过但仍建议在 runClient 日志中确认无 Mixin apply 警告。
- `JutsuXpGainHandler` 使用独立伤害事件给当前手持忍术/八门增加经验，实际体验数值需要进游戏按配置调试。

## 与产品上线对比

当前状态是“可构建但未完成实机验收”。距离上线还差一次客户端冒烟验收：

1. 创建/进入世界后 HUD 显示三条能量，位于屏幕左侧与快捷栏之间，原版查克拉 HUD 不再显示。
2. `/soulenergy`、`/bodyenergy` 指令能读写并立即同步 HUD。
3. 击杀、死亡、攻击、受击、挖矿触发对应能量变化，静置时灵魂/肉体能量不自然下降。
4. 肉体能量属性加成在属性面板/实际战斗中生效。
5. 静止和睡觉不再触发原版查克拉自然恢复；查克拉低于阈值时只通过消耗灵魂/肉体能量恢复。
6. ninja XP 只通过新转化系统增长，不再通过原版战斗增长，也不造成灵魂/肉体能量自然流逝。
7. 灵魂能量低量时反胃并通过睡觉/静止恢复；肉体能量低量时三个负面效果并通过饱食度恢复。
8. 兵粮丸只恢复肉体能量，不再直接或通过查克拉再生药水恢复查克拉。
9. 因陀罗/阿修罗初始能量倍率和恢复倍率按配置生效。
10. 写轮眼按灵魂能量从 1 勾玉到永恒万花筒完整进化。
