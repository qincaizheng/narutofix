package com.qdd.narutofix.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.narutomod.Chakra;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 拦截 Chakra.PathwayPlayer.PlayerHook.onDeath，防止死亡时查克拉被重置为 0。
 * <p>
 * 原版 onDeath 在服务端直接调用 p.set(0) 然后把 player 从 playerMap 中移除，
 * 导致 clone 事件时查克拉数据完全丢失，复活后查克拉变成 0。
 * 本 Mixin 直接取消整个 onDeath 的服务端执行分支，
 * 让 playerMap 中的查克拉保持在死亡前的真实值，
 * 这样 PlayerEvent.Clone 的 LOWEST 处理句柄能正常复制查克拉到新玩家。
 * </p>
 */
@Mixin(Chakra.PathwayPlayer.PlayerHook.class)
public class MixinChakraPlayerHookOnDeath {

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true, remap = false)
    private void narutofix$keepChakraOnDeath(LivingDeathEvent event, CallbackInfo ci) {
        if (event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote) {
            ci.cancel();
        }
    }
}
