package com.qdd.narutofix.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.qdd.narutofix.Configs;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.narutomod.SpawnTailedBeasts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SpawnTailedBeasts.class)
public class mixinSpawnTailedBeasts {
    @Unique
    private World world;

    @Inject(method = "onWorldTick",at = @At("HEAD"),remap = false)
    public void monWorldTick(TickEvent.WorldTickEvent event, CallbackInfo ci) {
        if (world==null) {
            this.world = event.world;
        }
    }

    @ModifyVariable(method = "onWorldTick",at = @At("STORE"),remap = false)
    public BlockPos injected(BlockPos pos) {
        if (world==null) return pos;
        Random random = new Random();
        int r = random.nextInt(Configs.distance);
        int x = world.getSpawnPoint().getX() + (random.nextBoolean() ? -1 : 1) * r;
        int z = world.getSpawnPoint().getZ() + (random.nextBoolean() ? -1 : 1) * (Configs.distance-r);
        return new BlockPos(x, 0.0F, z);
    }
}
