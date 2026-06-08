package com.qdd.narutofix.mixin;

import com.qdd.narutofix.Configs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.BossInfoServer;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.entity.EntityTailedBeast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Mixin(EntityTailedBeast.Base.class)
public abstract class MixinEntityTailedBeastBase {

    @Shadow(remap = false)
    private BossInfoServer bossInfo;

    @Unique
    private final Map<UUID, Long> narutofix$lastBroadcastTick = new HashMap<>();

    @Redirect(method = "addTrackingPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BossInfoServer;addPlayer(Lnet/minecraft/entity/player/EntityPlayerMP;)V"), remap = false)
    private void narutofix$skipBossBarAddOnTracking(BossInfoServer bi, EntityPlayerMP player) {
    }

    @Inject(method = "onUpdate", at = @At("TAIL"), remap = false)
    private void narutofix$onUpdateBossBar(CallbackInfo ci) {
        EntityTailedBeast.Base self = (EntityTailedBeast.Base) (Object) this;
        if (self.world.isRemote) {
            return;
        }

        EntityBijuManager bm = self.getBijuManager();
        if (bm == null || bm.getJinchurikiPlayer() != null) {
            return;
        }

        int range = Configs.tails.bossBarRange;
        long currentTick = self.world.getTotalWorldTime();
        double rangeSq = (double) range * (double) range;
        int selfDim = self.world.provider.getDimension();

        // Remove out-of-range / wrong-dimension players
        for (EntityPlayerMP player : new HashSet<>(this.bossInfo.getPlayers())) {
            if (!player.isEntityAlive()
                || player.dimension != selfDim
                || player.getDistanceSq(self) > rangeSq) {
                this.bossInfo.removePlayer(player);
            }
        }

        // Add in-range players (same dimension)
        for (EntityPlayer playerEntity : self.world.playerEntities) {
            if (!(playerEntity instanceof EntityPlayerMP)) {
                continue;
            }
            EntityPlayerMP player = (EntityPlayerMP) playerEntity;
            if (!this.bossInfo.getPlayers().contains(player)
                && player.dimension == selfDim
                && player.isEntityAlive()
                && player.getDistanceSq(self) <= rangeSq) {
                this.bossInfo.addPlayer(player);
            }
        }

        // Coordinate broadcast at interval
        int broadcastInterval = Configs.tails.coordinateBroadcastInterval;
        if (broadcastInterval > 0 && !this.bossInfo.getPlayers().isEmpty()) {
            int px = (int) Math.floor(self.posX);
            int py = (int) Math.floor(self.posY);
            int pz = (int) Math.floor(self.posZ);
            String name = self.getName();
            String msg = I18n.translateToLocalFormatted("chattext.tailbeast.coordinates", name, px, py, pz);

            for (EntityPlayerMP player : new HashSet<>(this.bossInfo.getPlayers())) {
                long lastTick = this.narutofix$lastBroadcastTick.getOrDefault(player.getUniqueID(), 0L);
                if (currentTick - lastTick >= (long) broadcastInterval) {
                    player.sendStatusMessage(new TextComponentString(msg), true);
                    this.narutofix$lastBroadcastTick.put(player.getUniqueID(), currentTick);
                }
            }
        }
    }
}
