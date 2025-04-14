package com.qdd.narutofix.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class modWorldProvider extends WorldProvider {
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

    public void init() {
        this.biomeProvider = new BiomeProviderSingle(Biomes.PLAINS);
        this.world.setSeaLevel(64);
        this.nether = false;
    }

    public DimensionType getDimensionType() {
        return modWorldProvider.dtype;
    }

    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float par1, float par2) {
        return new Vec3d((double)0.0F, (double)0.0F, (double)0.0F);
    }

    public IChunkGenerator createChunkGenerator() {
        FlatGeneratorInfo flat = new FlatGeneratorInfo();
        flat.getFlatLayers().add(new FlatLayerInfo(1, Blocks.BEDROCK));
        flat.getFlatLayers().add(new FlatLayerInfo(2, Blocks.DIRT));
        flat.getFlatLayers().add(new FlatLayerInfo(1, Blocks.GRASS));

        return new ChunkGeneratorFlat(
                world,                  // 世界对象
                world.getSeed(),        // 种子
                false,                  // 是否生成结构
                flat.toString()         // 超平坦配置
        );
    }

    public boolean isSurfaceWorld() {
        return false;
    }

    public boolean canRespawnHere() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int par1, int par2) {
        return false;
    }

    public WorldProvider.WorldSleepResult canSleepAt(EntityPlayer player, BlockPos pos) {
        return WorldSleepResult.ALLOW;
    }

    protected void generateLightBrightnessTable() {
        float f = 0.12F;

        for(int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float)i / 15.0F;
            this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
        }

    }
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

    public boolean doesWaterVaporize() {
        return false;
    }

    public BlockPos getSpawnPoint() {
        WorldInfo info = this.world.getWorldInfo();
        new BlockPos(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ());
        BlockPos pos = new BlockPos(info.getSpawnX(), 4, info.getSpawnZ());
        this.setSpawnPoint(pos);
        return pos;
    }

    public void onPlayerAdded(EntityPlayerMP entity) {
        entity.sendPlayerAbilities();
    }

    public void onPlayerRemoved(EntityPlayerMP player) {
        player.sendPlayerAbilities();
    }


}


