package com.qdd.narutofix.cap.awakening;

import net.minecraftforge.common.capabilities.CapabilityManager;

public final class PlayerAwakeningCapabilityHandler {
    private PlayerAwakeningCapabilityHandler() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IPlayerAwakeningData.class, new PlayerAwakeningDataStorage(), PlayerAwakeningData::new);
    }
}