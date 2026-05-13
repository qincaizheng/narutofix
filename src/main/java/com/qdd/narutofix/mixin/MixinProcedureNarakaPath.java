package com.qdd.narutofix.mixin;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.Chakra;
import net.narutomod.entity.EntityKingOfHell;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.procedure.ProcedureNarakaPath;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(ProcedureNarakaPath.class)
public abstract class MixinProcedureNarakaPath {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutofix$useVirtualEye(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        Object worldObject = dependencies.get("world");
        if (!(entity instanceof EntityPlayer) || !(worldObject instanceof net.minecraft.world.World)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        net.minecraft.world.World world = (net.minecraft.world.World) worldObject;
        if (world.isRemote) return;

        // Only intercept if head slot has no dojutsu
        ItemStack headSlot = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (headSlot.getItem() instanceof ItemDojutsu.Base) return;

        // Check for virtual Rinnegan/Tenseigan
        ItemStack virtualEye = DojutsuEyeHelper.getVirtualEye(player);
        if (virtualEye.isEmpty()) return;
        if (virtualEye.getItem() != ItemRinnegan.helmet && virtualEye.getItem() != ItemTenseigan.helmet) return;

        if (player instanceof EntityLivingBase) {
            ((EntityLivingBase) player).swingArm(net.minecraft.util.EnumHand.MAIN_HAND);
        }

        UUID entityId = ProcedureUtils.getUniqueId(virtualEye, "KoH_id");
        if (entityId == null) {
            if (Chakra.pathway(player).consume(ItemRinnegan.getNarakaPathChakraUsage(player))) {
                EntityKingOfHell.EntityCustom entityToSpawn = new EntityKingOfHell.EntityCustom(player);
                player.world.spawnEntity(entityToSpawn);
                if (!virtualEye.hasTagCompound()) {
                    virtualEye.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                }
                virtualEye.getTagCompound().setUniqueId("KoH_id", entityToSpawn.getUniqueID());
            }
        } else {
            Entity summoned = ((net.minecraft.world.WorldServer) player.world).getEntityFromUuid(entityId);
            if (summoned instanceof EntityKingOfHell.EntityCustom) {
                ((EntityLivingBase) summoned).setHealth(0.0F);
            }
            ProcedureUtils.removeUniqueIdTag(virtualEye, "KoH_id");
        }
        ci.cancel();
    }
}
