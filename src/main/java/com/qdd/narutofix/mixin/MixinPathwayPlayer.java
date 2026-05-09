package com.qdd.narutofix.mixin;

import com.qdd.narutofix.cap.body.BodyEnergyDataProvider;
import com.qdd.narutofix.cap.body.IBodyEnergyData;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.narutomod.Chakra;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chakra.PathwayPlayer.class)
public abstract class MixinPathwayPlayer extends Chakra.Pathway<EntityPlayer> {

    protected MixinPathwayPlayer(EntityPlayer userIn) {
        super(userIn);
    }

    @Inject(method = "resetMax", at = @At("HEAD"), cancellable = true, remap = false)
    private void narutofix$resetMaxWithSoulBody(CallbackInfo ci) {
        ci.cancel();
        if (this.user == null) {
            this.setMax(Math.max(this.getMax(), 1.0D));
            return;
        }
        ISoulEnergyData soul = SoulEnergyDataProvider.get(this.user);
        IBodyEnergyData body = BodyEnergyDataProvider.get(this.user);
        if (soul != null && body != null) {
            double chakra = Math.min(soul.getMax(), body.getMax() / 4.0);
            this.setMax(Math.max(chakra, 1.0D));
        } else {
            this.setMax(Math.max(this.getMax(), 1.0D));
        }
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE",
            target = "Lnet/narutomod/Chakra$PathwayPlayer;sendToClient()V"), remap = false)
    private void narutofix$reapplySoulBodyBonus(CallbackInfo ci) {
        if (this.user == null || this.user.world.isRemote) return;
        ISoulEnergyData soul = SoulEnergyDataProvider.get(this.user);
        IBodyEnergyData body = BodyEnergyDataProvider.get(this.user);
        if (soul == null || body == null) {
            if (this.getMax() <= 0) {
                this.setMax(1.0D);
            }
            return;
        }
        // onUpdate at line 269 set max to BATTLEXP*0.5, overwriting our resetMax mixin.
        // Overwrite with chakra directly instead of stacking.
        double chakra = Math.min(soul.getMax(), body.getMax() / 4.0);
        this.setMax(Math.max(chakra, 1.0D));
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/narutomod/Chakra$PathwayPlayer;consume(F)V"), remap = false)
    private void narutofix$skipSleepChakraRegen(Chakra.PathwayPlayer self, float amount) {}

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/narutomod/Chakra$PathwayPlayer;consume(F)V"), remap = false)
    private void narutofix$skipIdleChakraRegen(Chakra.PathwayPlayer self, float amount) {}
}
