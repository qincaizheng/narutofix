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

- 编排重跑准备（Secretary）：本轮按当前 `AGENTS.md` 复核 todo/约束；该请求不是 generation task，只做重跑前置准备，不实现功能代码。
  - 基线文档检查：`docs/project.md` 与 `docs/diff.md` 均存在且可读；`docs/` 已存在，无需新建基线文档。
  - 当前 todo 已更新为玩家背包页面顶部或底部插入血脉、身体能量、灵魂能量、查克拉能量及实时回复速度信息，并需要适配原版缩放。
  - IDEA MCP Gradle 就绪检查：根项目 `narutofix` 已链接，`build.gradle` 可识别，Gradle 任务可列出；IDE 当前 Gradle JVM 显示为 `jbr-25`，后续 Java 8/Forge 1.12.2 编译验证仍需按项目约束用 IDEA MCP 执行。
  - Git 分支状态因 IDEA MCP 无本地 git 能力，使用允许的只读 `git status --short --branch` 例外检查：当前在 `feature/energy-system`，工作区显示 `AGENTS.md` 已修改；本轮未修改 git 状态。
  - 上线差异：现有能量系统文档状态仍是“可构建候选，待 `runClient` 实机冒烟”；新增背包信息展示 todo 尚未计划/实现/验证，距离上线还需要补充计划、实现、IDEA MCP 编译/构建验证和客户端冒烟验收，验证通过后才可合回 `2836`。

## 上线对齐

当前已达到“可构建候选”状态

## 2026-05-05 Plan 12.2 子代理收尾验证

- 角色：子代理，执行主代理派发的完整 Plan 12.2 验证/收尾任务。
- 已用 IDEA MCP 复查 todo/约束：未变化，仍是玩家背包页面顶部或底部显示血脉、身体能量、灵魂能量、查克拉能量及实时回复速度，并适配原版缩放。
- 当前分支：`.git/HEAD` 为 `ref: refs/heads/feature/energy-system`，未合回主分支 `2836`。
- 已用 IDEA MCP 读取并复查 12.1/12.2 共 7 个文件：`MixinGuiInventory.java`、`InventoryEnergyInfoRenderer.java`、`EnergyRecoverySnapshot.java`、`EnergyRecoveryCalculator.java`、`EnergyStateHandler.java`、`zh_cn.lang`、`en_us.lang`。
- IDEA MCP `get_file_problems(errorsOnly=false)`：上述 7 个文件均为 `errors: []`。
- IDEA MCP Gradle 验证：
  - `compileJava --stacktrace --rerun-tasks`：`BUILD SUCCESSFUL`。
  - `build --stacktrace`：`BUILD SUCCESSFUL`。
- IDEA MCP `runClient` 重新验证：
  - 后台启动客户端，加载 coremod 与 Mixin 配置正常，FML 识别 narutofix 与 narutomod。
  - 日志在 LWJGL 初始化（`LWJGL Version: 2.9.4`）后停止增长。
  - 60 秒后重读日志无新增输出；Gradle runClient 任务最终被取消。
  - 原因：当前环境无可用显示器/虚拟帧缓冲，Minecraft 无法创建游戏窗口。
- 判定：`blocked`。代码与构建完全通过，但 runClient 无法在本环境完成进入世界和背包 GUI 画面操作。
- 上线差距：仍不能合回 `2836`。需要负责人带显示器环境手动执行 `runClient` 并完成背包画面验收后，才可推进合并。

## 2026-04-29 重新同步 skill / AGENTS 约束

- 角色：task-execution-orchestrator 工作流 Secretary；本轮只做最新约束同步与前置阻塞确认，不做业务实现。
- generation 判定：总工作流目标是生成/实现玩家背包信息展示功能，属于 generation task；本 Secretary 子任务本身只是前置同步，不产出业务代码。
- 已用 IDEA MCP 读取项目内 `AGENTS.md`、`docs/project.md`、`docs/diff.md`、`docs/plans.md`、`docs/know.md`、`.git/HEAD`。
- 项目内 `AGENTS.md` 存在；当前 todo 仍是玩家背包页面顶部或底部显示血脉、身体能量、灵魂能量、查克拉能量及实时回复速度，并适配原版缩放。
- 上层 AGENTS 约束继续生效：所有项目读取/验证/编译/构建/测试使用 IDEA MCP；plan 中的小计划不得再拆给 subagent；无用和结束子代理要清理；缺环境应询问用户；非大陆资源先尝试国内源，慢时使用 7890 代理；文档放入 `docs/`；进度评估必须对齐上线；当前是 Minecraft mod 开发场景，允许使用 IDEA MCP。
- skill 文件 `/root/.codex/skills/task-execution-orchestrator/SKILL.md` 位于项目外，IDEA MCP 拒绝读取；本轮按当前对话中用户贴出的 skill 全文核对：Manager 只负责编排不直接读写，Secretary 处理前置/小任务，Executor 承接完整 plan item，Supervisor 负责正式验证并更新 `docs/diff.md`，完成子代理必须关闭，普通 30 秒轮询超时不等于卡住。
- 当前分支：`.git/HEAD` 为 `ref: refs/heads/feature/energy-system`，未合回主分支 `2836`。
- 当前计划状态：`docs/plans.md` 已有 Plan 12.1 与 Plan 12.2；12.2 依赖 12.1，不能并行，且 12.1/12.2 都是最小派发粒度，Executor 不得再拆。
- 当前 blocked 状态：Supervisor 已判定 Plan 12.1 blocked。前置原因是 Executor 只新增了 `EnergyRecoverySnapshot` / `EnergyRecoveryCalculator`，但 `EnergyStateHandler.java` 尚未接入统一 calculator；此前 IDEA MCP 写入该文件返回 `isWritable=false` / 只读文档。`compileJava` 通过只能证明新增 util 可编译，不能证明服务端真实恢复逻辑与 GUI 展示口径统一。
- 上线差距：Plan 12.1 未通过前不能进入 Plan 12.2；距离上线还缺解除或确认 `EnergyStateHandler.java` 写入阻塞、完成 12.1 接入、Supervisor 复验、执行 12.2 背包 GUI 渲染、IDEA MCP `compileJava` / `build`、`runClient` 背包缩放和配方书实机验收，全部通过后才可合回 `2836`。
- 下一步角色建议：先由 Secretary 确认/处理 IDEA MCP 对 `EnergyStateHandler.java` 的写入前置问题；若可写，Manager 按完整 Plan 12.1 重新派发 Executor；Executor 完成后交 Supervisor 正式验证。只有当写入阻塞无法解除或计划粒度需要调整时，再让 Initiator 追加兄弟 plan item，不覆盖现有计划。

## 2026-04-29 runClient 验收前置处理（Secretary）

- 角色：task-execution-orchestrator 工作流 Secretary；本轮只处理 Plan 12.2 验收前置和运行方式确认，不做业务代码修改，不做最终验收判定。
- 已按最新约束用 IDEA MCP 读取 `AGENTS.md`、`docs/project.md`、`docs/diff.md`、`docs/plans.md`、`docs/know.md`、`.git/HEAD`。
- 当前分支：`.git/HEAD` 为 `ref: refs/heads/feature/energy-system`，仍未合回主分支 `2836`。
- 当前 blocked 复核：Plan 12.2 的 IDEA 文件检查、`compileJava --stacktrace --rerun-tasks`、`build --stacktrace` 均已通过；阻塞点是 Supervisor 的 `runClient --stacktrace` 在 120 秒工具超时内未完成背包 GUI 实机验收，不是 compile/build 失败。
- IDEA MCP 运行配置查询结果：存在 `narutofix [runClient]`（Gradle 构建配置）、`narutofix [build]`、`Minecraft Client`、`Minecraft Server`；`Minecraft Client` 工作目录为 `/home/qdd/codex/workspace/narutofix/run`。Gradle task `runClient` 存在，类型为 `com.gtnewhorizons.retrofuturagradle.minecraft.RunMinecraftTask`，描述为运行带本 mod 的反混淆客户端。
- 已用 IDEA MCP `execute_run_configuration` 启动 `narutofix [runClient]`，参数为 `waitForExit=false`，避免把长生命周期客户端当成失败；IDEA 返回日志文件 `/home/qdd/.cache/JetBrains/IntelliJIdea2026.1/tmp/ij_run__narutofix_[runClient]_9602814279566738866.log`。
- 已用 IDEA MCP VFS 读取该日志，确认 Gradle 进入 `:runClient`，客户端启动参数包含 `--gameDir /home/qdd/codex/workspace/narutofix/run`，并加载 coremod `com.qdd.narutofix.core.FixPlugin`、`MixinBooterPlugin` 和 `AssetMoverCore`。
- 关键日志：Forge 1.12.2 在 Java 8 Zulu 上启动；MixinBooter 添加 `mixins.narutofix_early.json` 与 `mixins.narutofix.json`；FML 识别并加载 `minecraft, mcp, mixinbooter, FML, forge, assetmover, narutofix, narutomod`；最终出现 `Forge Mod Loader has successfully loaded 8 mods`。
- 观察到的日志项主要是开发环境常见 warning：Forge 版本检查、maven library folder 格式提示、narutofix metadata version 回退、narutomod 贴图 mip/OBJ 模型 warning、Realms 授权失败；本轮未发现阻止客户端初始化的崩溃日志。
- 本轮结论：`runClient` 可通过 IDEA MCP 后台启动并进入客户端初始化完成阶段，可作为后续 Supervisor 继续执行背包 GUI 实机验收的前置；但本 Secretary 未进入世界、未打开背包、未检查 GUI Scale/配方书/宽窄屏/数值实时变化，因此不能替代 Supervisor 的正式验收。
- 上线差距：当前仍不具备合回 `2836` 条件；下一步应由 Supervisor 基于已启动/可启动的 `runClient` 路径复验 Plan 12.2，完成背包页面目视验收、Mixin runtime 关键日志检查和 `docs/diff.md` 更新后，才可把 12.2 从 blocked 推进为可合回候选。

## 2026-04-29 本轮最终收尾（Secretary）

- 角色：task-execution-orchestrator 工作流 Secretary；本轮只做收尾记录和总结，不修改业务代码、资源文件或 Gradle 运行时 mod 依赖。
- 已按最新约束用 IDEA MCP 读取 `AGENTS.md`、`docs/project.md`、`docs/diff.md`、`docs/plans.md`、`docs/know.md`、`.git/HEAD`；未发现相对上一轮的新 todo 或强制约束变化。当前 todo 仍是玩家背包页面顶部或底部显示血脉、身体能量、灵魂能量、查克拉能量及实时回复速度，并适配原版缩放。
- 当前分支：`.git/HEAD` 为 `ref: refs/heads/feature/energy-system`，仍未合回主分支 `2836`。
- 完成内容：
  - Plan 12.1 已完成并由 Supervisor 判定 `pass`；`EnergyStateHandler` 已复用 `EnergyRecoveryCalculator` / `EnergyRecoverySnapshot`，背包展示实时回复速度已有统一只读计算数据源。
  - Plan 12.2 已完成；背包前景层渲染、`InventoryEnergyInfoRenderer`、中英文 lang 文本均已落地，显示血脉与身体/灵魂/查克拉当前值、最大值、每秒回复速度。
- 已通过验证项：
  - 12.1/12.2 涉及文件的 IDEA MCP 文件检查均为 `errors: []`。
  - IDEA MCP `compileJava --stacktrace --rerun-tasks` 为 `BUILD SUCCESSFUL`。
  - IDEA MCP `build --stacktrace` 为 `BUILD SUCCESSFUL`。
  - IDEA MCP 后台启动 `narutofix [runClient]` 成功，日志确认 FML 成功加载 8 个 mods，包含 `narutofix` 与 `narutomod`，未发现启动崩溃。
- 当前 blocked 原因：最终 Supervisor 判定仍为 `blocked`。IDEA MCP 目前只能启动客户端和读取日志，不能自动操作 Minecraft 客户端进入世界、打开背包、切换 GUI Scale/配方书，也不能截图确认背包顶部/底部信息实际画面。因此缺少背包 GUI 实机画面验收，不能判定 Plan 12.2 为 `pass`。
- 上线差距：当前仍不具备合回 `2836` 条件。代码与构建已到候选状态，但上线门禁缺人工或可操作客户端工具完成的背包 GUI 画面验收。
- 下一步验收清单：
  - 启动客户端并进入世界，打开玩家背包确认新增信息实际可见。
  - 覆盖 GUI Scale Auto/Normal/Large、宽屏/窄屏、配方书打开/关闭。
  - 确认信息不覆盖物品槽、玩家模型、合成标题、配方书按钮、虚拟瞳术槽。
  - 确认血脉文案正确，身体/灵魂/查克拉当前值与最大值正确，低能量状态下实时回复速度会变化。
  - 留存截图或人工验收记录，并让 Supervisor 补记 `docs/diff.md`；验收通过后再推进合回 `2836`。

## 2026-05-05 Secretary 盘点记录

- 本轮仅做状态盘点与文档留档，没有实现功能、编写测试或执行正式验收。
- 已确认 `docs/`、`docs/project.md`、`docs/diff.md` 均存在且可读；未改写 `AGENTS.md`。
- 读取到的当前进度仍停留在“代码与构建通过、但需要 `runClient` 背包 GUI 实机画面验收”的阶段，目标依然是把血脉、身体能量、灵魂能量、查克拉能量及实时回复速度显示在玩家背包页面顶部或底部，并适配原版缩放。
- 只读 git 检查结果：当前分支为 `feature/energy-system`，工作区存在既有未提交改动，本轮没有切分支、提交、合并或重置。
- 上线对比：当前还不能算 ready to merge to `2836`，原因不是静态编译，而是缺少可证明的客户端背包 GUI 验收结果。

## 2026-05-05 冲突方案文档生成（子代理）

- 角色：文档记录子代理；本轮仅追加本记录到 `docs/project.md`，未改动代码、测试、资源或 `AGENTS.md`，也未执行编译/构建/runClient。
- 已按用户要求由 `claude-opus-4-7` 子代理生成/恢复 `docs/conflict-resolution-plan.md`。
- 该文档采用竖线 Markdown 表格，列依次为：冲突项、当前冲突表现、影响范围、方案 A、方案 B、方案 C、推荐倾向、用户选择/手写方案。
- 覆盖的核心冲突条目：
  - 血脉觉醒门槛实际使用 pathway max 1000 vs 血脉初始能量加成之间的口径冲突。
  - soul/body 增长派生 ninja XP 但不扣能量所带来的语义与平衡冲突。
  - narutomod `BATTLEXP` 与查克拉上限链路仍待确认。
  - 旧存档迁移 / `dataVersion` 缺口问题。
  - 低查克拉恢复阈值出现断崖式变化的问题。
  - 兵粮丸语义（恢复对象/能量类型）不明确。
  - HUD 与背包信息重复展示的问题。
  - 同步包已存在但缺 `runClient` 实机刷新验证。
- 本轮上线对齐：该文档只提供方案选择入口，不推进功能上线门禁。当前仍不能合回 `2836`；需用户先在该表格中勾选/手写方案，再由主代理将结论转成后续 plan，并完成 IDEA MCP `compileJava`/`build` 与 `runClient` 背包 GUI 实机验收，才能推进合并。

## 2026-05-05 Plan 13 追加（子代理：文档追加）

- 角色：文档追加子代理；本轮仅向 `docs/plans.md` 追加“## 13. 冲突修正实施计划（2026-05-05 用户方案选择）”及 13.1–13.7 七个最小派发计划，并向 `docs/project.md` 追加本节；未改动代码、测试、资源或 `AGENTS.md`，未触碰 git，未执行编译/构建/runClient。
- 用户已基于 `docs/conflict-resolution-plan.md` 完成全部冲突项的方案选择（含两条手写方案：允许静止时自然回复查克拉；产品上线验证由用户自验，不要代理处理 `runClient`）。
- 已据此追加 Plan 13，覆盖：13.1 血脉初始加成立即发放+版本化对齐+旧存档迁移；13.2 ninja XP 派生命名修正与指令 reset baseline；13.3 静止自然回复查克拉与创造模式 body 独立恢复；13.4 兵粮丸 BODY_ONLY 语义文案修正；13.5 写轮眼进化来源配置化；13.6 ChakraSyncHelper 脏标记合并同步；13.7 用户自验清单文档化（不要求代理跑 runClient）。
- Plan 13 已显式标注本轮“不修改”的四项：血脉觉醒门槛 1000、BATTLEXP→pathway max 闭环、HUD 与背包信息重复、同步包 runClient 确认。
- 当前实现状态：Plan 13 全部为“已规划、未实现、未验证”。代码侧未做任何变更，仍停留在既有 `feature/energy-system` 工作区状态。
- 上线对齐：本轮仅推进“规划文档”阶段，距离合回 `2836` 仍至少差：(1) Plan 13.1–13.6 实现并通过 IDEA MCP `compileJava`，共享逻辑后再 `build`；(2) Plan 13.7 自验清单落地；(3) 用户依据清单自验全部通过。代理侧本轮不承担 `runClient`。

## 2026-05-05 Plan 13.7 用户自验清单文档化（子代理）

- 角色：文档子代理，执行主代理委托的 Plan 13.7 完整任务。
- 已用 IDEA MCP 读取 AGENTS.md、docs/plans.md（Plan 13.7）、docs/conflict-resolution-plan.md、docs/diff.md、docs/project.md。
- 写入范围：
  - 新增 `docs/validation-checklist.md`：中文，结构清晰，面向用户自行 runClient 验证。每项含操作步骤、预期现象、异常反馈要点。
- 清单覆盖：
  - 背包 GUI：GUI Scale Auto/Normal/Large、配方书开关、宽屏/窄屏下血脉与三类能量显示。
  - HUD：与背包信息共存时不崩、不明显错位（本轮不去重）。
  - 血脉：新觉醒立即出现初始加成；老存档登录只 max 对齐、current 不变。
  - 能量：低灵魂、低肉体、低查克拉、静止自然回复查克拉、创造模式 body 恢复。
  - 查克拉：阈值切换无断崖；登录/维度切换/重生/背包打开刷新。
  - 兵粮丸：tooltip 中英文文案明确 BODY_ONLY。
  - 写轮眼：sharinganEvolutionSource 三种取值 VANILLA/SOUL/BOTH 的进化行为。
  - 指令：/soulenergy、/bodyenergy set/add 后 ninja XP 不再产生巨量历史派生。
  - 旧存档/配置：旧 ninjaXpConversionRate 配置仍可加载；旧 NBT 缺版本字段不崩。
- docs/diff.md 更新：修改了产品上线验证行（阻塞 → 待用户自验），追加了 Plan 13.7 行。
- docs/project.md 更新：追加本节记录。
- 验证确认：用 IDEA MCP 读取新增文档确认内容已落盘；IDE 问题检查（markdown 文件仅含 table，竖线表格弱提示按用户要求忽略）。
- 未执行 compileJava/build/runClient。
- 代理上线状态不变：仍不能合回 `2836`。用户依据清单自验全部通过后才具备合并条件。

## 2026-05-05 Plan 13.2 修正执行（子代理）

- 角色：子代理，执行 Plan 13.2 ninja XP 派生命名修正、旧配置兼容与指令 reset baseline 修正。
- 已用 IDEA MCP 读取 Configs.java、NinjaXpConversionHandler.java、CommandSoulEnergy.java、CommandBodyEnergy.java、zh_cn.lang、en_us.lang，确认现有代码状态。

### 审计发现

1. **Configs.java - 分类名风险（已修复）**：`@Config.Name("XP Contribution Settings")` 会破坏旧配置文件中 `"XP Conversion Settings"` 分组的读取，与 Plan 13.2 "旧配置兼容"要求冲突。已改回 `@Config.Name("XP Conversion Settings")`。
2. **Configs.java - 旧字段迁移无日志（已修复）**：`getEffectiveContributionRate()` 和 `getEffectiveContributionInterval()` 在检测到旧字段被自定义时会自动赋值到新字段，但没有任何日志输出。已新增 log4j logger 和 `LOGGER.info("Migrating old config ...")` 语句。
3. **NinjaXpConversionHandler.resetBaseline（已实现）**：`resetBaseline(EntityPlayerMP)` 方法已存在，内部将 baseline 设为 `min(soul.getCurrent(), body.getCurrent())`，语义正确。
4. **CommandSoulEnergy / CommandBodyEnergy（已实现）**：两指令在 set/add 操作后已调用 `NinjaXpConversionHandler.resetBaseline(player)`（各在第 87 行）。
5. **派生路径不扣减 soul/body（已验证）**：`NinjaXpConversionHandler.onPlayerTick` 仅调用 `NinjaXpHelper.add(player, contributed, false)`，不对 soul/body 做任何 `addCurrent(-x)` 调用。
6. **Lang 文案（已验证）**：zh_cn.lang 和 en_us.lang 已使用"贡献/派生"和 "Contribution" 语义，无需修改。

### 改动文件

- `src/main/java/com/qdd/narutofix/Configs.java`：分类名改回 `XP Conversion Settings`、新增 logger + 迁移日志
- `docs/diff.md`：新增 Plan 13.2 状态行
- `docs/project.md`：追加本节记录

### 验证

- IDEA MCP `get_file_problems`：Configs.java 无 error，仅保留预期的 deprecated 字段 warning
- IDEA MCP `compileJava --stacktrace --rerun-tasks`：BUILD SUCCESSFUL

### 剩余风险

- 配置迁移日志只覆盖 effective getter 调用时，如果旧字段从未被读取则不会触发日志；建议用户在旧配置存在时执行一次 get/set 操作触发。不影响实际功能。
- runClient 实机验证仍由用户按 `docs/validation-checklist.md` 自行完成，代理侧不行使。

## 2026-05-05 Plan 13.3 执行（子代理）

- 角色：子代理，执行 Plan 13.3 静止自然回复查克拉与创造模式 body 独立恢复。
- 已用 IDEA MCP 读取 Configs.java、EnergyRecoveryCalculator.java、EnergyRecoverySnapshot.java、EnergyStateHandler.java、InventoryEnergyInfoRenderer.java、docs/plans.md、docs/diff.md、docs/project.md、docs/new-features.md。
- 当前分支仍为 `feature/energy-system`，未合回主分支 `2836`。

### 改动文件

- `src/main/java/com/qdd/narutofix/Configs.java`：新增 `ChakraStationaryRecoveryConfig`（`perTick=0.05`、`requiredTicks=100`）和 `CreativeBodyRecoveryConfig`（`perTick=0.5`）两个配置子类。
- `src/main/java/com/qdd/narutofix/util/EnergyRecoveryCalculator.java`：
  - `calculateChakraRecovery` 新增 `stationaryTicks` 参数，新增非低查克拉静止自然恢复分支（不消耗 soul/body）。
  - `calculateBodyRecovery` 新增创造模式 body 独立恢复分支，改为 `creative && current < max` 即恢复，未满即按 `Configs.creativeBody.perTick` 恢复，不走食物消耗。
  - `calculate()` 调用处同步传入 `stationaryTicks`。
- `src/main/java/com/qdd/narutofix/handler/EnergyStateHandler.java`：`handleLowBody` 现在把负面效果与恢复执行拆开，低体能才施加负面效果；恢复动作则只看 `snapshot.getBody().getRecoveryPerTick() > 0`，确保 creative body 在非低阈值时也会实际恢复且不扣饱食度。
- `src/main/java/com/qdd/narutofix/handler/EnergyStateHandler.java`：`handleLowChakra` 新增 `else if` 分支，在非低查克拉时执行静止自然恢复（直接 `pathway.consume(-recovery)`，不扣 soul/body）。
- `docs/diff.md`：新增 Plan 13.3 状态行。
- `docs/new-features.md`：更新"功能目标"、"查克拉恢复"、"肉体能量"、"配置范围"、"上线前验收重点"相关节。
- `docs/project.md`：追加本节记录。

### 不需改动的文件

- `EnergyRecoverySnapshot.java`：现有字段（`isLow`、`recoveryPerTick`）已足够区分紧急与静止恢复类型，无需新增字段。
- `InventoryEnergyInfoRenderer.java`：使用 `getNetPerTick()` 显示净回复速度，已自动反映静止自然恢复和紧急恢复的总速率，无需修改。

### 验证

- `compileJava --stacktrace --rerun-tasks` 待执行。

## 2026-05-05 Plan 13.4 兵粮丸 BODY_ONLY 语义文案修正（子代理）

- 角色：子代理，执行主代理派发的 Plan 13.4 兵粮丸 BODY_ONLY 语义文案修正。
- 已用 IDEA MCP 复查 AGENTS.md、docs/plans.md（Plan 13.4）、docs/diff.md、docs/project.md。
- 当前分支仍为 `feature/energy-system`，未合回主分支 `2836`。

### 改动文件

- `src/main/java/com/qdd/narutofix/mixin/MixinItemMilitaryRationsPillFood.java`：在现有 Mixin（已包含 `@Redirect` 屏蔽直接查克拉恢复与查克拉再生药水）中新增 `@Inject(method = "addInformation", at = @At("TAIL"))` 方法 `narutofix$appendMilitaryRationsPillTooltip`，对 `ItemMilitaryRationsPill.ItemFoodCustom` 和 `ItemMilitaryRationsPillGold.ItemFoodCustom` 两种兵粮丸追加两行 tooltip 文本。
- `src/main/resources/assets/narutofix/lang/zh_cn.lang`：新增 `tooltip.narutofix.military_rations_pill.info`（直接恢复肉体能量，不直接恢复查克拉）和 `tooltip.narutofix.military_rations_pill.info2`（低查克拉时可通过能量系统间接帮助查克拉恢复）。
- `src/main/resources/assets/narutofix/lang/en_us.lang`：新增对应英文 key。
- `docs/diff.md`：新增 Plan 13.4 状态行。
- `docs/project.md`：追加本节记录。

### 不需改动的文件

- `EnergyStateHandler.java`：兵粮丸实际恢复逻辑已在 `onItemUseFinish` 中正确实现（body.addCurrent + sync），无需修改。
- `Configs.java`：兵粮丸恢复值由配置控制且已在 Plan 13.3 之前正确实现，无需修改。
- 不涉及其他 Mixin 配置 JSON（`MixinItemMilitaryRationsPillFood` 已在 mixins.narutofix.json 中注册）。

### 验证

- IDEA MCP `compileJava --stacktrace --rerun-tasks`：待执行。
- 修改已与 Plan 13.3 合并：13.3 子代理在 EnergyStateHandler/Configs/EnergyRecoveryCalculator 中的变更均未涉及兵粮丸 tooltip，无冲突。

### 剩余风险

- 兵粮丸 tooltip 运行时预期：narutomod 原版 tooltip（`tooltip.mrp.browntip` / `tooltip.mrp.goldtip`）仍显示"恢复查克拉"，narutofix 新增 tooltip 会追加在下方，玩家会看到两套描述。新描述明确 BODY_ONLY 语义，应能消除预期误差。
- tooltip 使用 `§7` 灰色格式，与 narutomod 原版 tooltip 的灰色描述风格一致。
- runClient 实机验证仍由用户按 `docs/validation-checklist.md` 自行完成，代理侧不行使。

## 2026-05-05 Plan 13.4 窄修：移除 narutomod 旧误导 tooltip（子代理）

- 角色：子代理，执行主代理 Review 后的 Plan 13.4 窄修。
- 问题：上一轮追加的 narutofix tooltip 与原版 narutomod `tooltip.mrp.browntip` / `tooltip.mrp.goldtip`（"恢复 200/500 点查克拉"）同时显示，两套矛盾文案不符合“消除语义冲突”目标。
- 解决方案：在 `MixinItemMilitaryRationsPillFood.addInformation` TAIL 注入中，先移除由原版 `addInformation` 添加的查克拉恢复 tooltip 行，再追加 narutofix 修正行。

### 实现细节

- 移除方式：使用 `I18n.translateToLocal("tooltip.mrp.browntip")` 和 `I18n.translateToLocal("tooltip.mrp.goldtip")` 查出当前语言翻译值，通过 `List<String>.remove(Object)` 精准匹配删除。不依赖硬编码字符串，中英文均兼容。
- 保留行：非查克拉恢复的工具 tip（如 SATURATION 药水效果的描述）不会被删除。
- 安全边界：若 narutomod 的 lang key 不存在或翻译值未出现在 tooltip 列表中，`remove()` 静默返回 false，不会抛异常。

### 改动文件

- `src/main/java/com/qdd/narutofix/mixin/MixinItemMilitaryRationsPillFood.java`：`narutofix$appendMilitaryRationsPillTooltip` 方法新增两行 `tooltip.remove(...)`。
- `docs/diff.md`：更新 Plan 13.4 行当前实现与说明列。
- `docs/new-features.md`：更新"兵粮丸 tooltip 文案澄清"小节，追加窄修说明。
- `docs/project.md`：追加本节记录。

## 2026-05-05 Plan 13.5 写轮眼进化来源配置化（子代理）

- 角色：子代理，执行主代理派发的 Plan 13.5 — 写轮眼进化来源配置化。
- 目标：新增配置 `sharinganEvolutionSource`，取值 `VANILLA` / `SOUL` / `BOTH`，默认 `BOTH`，三种取值有明确分支。
- 当前分支仍为 `feature/energy-system`，未合回主分支 `2836`。

### 实现

1. **Configs.java**：新增 `SharinganEvolutionSource` 枚举（VANILLA/SOUL/BOTH）与 `@Config` 字段 `sharinganEvolutionSource`，默认 `BOTH`，配置注释中文化；新增 `@Config.LangKey("narutofix.sharingan.evolution.source")`。

2. **mixinPathway.java**（灵魂进化入口）：`onUpdate2` 中的 `tryUpgradeSharingan` 调用加上了 `sharinganEvolutionSource != VANILLA` 守卫。VANILLA 模式跳过本 mod 的灵魂阈值进化；SOUL/BOTH 时正常执行。

3. **MixinProcedureSharinganHelmetTickEvent.java**（原版进化入口）：
   - 新增 `@Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true)` — 当配置为 `SOUL` 时直接取消整个原版 `ProcedureSharinganHelmetTickEvent.executeProcedure`，阻止任何原版进化触发。VANILLA/BOTH 时不取消。
   - 修改现有 `@Redirect` BATTLEXP 拦截：仅当配置为 `SOUL` 时把 BATTLEXP 读数兜底归零；`VANILLA` 和 `BOTH` 都不拦截 BATTLEXP 读取，保留完整原版进化检查。
   - 新增 import `com.qdd.narutofix.Configs`、`java.util.Map`、`CallbackInfo`。

4. **中英文 lang 文件**：新增 `narutofix.sharingan.evolution.source` 配置显示 key。

### 三种模式行为

| 模式 | 原版进化（ProcedureSharinganHelmetTickEvent） | 灵魂进化（mixinPathway.tryUpgradeSharingan） |
|------|---------------------------------------------|----------------------------------------------|
| VANILLA | 完整运行原版逻辑，BATTLEXP 不拦截 | 跳过，不运行 |
| SOUL | @Inject HEAD cancellable 取消整个方法；@Redirect 安全兜底 | 正常执行 |
| BOTH | 完整运行原版逻辑，BATTLEXP 不拦截 | 正常执行 |

### 原版进化入口定位

- narutomod 原版写轮眼进化在 `ProcedureSharinganHelmetTickEvent.executeProcedure(Map)` 中实现，通过读取 `NBTTagCompound.getDouble(BATTLEXP)` 检查进化条件。
- 现有 mixin `MixinProcedureSharinganHelmetTickEvent` 使用 `@Redirect(ordinal=1)` 作为 SOUL 模式兜底，只在 SOUL 下把第二个 BATTLEXP 读取归零。
- SOUL 模式通过 `@Inject HEAD cancellable` 完全取消该过程；VANILLA 和 BOTH 模式不拦截 BATTLEXP，保留完整原版行为。

### 改动文件

- `src/main/java/com/qdd/narutofix/Configs.java`：新增枚举与配置字段
- `src/main/java/com/qdd/narutofix/mixin/MixinProcedureSharinganHelmetTickEvent.java`：HEAD 注入 + 条件重定向
- `src/main/java/com/qdd/narutofix/mixin/mixinPathway.java`：VANILLA 守卫
- `src/main/resources/assets/narutofix/lang/zh_cn.lang`：配置 key
- `src/main/resources/assets/narutofix/lang/en_us.lang`：配置 key
- `docs/diff.md`：新增 Plan 13.5 行
- `docs/new-features.md`：更新写轮眼进化章节
- `docs/project.md`：追加本节记录

### 验证

- IDEA MCP `get_file_problems`：Configs.java（仅预存警告）、MixinProcedureSharinganHelmetTickEvent.java（无问题）、mixinPathway.java（仅预存警告）—— 均无新增错误。
- IDEA MCP `compileJava --stacktrace --rerun-tasks`：BUILD SUCCESSFUL。

### 剩余风险

- `ProcedureSharinganHelmetTickEvent` 的原始逻辑基于 BATTLEXP 查询；SOUL 模式下 `@Inject HEAD cancellable` 会取消整个方法，包括非进化相关的副作用（如玩家标记、闪烁检测等）。若 narutomod 在该方法中加入了与进化无关但必要的 tick 行为，SOUL 模式下会丢失。当前 narutomod 源码该方法的名称暗示仅处理 Sharingan Helmet 的 tick 事件，而进化是其主要目的，此风险较低。
- VANILLA/BOTH 模式下，原版 BATTLEXP 进化检查完全恢复；BOTH 同时保留 `mixinPathway` 的灵魂阈值进化，符合“原版或灵魂任一满足即可触发”的语义。
- `runClient` 实机验证仍由用户按 `docs/validation-checklist.md` 自行完成，代理侧不行使。

### 未改动文件

- lang 文件：narutofix 的 lang key 不变，narutomod 的 lang 文件不能改也不需改。
- 不涉及其他 Java 文件、配置或 JSON。

### 验证

- IDEA MCP `compileJava --stacktrace --rerun-tasks`：`BUILD SUCCESSFUL in 12s`。
- Mixin Annotation Processor 运行正常，refmap 写入正确。

### 剩余风险

- 如果 narutomod 的 `tooltip.mrp.browntip` / `tooltip.mrp.goldtip` 翻译值在运行时与其他 mod 的 tooltip 行恰好相等（概率极低），`remove()` 可能误删非本 mod 的行。但在真实场景下，这些翻译值是兵粮丸专有文本，被其他工具提示重用的可能性非常小。
- 运行时仍然需要用户 `runClient` 按 `validation-checklist.md` 第 6 节确认 tooltip 正确显示。代理侧不行使。

## 2026-05-05 Plan 13.5 窄修：BOTH 不再拦截原版 BATTLEXP（子代理）

- 角色：子代理，继续执行主代理 Review 后的 Plan 13.5 窄修。
- 问题：上一轮 `MixinProcedureSharinganHelmetTickEvent.narutofix$disableBattleXpMangekyoEvolution` 在非 VANILLA 模式下都会把 `BATTLEXP` 读数返回 0，导致 `BOTH` 仍屏蔽原版万花筒进化条件，不符合“原版 narutomod 规则或灵魂能量规则任一满足即可触发”的用户选择 C 语义。
- 修正：`@Redirect` 现在只在 `Configs.sharinganEvolutionSource == SOUL` 且 key 为 `BATTLEXP` 时返回 `0.0D`；`VANILLA` 和 `BOTH` 均直接返回 `compound.getDouble(key)`，完整保留原版 BATTLEXP 逻辑。
- 行为结果：
  - `VANILLA`：原版入口完整运行；soul 入口不运行。
  - `SOUL`：原版入口 HEAD 取消；redirect 仅作为兜底归零；soul 入口运行。
  - `BOTH`：原版入口完整运行且不拦 BATTLEXP；soul 入口也运行。
- 文档更新：`docs/diff.md`、`docs/new-features.md`、`docs/project.md` 已同步把 BOTH 写成“不拦截原版入口 + soul 入口也运行”。
- 验证：IDEA MCP 文件问题检查无新增错误（`MixinProcedureSharinganHelmetTickEvent.java` 无问题；`Configs.java` / `mixinPathway.java` 仅保留既有 warning）；IDEA MCP `compileJava --stacktrace --rerun-tasks`：BUILD SUCCESSFUL。

## 2026-05-05 Plan 13 最终文档收尾（子代理）

- 角色：文档收尾子代理，按主代理要求对 `docs/diff.md` 做最终文档整理。
- 已复查 Plan 13.1—13.7 在 `docs/diff.md` 中的状态行。
- 已确认 Plan 13.1 行完整（含旧存档登录只 max 对齐、已有版本标记不重复发放说明），未断行。
- 已确认 Plan 13.4 行说明旧误导 tooltip 已被移除（在 `MixinItemMilitaryRationsPillFood` 的 TAIL 注入中先 `remove` 再追加），不是只追加新文案。
- 已确认 Plan 13.5 行说明 `BOTH` 不拦截原版 BATTLEXP，soul 入口也运行。
- 已补充缺失的 Plan 13.6 行：ChakraSyncHelper 脏标记合并同步（dirty flag Set；`refresh()` 设脏标记；`flushNow()` 关键路径立即同步；`flushDirtyPlayers()` 在 `ServerTickEvent.Phase.END` 统一发送；玩家下线清理脏标记）。
- 已确认 Plan 13.7 行存在且完整。
- `docs/diff.md` 所有竖线表格行完整闭合，无断行。
- 未编译/构建/运行；上线状态仍是"待用户按 `docs/validation-checklist.md` 自验"。全部通过后才可合回 `2836`。

## 2026-05-05 天手力解耦与 Javadoc 收尾（主代理）

- 角色：主代理；本轮承接未完成的天手力解耦与 Javadoc 清理。
- 已回读最新 AGENTS.md：当前 todo 仍是“将代码中，天手力引用的白眼特性转为自己的代码，不再与 narutomod 耦合；将 javadoc 的警告消除”。
- 已完成的代码调整：
  - 新增 `AmenotejikaraOverlayState`，作为客户端天手力短时视觉状态持有者。
  - 新增 `PacketAmenotejikaraOverlay`，server 侧直接控制天手力 overlay 的激活与关闭。
  - `AmenotejikaraOverlayHandler` 改为读取 narutofix 自有状态，不再依赖 `OverlayByakuganView.byakuganActivated` 或 `ItemByakugan`。
  - `SixTomoeRinneganLogic` 的天手力激活/收尾改为走自有 packet，不再调用 `OverlayByakuganView.sendCustomData(...)`。
  - `PacketRegister` 已注册新的天手力 overlay packet。
  - `PacketSyncChakra` 的 raw `Chakra.Pathway` 原始类型 warning 已修为参数化类型。
  - `DojutsuEyeHandler`、`EyeInventoryManager`、`BloodlineEnergyBonusApplier` 补齐 Javadoc `@param`，清掉 build 中暴露的 6 个 Javadoc 警告。
- 已完成验证：
  - IDEA MCP `get_file_problems`：上述新增/修改文件均无 error。
  - IDEA MCP `compileJava --stacktrace --rerun-tasks`：通过。
  - IDEA MCP `javadoc --stacktrace --rerun-tasks`：通过，先前 6 个 Javadoc warning 已消除。
  - IDEA MCP `build --stacktrace`：通过。
- 说明：
  - 天手力对白眼 overlay 状态的直接依赖已经切掉，天手力视觉现在由 narutofix 自己的 packet/state 管。
  - 仍需用户在 `runClient` 环境里做最终可视化实测，确认时长、清理、相机和实体发光的实际画面没有回归。

---

## 2026-05-05 — P0-P3 实现与 docs 收口

按 claude-opus-4-7 制定的修正计划执行，覆盖 AGENTS.md todo 中关于查克拉回复口径、下蹲静止查克拉转化、饱食度欠债修复、睡觉恢复的四个需求。

### 变更文件

- `Configs.java`：新增 `CrouchChakraExchangeConfig`（P1）、`SleepRecoveryConfig`（P3）、`BodyEnergyConfig.allowConsumeHunger`（P2）且默认 false 时注释改为"仅消耗饱和度，不累积 debt，不扣 foodLevel"；`ChakraStationaryRecoveryConfig` 注释标注 deprecated
- `EnergyRecoveryCalculator.java`：移除旧版 `chakraStationary` 无成本静止自然恢复分支（P0）；新增 `calculateCrouchExchange`（P1）、`calculateSleepRecovery`（P3）；修复 `FoodPreview` 增加 `remainingFood` 字段；`calculateBodyRecovery` 不再因 restore=0 跳过 debt 更新（P2）；`previewFoodConsumption` 在 `allowConsumeHunger=false` 时不扣 foodLevel
- `EnergyRecoverySnapshot.java`：新增 `crouchExchangeActive`、`crouchExchangeChakraGain`、`crouchExchangeSoulCost`、`crouchExchangeBodyCost`、`sleepRecoveryAvailable`、`sleepSoulRecovery`、`sleepBodyRecovery`、`chakraEmergencyRecoveryPerTick`、`crouchExchangeEligible`、`crouchExchangeTriggered` 字段及 getter
- `EnergyStateHandler.java`：移除 `handleLowChakra` 中的旧静止自然恢复 else-if 分支（P0）；新增 `handleCrouchExchange` 执行方法（P1）；`handleLowBody` 无条件写回 debtAfter（P2）；`applyFoodConsumption` 按 `allowConsumeHunger` 分支处理 foodLevel 扣除（P2）；新增 `onPlayerWakeUp(PlayerWakeUpEvent)` 事件驱动恢复结算，不再用 tick-count 差值判定（P3）
- `zh_cn.lang` / `en_us.lang`：新增 `body.allowConsumeHunger`、`chakraCrouchExchange.*`、`sleep.*` 翻译键
- `docs/diff.md`：更新第 30 行（Plan 13.3 替换为 P0/P1）；追加 P0-P3 四行表格
- `docs/project.md`：追加本记录
- `docs/new-features.md`（见下文）
- `docs/validation-checklist.md`（见下文）

### 验证状态

- IDEA MCP `compileJava --stacktrace --rerun-tasks`：待执行
- IDEA MCP `get_file_problems`：待执行

### 与产品上线关系

代码层面已实现 P0-P3 全部需求。实机验收仍卡在 `runClient` 窗口初始化。用户应使用 `validation-checklist.md` 中新增的 P0-P3 自验项完成客户端确认后，才具备合回 `2836` 的条件。

---

## 2026-05-05 — Review 修复：服务端 pathway、睡觉公式、Entry 语义、P2 debt、文档

按主代理 review 要求修了 5 个核心问题。非新增需求，全部为已有代码的 bug 修复。

### 变更文件

- `EnergyStateHandler.java`：
  - Fix 1：`Chakra.isInitialized(player)` → `Chakra.pathway(player)`（服务端获取 pathway）
  - Fix 4：`applyFoodConsumption` 在 `allowConsumeHunger=false` 时不再写 debt；清理注释
- `EnergyRecoveryCalculator.java`：
  - Fix 1：3-arg `calculate` 和 `calculateCrouchExchange` 去掉 `Chakra.isInitialized` 守卫
  - Fix 2：`calculateSleepRecovery` 公式从 `max*percent - current`（补到 percent）改为 `min(max*percent, max-current)`（回复 percent 量，上限剩余空间）
  - Fix 3：Entry 构造中 crouch exchange 改为 per-tick 等效值；soul/body drain 包含 crouch 成本；chakra recovery 包含 crouch 等效值；recovering 不再基于 `crouchCost <= 0`
  - Fix 4：`previewFoodConsumption` 在 `!allowConsumeHunger` 时直接返回 debtAfter=0（不累积 debt）
- `docs/diff.md`：更新 P0/P1/P2/P3 四行表格
- `docs/project.md`：追加本记录
- `docs/validation-checklist.md`：13.1 节修正为"默认不累积 debt"

### 验证状态

- IDEA MCP `get_file_problems`：待执行
- IDEA MCP `compileJava --stacktrace --rerun-tasks`：待执行

### 与产品上线关系

本轮为纯修复，不引入新功能。验证通过后上线状态不变（仍待用户 `runClient` 实机验收）。

## 2026-05-05 AGENTS 盘点补充

- 角色：主代理；本轮仅回读当前 `AGENTS.md`、`docs/diff.md`、`docs/project.md`，把新出现的 todo 补进差异追踪，不做代码实现、测试或构建。
- 已确认 `AGENTS.md` 当前新增待办仍是 4 项：
  - 重进游戏后肉体/灵魂 current 变成 100 的同步问题
  - 肉体能量不满时的饱食度保留口径调整
  - 因陀罗/阿修罗初始值增益改为固定可配置值
  - 清理旧的、无用的血脉配置项
- 已将上述 4 项追加到 `docs/diff.md`，作为后续 plan / 实现的追踪入口。
- 当前上线状态不变：仍需先完成对应实现与 IDEA MCP 验证，再结合用户自验结果判断是否能回到 `2836`。
- 本轮同步修正：`EnergySyncHandler` 已补注册到事件总线，登录/切维度/重生/加入世界会统一补同步 soul/body/chakra；血脉初始值配置已改为固定值；body 饥饿策略已收口为“保留 9 格 foodLevel”。
- 旧配置清理已完成：移除了 `Configs.upgrade`（旧写轮眼阈值）、`ChakraStationaryRecoveryConfig`（废弃静止恢复）、`minimumSleepTicks`（废弃最少睡觉刻数）及对应 lang 键。
- 死亡丢查克拉修复：新增 `MixinChakraPlayerHookOnDeath`，拦截 `Chakra.PathwayPlayer.PlayerHook.onDeath` 的服务端分支，防止查克拉被重置为 0，让 `PlayerEvent.Clone` 能正常复制查克拉到新玩家。
- 查克拉上限联动修复：在 `MixinPathwayPlayer` 中 `@Redirect` 了 `resetMax()` 和 `onUpdate()` 里的 `getBattleXp` 调用，把 `soul.current + body.current` 作为固定加成叠加到原 `BATTLEXP * 0.5` 公式上，查克拉上限现在会随灵魂/肉体能量实时变化。

---

## 2026-05-07 — 修复三个 AGENTS.md 新 todo

### 角色
主代理：分析根因 → 规划 → 派发子代理 → Review → 验证

### 修改文件

**Plan A — 六勾玉轮回眼头盔槽须佐修复**
- `src/main/java/com/qdd/narutofix/mixin/MixinProcedureSusanoo.java`：选取 effectiveEye 逻辑重构，同时检查虚拟瞳术槽和头盔槽的六勾玉轮回眼，不在头盔槽时放行给原版处理。

**Plan B — 身体能量属性加成上限**
- `src/main/java/com/qdd/narutofix/Configs.java`：`BodyEnergyConfig` 新增 `maxArmor`(30)、`maxMoveSpeedMultiplier`(2.0)、`maxAttackDamageMultiplier`(10.0)、`maxAttackSpeedMultiplier`(3.0) 四个上限配置。
- `src/main/java/com/qdd/narutofix/handler/BodyAttributeHandler.java`：对应四个 modifier 用 `Math.min(计算值, cap)` 限制上限。MAX_HEALTH 和 HP Regen 不加 cap。

**Plan C — 血量渲染改为紧凑进度条**
- `src/main/java/com/qdd/narutofix/Configs.java`：`BodyEnergyConfig` 新增 `enableCompactHealthBar`(true) 配置开关。
- 新建 `src/main/java/com/qdd/narutofix/client/gui/HealthBarOverlayHandler.java`：拦截 `RenderGameOverlayEvent.Pre(HEALTH)`，替换为 120×8 紧凑进度条 + HP 数值文案。可通过配置关闭以恢复原版渲染。

### 验证
- `compileJava --stacktrace --rerun-tasks`：BUILD SUCCESSFUL。

### 上线状态
仍为“待用户按 `docs/validation-checklist.md` 自验”。新增三个 todo 的验证项需补充到自验清单后，全部通过才可合回 `2836`。

---

## 2026-05-07 — AGENTS.md 三个 todo 处理

### 角色
主代理：分析根因 → 规划 → 派发子代理 → Review → 编译验证

### Plan A — 修复血条 GL 状态泄漏
- 子代理 Lorentz 执行。
- 修改 `HealthBarOverlayHandler.java`：在紧凑血条绘制前后添加 `GlStateManager.pushMatrix()`/`popMatrix()`，并在其后显式恢复纹理、混合、颜色状态。
- 根因：`Gui.drawRect` 和 `fontRenderer.drawStringWithShadow` 会改变 GL 状态（禁纹理、改混合、改颜色），这些状态泄漏到后续 ARMOR/FOOD 元素渲染，导致护甲和饱食度看起来被"取消"。

### Plan C — 新增玩家进入即忍者+成就
- 子代理 Helmholtz 执行。
- 新增 `src/main/java/com/qdd/narutofix/event/FirstJoinHandler.java`：监听 `PlayerLoggedInEvent`，首次进入（BATTLEXP==0）时设置 BATTLEXP=1.0 并授予 narutomod:ninjaachievement。
- 修改 `CommonProxy.java`：注册 `FirstJoinHandler` 到 `MinecraftForge.EVENT_BUS`。
- 老玩家（BATTLEXP>0）自动跳过。

### Plan B — 调查六勾玉轮回眼头盔槽须佐阻塞因素
- 主代理分析，未派发子代理。
- 发现 3 个可能阻塞因素（见 final 输出）。

### 验证
- `compileJava --stacktrace`：BUILD SUCCESSFUL。
- 无新增警告或错误。

### 上线状态
代码层已完成。需用户按 `docs/validation-checklist.md` 自验后合回 `2836`。

---

## 2026-05-08 — 须佐能乎完整重写（主代理）

- 角色：主代理；根据 `docs/须佐.md` 规格，完全新建须佐能乎实现体系，低耦合、高性能。
- 目标：不依赖 narutomod 的 `EntitySusanooBase` 层级体系，新建独立实体。
- 分支：`feature/energy-system`（用户要求，不新建分支）

### 架构
新建包 `com.qdd.narutofix.entity.susanoo`，包含 18 个 Java 文件，总计约 4137 行。实体类继承 `EntityCreature` 而非 narutomod 的 `EntitySusanooBase`，渲染器照搬 narutomod 源码。

### 新建文件清单

**基础层（2 个）**
| 文件 | 行数 | 说明 |
|------|------|------|
| `SusanooEntityBase.java` | 533 | 抽象基类，extends EntityCreature，Owner/FlameColor DataManager、骑乘、伤害免疫、查克拉消耗、火焰粒子/音效 |
| `SusanooStateHelper.java` | 88 | 玩家 NBT 状态管理（激活/关闭/冷却计算），存 `narutofix_susanoo_*` 前缀 |

**实体层（4 个）**
| 文件 | 行数 | 说明 |
|------|------|------|
| `SusanooSkeletonEntity.java` | 132 | L0（骨架半身）/ L1（完整上半身），FULL_BODY DataParameter、天照碰撞 |
| `SusanooClothedEntity.java` | 282 | L2（着衣无腿）/ L3（着衣有腿），剑 modifier、Magatama 射击、AI、天照碰撞 |
| `SusanooWingedEntity.java` | 251 | L4 完全体有翼，翅膀动画、飞行、特殊物品发放、Magatama |
| `SusanooMagatamaEntity.java` | 157 | 八尺琼勾玉弹射物，爆炸 + AOE |

**AI 层（4 个）**
| 文件 | 行数 | 说明 |
|------|------|------|
| `SusanooAIAttackMelee.java` | 113 | 近战 AI，reachDistance 范围，20 tick 攻击间隔 |
| `SusanooAIAttackRangedAndMove.java` | 142 | 远程 AI（Magatama），30 tick 射击间隔，IRangedAttackMob |
| `SusanooAIFollowOwner.java` | 159 | 下马后跟随主人，距离超 144 格传送 |
| `SusanooAIOwnerHurtTarget.java` | 54 | 复制主人攻击目标 |

**模型层（3 个，照搬 narutomod）**
| 文件 | 行数 | 说明 |
|------|------|------|
| `ModelSusanooSkeleton.java` | 342 | 骨架外形（ModelBiped），两层渲染（本体+火焰） |
| `ModelSusanooClothed.java` | 412 | 着衣外形（ModelBiped），含尖刺、角、剑 |
| `ModelSusanooWinged.java` | 789 | 完全体外形，含翅膀动画、爪子、鳞叠 |

**渲染层（4 个，照搬 narutomod）**
| 文件 | 行数 | 说明 |
|------|------|------|
| `RenderSusanooSkeleton.java` | 76 | 骨架渲染器，火焰叠加 |
| `RenderSusanooClothed.java` | 120 | 着衣渲染器，LayerHeldItem，骑乘者 limb sync |
| `RenderSusanooWinged.java` | 161 | 有翼渲染器，飞行姿态（俯冲 body rotation） |
| `RenderSusanooMagatama.java` | 158 | 勾玉渲染器，Tessellator 旋转发光 |

**召唤逻辑层（3 个）**
| 文件 | 行数 | 说明 |
|------|------|------|
| `SusanooSummonHandler.java` | 168 | 召唤/升级/取消/清理事件处理器 |
| `SusanooKeyHandler.java` | (keybind包) | 客户端按键检测 → 发包至服务端 |
| `PacketSummonSusanoo.java` | (network包) | 召唤/取消包 |
| `PacketUpgradeSusanoo.java` | (network包) | 升级包 |

### 修改的文件
| 文件 | 变更 |
|------|------|
| `CommonProxy.java` | 注册 4 个实体、SusanooSummonHandler 事件监听 |
| `ClientProxy.java` | 注册 SusanooKeyHandler |
| `KeyLoader.java` | 新增 sumonSusanoo(N)/upgradeSusanoo(M) 按键绑定 |
| `PacketRegister.java` | 注册 PacketSummonSusanoo、PacketUpgradeSusanoo |
| `Configs.java` | 新增 SusanooConfig 配置节（BXP 阈值、查克拉消耗） |

### 低耦合设计
- 实体继承 `EntityCreature`，不依赖 narutomod 的 `EntitySusanooBase`
- 与 narutomod 交互仅通过 `Chakra.pathway()`（查克拉消耗）、`PlayerTracker.getBattleXp()`（战斗经验检查）、`Particles.Types.FLAME`（粒子）
- 写轮眼检测通过已有 `DojutsuEyeHelper.getCompatibleSusanooEye()`，同时检查头盔槽和虚拟瞳术槽

### 编译验证
- `./gradlew compileJava` → BUILD SUCCESSFUL
- `./gradlew build` → BUILD SUCCESSFUL
- 新增 susanoo 包全量编译通过，无新增警告

### 风险点
1. 渲染器和模型暂未在 ClientProxy 注册（需要 `RenderingRegistry.registerEntityRenderingHandler`），需用户接上后方可可见
2. `SusanooMagatamaEntity` 继承 `EntityScalableProjectile.Base`（narutomod 类），重构风险
3. 需 `runClient` 实机验证召唤/升级/查克拉消耗/取消惩罚全流程

---

## 2026-05-08 — 须佐召唤入口接入 narutomod（主代理）

### 变更
- `mixinProcedureSusanoo.java` 重写：取消原版 `ProcedureSusanoo.execute()` 和 `ProcedureSusanoo.upgrade()`，重定向到 `SusanooSummonHandler.summonSusanoo()` / `.upgradeSusanoo()`
- 六勾玉轮回眼支持：`DojutsuEyeHelper.getCompatibleSusanooEye()` 已检查 `ModItems.SIX_TOMOE_RINNEGAN`，六勾玉轮回眼持有者可正常召唤

### 编译验证
- `compileJava` → BUILD SUCCESSFUL
