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

## 上线对齐

当前已达到“可构建候选”状态：源码编译、完整 Gradle build、翻译补齐后的再次编译均通过，但尚未在 `runClient` 中做实机冒烟测试。上线前必须确认 HUD 渲染、指令、能量同步、属性加成、忍者 XP 转化、写轮眼进化在实际客户端流程中行为正确。
