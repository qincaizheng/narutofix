package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.narutomod.Chakra;
import net.narutomod.PlayerTracker;
import net.narutomod.item.ItemByakugan;
import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.procedure.ProcedureHakkeKusho;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Map;

@Mixin(ProcedureHakkeKusho.class)
public abstract class MixinProcedureHakkeKusho {
    private static final double XP_REQUIRED = 500.0D;

    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$useVirtualByakugan(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        ItemStack virtualEye = DojutsuEyeHelper.getVirtualEye(player);
        if (virtualEye.isEmpty() || virtualEye.getItem() != ItemByakugan.helmet || DojutsuEyeHelper.getHeadEye(player).getItem() == ItemByakugan.helmet) {
            return;
        }

        if (!player.isCreative() && (PlayerTracker.getBattleXp(player) < XP_REQUIRED || !ProcedureUtils.isOriginalOwner(player, virtualEye))) {
            ci.cancel();
            return;
        }

        boolean isPressed = Boolean.TRUE.equals(dependencies.get("is_pressed"));
        int pressDuration = ProcedureAirPunch.getPressDuration(player);
        Chakra.Pathway pathway = Chakra.pathway(player);
        if (!isPressed && pressDuration > 0) {
            ProcedureSync.SwingMainArm.send(player);
            entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
                    net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:HakkeKusho")),
                    SoundCategory.PLAYERS, 1.0F, 1.0F);
            pathway.consume(ItemByakugan.getKushoChakraUsage(player) * pressDuration);
        }
        if (pathway.getAmount() >= ItemByakugan.getKushoChakraUsage(player) * (pressDuration + 1)) {
            getKusho().execute(isPressed, player);
        }
        ci.cancel();
    }

    private static ProcedureHakkeKusho.AirPunch getKusho() {
        try {
            Field field = ProcedureHakkeKusho.class.getDeclaredField("KUSHO");
            field.setAccessible(true);
            return (ProcedureHakkeKusho.AirPunch) field.get(null);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to access Hakke Kusho instance", exception);
        }
    }
}