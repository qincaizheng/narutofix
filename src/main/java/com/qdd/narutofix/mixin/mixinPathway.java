package com.qdd.narutofix.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import com.qdd.narutofix.Configs;
import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import com.qdd.narutofix.cap.soul.ISoulEnergyData;
import com.qdd.narutofix.cap.soul.SoulEnergyDataProvider;
import com.qdd.narutofix.items.Sharingan1;
import com.qdd.narutofix.items.Sharingan2;
import com.qdd.narutofix.network.PacketSyncSoulEnergy;
import com.qdd.narutofix.potion.PotionLoader;
import com.qdd.narutofix.util.AdvancementHelper;
import com.qdd.narutofix.util.ChakraSyncHelper;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.narutomod.Chakra;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chakra.Pathway.class)
public abstract class mixinPathway<T extends EntityLivingBase> {
    @Shadow(remap = false) @Final protected T user;

    @Shadow(remap = false)
    public abstract boolean consume(double amountIn);

    @Inject(method = "onUpdate",at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;addPotionEffect(Lnet/minecraft/potion/PotionEffect;)V"),cancellable = true)
    private void onUpdate(CallbackInfo ci) {
        if(this.user.isPotionActive(PotionLoader.PotionIzanagi)){
            ci.cancel();
        }
    }

    @Inject(method = "onUpdate",at= @At(value = "HEAD"),remap = false)
    private void onUpdate2(CallbackInfo ci) {
        if (!(this.user instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) this.user;
        ISoulEnergyData soul = SoulEnergyDataProvider.get(player);
        if (soul == null) {
            return;
        }

        if (!player.world.isRemote) {
            this.tryUpgradeSharingan(player, soul);
        }
    }

    private void tryUpgradeSharingan(EntityPlayer player, ISoulEnergyData soul) {
        ItemStack current = DojutsuEyeHelper.getCapabilityEye(player);
        boolean virtualEye = !current.isEmpty();
        if (current.isEmpty()) {
            current = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        }
        Item item = current.getItem();
        if (item == Sharingan1.helmet) {
            this.tryReplaceEye(player, soul, current, new ItemStack(Sharingan2.helmet, 1), Configs.soul.sharinganTomoeUpgradeSoul, null, virtualEye);
        } else if (item == Sharingan2.helmet) {
            this.tryReplaceEye(player, soul, current, new ItemStack(ItemSharingan.helmet, 1), Configs.soul.sharinganTomoeUpgradeSoul, "narutomod:sharinganopened", virtualEye);
        } else if (item == ItemSharingan.helmet) {
            Item mangekyo = player.getRNG().nextBoolean() ? ItemMangekyoSharingan.helmet : ItemMangekyoSharinganObito.helmet;
            this.tryReplaceEye(player, soul, current, new ItemStack(mangekyo, 1), Configs.soul.mangekyoUpgradeSoul, "narutomod:mangekyosharinganopened", virtualEye);
        } else if (item == ItemMangekyoSharingan.helmet || item == ItemMangekyoSharinganObito.helmet) {
            this.tryReplaceEye(player, soul, current, new ItemStack(ItemMangekyoSharinganEternal.helmet, 1), Configs.soul.eternalMangekyoUpgradeSoul, null, virtualEye);
        }
    }

    private void tryReplaceEye(EntityPlayer player, ISoulEnergyData soul, ItemStack oldEye, ItemStack newEye, double soulCost, String advancementId, boolean virtualEye) {
        if (soul.getCurrent() < soulCost || newEye.isEmpty()) {
            return;
        }
        if (oldEye.getItem() instanceof ItemSharingan.Base && newEye.getItem() instanceof ItemSharingan.Base) {
            ((ItemSharingan.Base) newEye.getItem()).copyOwner(newEye, oldEye);
        } else if (newEye.getItem() instanceof ItemDojutsu.Base) {
            ((ItemDojutsu.Base) newEye.getItem()).setOwner(newEye, player);
        }

        soul.addCurrent(-soulCost);
        if (virtualEye) {
            IPlayerAwakeningData awakeningData = PlayerAwakeningDataProvider.get(player);
            if (awakeningData == null) {
                return;
            }
            awakeningData.setEquippedEye(newEye);
        } else {
            player.setItemStackToSlot(EntityEquipmentSlot.HEAD, newEye);
        }
        if (player instanceof EntityPlayerMP) {
            PacketSyncSoulEnergy.sync((EntityPlayerMP) player);
            ChakraSyncHelper.refresh((EntityPlayerMP) player);
            if (advancementId != null) {
                AdvancementHelper.grant((EntityPlayerMP) player, advancementId);
            }
        }
    }
}
