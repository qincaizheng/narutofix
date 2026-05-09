package com.qdd.narutofix.util;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.awakening.Bloodline;
import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import com.qdd.narutofix.cap.body.BodyEnergyDataProvider;
import com.qdd.narutofix.cap.body.IBodyEnergyData;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import com.qdd.narutofix.network.PacketSyncBodyEnergy;
import com.qdd.narutofix.network.PacketSyncSoulEnergy;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * 血脉初始能量加成统一入口。
 * <p>
 * Plan 13.1：实现血脉初始加成立即发放、版本化对齐与旧存档迁移。
 * 两个准入路径：
 * <ul>
 *   <li>{@link #applyForNewAwakening} — 觉醒事件中立即调用，全额发放 max + current 加成。</li>
 *   <li>{@link #applyFallback} — EnergyStateHandler tick 兜底，仅做 max 对齐。</li>
 * </ul>
 */
public final class BloodlineEnergyBonusApplier {

    /**
     * 当前数据版本，用于区分旧存档与新存档。
     * 递增此值可触发旧存档 max 对齐迁移。
     */
    public static final int CURRENT_VERSION = 1;

    private BloodlineEnergyBonusApplier() {
    }

    /**
     * 玩家新觉醒血脉时立即调用。
     * 全额发放：max 至少提升至 targetMax，current 至少提升至 targetCurrent。
     * 随后写入 bloodlineAppliedVersion = CURRENT_VERSION。
     *
     * @param player 新觉醒血脉的玩家
     * @param bloodline 本次觉醒的血脉
     */
    public static void applyForNewAwakening(EntityPlayerMP player, Bloodline bloodline) {
        if (player == null || bloodline == null) {
            return;
        }
        ISoulEnergyData soul = SoulEnergyDataProvider.get(player);
        IBodyEnergyData body = BodyEnergyDataProvider.get(player);
        if (soul == null || body == null) {
            return;
        }

        boolean changed = false;
        if (bloodline == Bloodline.INDRA) {
            changed = applyNewSoulBonus(soul);
        } else if (bloodline == Bloodline.ASURA) {
            changed = applyNewBodyBonus(body);
        }

        if (changed) {
            sync(player);
        }
    }

    /**
     * 兜底对齐入口，在 EnergyStateHandler tick 中调用。
     * <p>
     * 行为：
     * <ul>
     *   <li>bloodlineAppliedVersion &lt; CURRENT_VERSION（旧存档）：仅将 max 提升至 targetMax，不触及 current。</li>
     *   <li>bloodlineAppliedVersion &gt;= CURRENT_VERSION（已应用）：仍检查 max 是否低于 targetMax，低于则对齐。</li>
     * </ul>
     * 注意：不得在此路径发放初始 current 加成，避免老玩家二次受益。
     *
     * @param player 需要做旧存档兜底对齐的玩家
     */
    public static void applyFallback(EntityPlayerMP player) {
        IPlayerAwakeningData awakening = PlayerAwakeningDataProvider.get(player);
        if (awakening == null) {
            return;
        }
        ISoulEnergyData soul = SoulEnergyDataProvider.get(player);
        IBodyEnergyData body = BodyEnergyDataProvider.get(player);
        if (soul == null || body == null) {
            return;
        }

        boolean changed = false;
        if (awakening.hasBloodline(Bloodline.INDRA)) {
            changed |= applySoulFallback(soul);
        }
        if (awakening.hasBloodline(Bloodline.ASURA)) {
            changed |= applyBodyFallback(body);
        }

        if (changed) {
            sync(player);
        }
    }

    // ========== 灵魂能量（因陀罗） ==========

    private static boolean applyNewSoulBonus(ISoulEnergyData soul) {
        double targetMax = Configs.soul.indraInitialSoulBonusMax;
        double targetCurrent = Configs.soul.indraInitialSoulBonusCurrent;

        boolean changed = false;
        if (soul.getMax() < targetMax) {
            soul.setMax(targetMax);
            changed = true;
        }
        if (soul.getCurrent() < targetCurrent) {
            soul.setCurrent(targetCurrent);
            changed = true;
        }
        if (soul.getBloodlineAppliedVersion() != CURRENT_VERSION) {
            soul.setBloodlineAppliedVersion(CURRENT_VERSION);
            changed = true;
        }
        if (soul.getDataVersion() != CURRENT_VERSION) {
            soul.setDataVersion(CURRENT_VERSION);
        }
        return changed;
    }

    private static boolean applySoulFallback(ISoulEnergyData soul) {
        int appliedVersion = soul.getBloodlineAppliedVersion();
        double targetMax = Configs.soul.indraInitialSoulBonusMax;

        boolean changed = false;
        if (appliedVersion < CURRENT_VERSION) {
            if (soul.getMax() < targetMax) {
                soul.setMax(targetMax);
            }
            soul.setBloodlineAppliedVersion(CURRENT_VERSION);
            changed = true;
        } else if (soul.getMax() < targetMax) {
            soul.setMax(targetMax);
            changed = true;
        }

        if (soul.getDataVersion() != CURRENT_VERSION) {
            soul.setDataVersion(CURRENT_VERSION);
        }
        return changed;
    }

    // ========== 肉体能量（阿修罗） ==========

    private static boolean applyNewBodyBonus(IBodyEnergyData body) {
        double targetMax = Configs.body.asuraInitialBodyBonusMax;
        double targetCurrent = Configs.body.asuraInitialBodyBonusCurrent;

        boolean changed = false;
        if (body.getMax() < targetMax) {
            body.setMax(targetMax);
            changed = true;
        }
        if (body.getCurrent() < targetCurrent) {
            body.setCurrent(targetCurrent);
            changed = true;
        }
        if (body.getBloodlineAppliedVersion() != CURRENT_VERSION) {
            body.setBloodlineAppliedVersion(CURRENT_VERSION);
            changed = true;
        }
        if (body.getDataVersion() != CURRENT_VERSION) {
            body.setDataVersion(CURRENT_VERSION);
        }
        return changed;
    }

    private static boolean applyBodyFallback(IBodyEnergyData body) {
        int appliedVersion = body.getBloodlineAppliedVersion();
        double targetMax = Configs.body.asuraInitialBodyBonusMax;

        boolean changed = false;
        if (appliedVersion < CURRENT_VERSION) {
            if (body.getMax() < targetMax) {
                body.setMax(targetMax);
            }
            body.setBloodlineAppliedVersion(CURRENT_VERSION);
            changed = true;
        } else if (body.getMax() < targetMax) {
            body.setMax(targetMax);
            changed = true;
        }

        if (body.getDataVersion() != CURRENT_VERSION) {
            body.setDataVersion(CURRENT_VERSION);
        }
        return changed;
    }

    // ========== 同步 ==========

    private static void sync(EntityPlayerMP player) {
        PacketSyncSoulEnergy.sync(player);
        PacketSyncBodyEnergy.sync(player);
        ChakraSyncHelper.refresh(player);
    }
}
