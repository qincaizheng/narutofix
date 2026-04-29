# Project Progress

## 2026-04-28

- 按 `AGENTS.md` 约束在 `2836` 基础上创建并使用开发分支 `feature/energy-system`。
- 已拉取 `AHZNB/naruto_mod` 的 `0.3.1-beta` 分支作为本地参考源码：
  - 路径：`/root/workspace/naruto_mod_0.3.1`
  - commit：`2c0dec2a48da09f206a447413a9847e1dc533d4d`
  - Gradle 运行时依赖未删除，仍保留 `curse.maven:ahznbs-naruto-mod-831157:5967126`。
- 已生成并维护：
  - `docs/plans.md`：开发计划
  - `docs/know.md`：naruto_mod 0.3.1-beta 源码研究记录
  - `docs/diff.md`：当前 todo 与上线差异
- 已完成实现：
  - 灵魂能量与肉体能量配置项
  - 灵魂/肉体能量 Forge capability、NBT 持久化、死亡/克隆继承
  - 服务端到客户端同步包与登录/维度切换/重生同步
  - 击杀/死亡/攻击/受击/挖矿事件驱动能量变化
  - 肉体能量属性加成：生命上限、护甲、回复、移速、攻击力、攻速
  - 灵魂能量影响查克拉恢复、忍术经验倍率、忍术蓄力速度
  - 禁用原版战斗忍者经验增长和原版 ninja XP 生命加成
  - 新增 min(灵魂当前值, 肉体当前值) 到 ninja XP 的自动转化
  - 新增灵魂、肉体、查克拉 HUD，并屏蔽原版 chakra HUD
  - 新增 `/soulenergy` 与 `/bodyenergy` 指令
  - 写轮眼 1/2/3 勾玉、万花筒、永恒万花筒按灵魂能量阈值进化，覆盖头盔栏和虚拟瞳术槽
- IDEA MCP 验证：
  - `compileJava --stacktrace`：通过
  - `build --stacktrace`：通过
- 本轮补充：
  - `Configs.java` 中所有 `@Config.Comment` 已统一翻译为中文。
  - `en_us.lang` / `zh_cn.lang` 已补齐新增配置与 HUD/提示文本翻译。
  - IDEA MCP `compileJava --stacktrace` 再次通过。
  - 能量 HUD 已改用 `assets/narutofix/textures/gui/hud.png`，三条分别渲染灵魂能量、肉体能量、查克拉。
  - 能量 HUD 默认放在屏幕左侧与快捷栏左边缘之间，左侧显示文本，右侧显示数值。
  - HUD 贴图调整后 IDEA MCP `compileJava --stacktrace` 与 `build --stacktrace` 均通过。
  - 根据最新 `AGENTS.md` todo 修复 HUD 绝对定位：能量条默认放在屏幕左侧与快捷栏左边缘之间，配置项改为水平/垂直微调；进度填充改为在贴图边框内部绘制，避免当前值条偏下。
  - 移除 soul/body 因 ninja XP 周期转化造成的自然流逝：现在只在两种能量的较小当前值发生增长时按增长差值增加 ninja XP，不再自动扣减两种能量。
  - 新增 `PacketSyncChakra` 与 `ChakraSyncHelper`，在灵魂/肉体能量变化、忍者经验变化、登录/维度切换/重生、指令修改后刷新客户端查克拉 HUD 状态。
  - 灵魂能量和肉体能量新增可配置的初始当前值，默认均为 `100.0`；初始上限仍默认 `100.0`，旧存档上限异常时按配置回填。
  - 最新 todo 调整后 IDEA MCP `compileJava --stacktrace` 与 `build --stacktrace` 均通过。

## 2026-04-29

- 按本轮更新后的 `AGENTS.md` todo 继续在 `feature/energy-system` 分支实现能量系统补充：
  - HUD 三条能量条默认上移，继续锚定在屏幕左侧与原版快捷栏左边缘之间。
  - HUD 现在会计算左边框到快捷栏的可用宽度；宽度足够时渲染左侧文本、右侧数值，宽度不足时自动只渲染三条进度条。
  - 新增 `EnergyStateHandler`：统一处理低查克拉、低灵魂能量、低肉体能量状态。
  - 查克拉低于可配置阈值时，同时消耗灵魂能量与肉体能量恢复查克拉，阈值、单 tick 消耗量、两种能量到查克拉的转化率均可配置。
  - 灵魂能量低于可配置阈值时施加反胃；睡觉或连续静止后缓慢恢复，睡觉速度、静止速度、静止所需 tick 均可配置。
  - 肉体能量低于可配置阈值时施加缓慢、挖掘疲劳、虚弱；消耗饱和度/饱食度恢复肉体能量，消耗速度与转化率可配置。
  - 普通/金色兵粮丸改为恢复肉体能量；通过 Mixin 禁用其直接恢复查克拉和查克拉再生药水的间接恢复路径。
  - 因陀罗血脉会按配置提高初始灵魂能量上限/当前值，阿修罗血脉会按配置提高初始肉体能量上限/当前值；对应低能量恢复速度倍率也可配置。
  - 原版 `Chakra.PathwayPlayer.onUpdate()` 的睡觉和静止查克拉自然恢复通过 Mixin 禁用，不再绕过新的低查克拉能量转化逻辑。
- IDEA MCP 验证：
  - `compileJava --stacktrace --rerun-tasks`：通过。
  - `build --stacktrace --rerun-tasks`：通过（包含 `compileJava`、资源处理、jar、reobf、javadocJar、spotlessCheck）。
- HUD 位置微调：
  - 根据原有 HUD 几何计算，底部到快捷栏的默认净距由约 10px 调整为约 20px，整体上移 10px。
  - 快捷栏左侧安全距离由 4px 调整为 16px，并将 HUD 在左侧空隙内的位置从居中改为使用剩余空隙的 2/5 作为左边距，使整体向左移动但不贴边。
  - 文本列改为按当前语言最长标签宽度动态计算，并只额外保留 6px 到能量条，替代原固定 42px 文本列。
- IDEA MCP 验证：
  - HUD 微调后 `compileJava --stacktrace`：通过。
  - HUD 微调后 `build --stacktrace`：通过。
- HUD 条高/条宽修复：
  - 依据贴图切片 V 坐标 `0/10/20/30` 的 10px 分段，将 HUD 条高度从 8px 调整为 10px，避免每条底部 2px 被裁掉。
  - 保留 12px 行距，因此三条之间仍有 2px 间隔；总高度从 32px 变为 34px，整体顶部随公式自动上移 2px，底部快捷栏净距仍维持约 20px。
  - 根据右侧缺失 2px 的视觉结果，将 HUD 条宽度从 80px 调整为 82px；内部填充仍按左右各 1px 边框计算，满值填充宽度随之为 80px。
- IDEA MCP 验证：
  - HUD 条高/条宽修复后 `compileJava --stacktrace --rerun-tasks`：通过。
  - HUD 条高/条宽修复后 `build --stacktrace`：通过。
- 本轮文档补充：
  - 新增 `docs/new-features.md`，作为新增功能说明文档，覆盖玩家可见变化、配置范围、管理指令、技术闭环、验证状态和上线前验收重点。
  - 本轮只新增说明文档和进度记录，不改变 Java 或资源逻辑；上线状态仍是“可构建候选，待 runClient 实机冒烟验收”。
  - 新增文档后 IDEA MCP `build --stacktrace` 再次通过，Gradle 任务均为 up-to-date。

## 上线对齐

当前已达到“可构建候选”状态：源码编译与完整 Gradle build 均通过，HUD/能量自然流逝/查克拉同步/初始值配置/低能量循环/兵粮丸改造/血脉倍率已按最新 todo 调整；HUD 又按可计算的快捷栏间距、文本宽度和贴图 10px 切片高度做了微调。仍需 `runClient` 实机冒烟测试。上线前必须确认 HUD 渲染位置与贴图切片、静置/睡觉不自然恢复查克拉、低查克拉消耗两种能量恢复、低灵魂/低肉体 debuff 与恢复、兵粮丸只恢复肉体能量、指令与事件后查克拉同步、属性加成、忍者 XP 转化、写轮眼进化在实际客户端流程中行为正确。
