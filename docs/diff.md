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

## 上线前剩余风险

- 尚未执行 `runClient` 冒烟测试，无法确认客户端 HUD 实际位置、Mixin runtime 注入、虚拟瞳术槽进化和属性同步在游戏内全链路无误。
- `MixinProcedureSharinganHelmetTickEvent` 通过定点 redirect 禁用原版万花筒进化，编译通过但仍建议在 runClient 日志中确认无 Mixin apply 警告。
- `JutsuXpGainHandler` 使用独立伤害事件给当前手持忍术/八门增加经验，实际体验数值需要进游戏按配置调试。

## 与产品上线对比

当前状态是“可构建但未实机验收”。距离上线还差一次客户端冒烟验收：

1. 创建/进入世界后 HUD 显示三条能量，原版查克拉 HUD 不再显示。
2. `/soulenergy`、`/bodyenergy` 指令能读写并立即同步 HUD。
3. 击杀、死亡、攻击、受击、挖矿触发对应能量变化。
4. 肉体能量属性加成在属性面板/实际战斗中生效。
5. 灵魂能量提升查克拉恢复、忍术经验、蓄力速度。
6. ninja XP 只通过新转化系统增长，不再通过原版战斗增长。
7. 写轮眼按灵魂能量从 1 勾玉到永恒万花筒完整进化。
