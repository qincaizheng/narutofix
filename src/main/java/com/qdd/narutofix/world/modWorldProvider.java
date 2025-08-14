package com.qdd.narutofix.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.border.WorldBorder;
import com.qdd.narutofix.Configs;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


public class modWorldProvider extends WorldProviderSurface {
    public static int DIMID = 4;
    public static final boolean NETHER_TYPE = false;
    public static DimensionType dtype;
    public static void preInit(FMLPreInitializationEvent event) {
        if (DimensionManager.isDimensionRegistered(DIMID)) {
            DIMID = DimensionManager.getNextFreeDimId();
            System.err.println("Dimension ID for dimension Ninja Realm is already registered. Falling back to ID: " + DIMID);
        }

        dtype = DimensionType.register("ninjarealm", "_ninjarealm", DIMID, modWorldProvider.class, true);
        DimensionManager.registerDimension(DIMID, dtype);
    }

    public modWorldProvider() {
    }

    public DimensionType getDimensionType() {
        return modWorldProvider.dtype;
    }

    public long getSeed(){return Long.parseLong(Configs.ninjarealmseed);}

    @Override
    public WorldBorder createWorldBorder() {
        WorldBorder border = new WorldBorder();
        border.setCenter(0, 0);        // 边界中心点
        border.setDamageAmount(0.0);   // 边界外伤害（设为0不伤害玩家）
        border.setDamageBuffer(0);     // 伤害缓冲距离
        border.setWarningDistance(0);  // 警告距离
        border.setSize(10000);           // 边界大小（100x100）
        return border;
    }

}


