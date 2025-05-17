package com.qdd.narutofix.mixin;

import com.qdd.narutofix.items.ItemEightGatesTrue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.item.ItemEightGates;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureAddXP2JutsuCommandExecuted;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ProcedureAddXP2JutsuCommandExecuted.class)
public class mixinProcedureAddXP2JutsuCommandExecuted {
    @Inject(method = "executeProcedure",at= @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;",ordinal = 1),remap = false)
    private static void executeProcedure(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity)dependencies.get("entity");
        final HashMap cmdparams = (HashMap)dependencies.get("cmdparams");
        double xp2add = (double)0.0F;
        ItemStack itemmainhand = ItemStack.EMPTY;
        ItemStack itemoffhand = ItemStack.EMPTY;
        String playerName = "";
        xp2add = (double)((new Object() {
            int convert(String s) {
                try {
                    return Integer.parseInt(s.trim());
                } catch (Exception var3) {
                    return 0;
                }
            }
        })).convert(((new Object() {
            public String getText() {
                String param = (String)cmdparams.get("0");
                return param != null ? param : "";
            }
        })).getText());
        playerName = ((new Object() {
            public String getText() {
                String param = (String)cmdparams.get("1");
                return param != null ? param : "";
            }
        })).getText();
        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);
        if (player != null) {
            entity = player;
        }

        if (!playerName.equals("") && player == null) {
            if (entity instanceof EntityPlayer && !entity.world.isRemote) {
                ((EntityPlayer)entity).sendStatusMessage(new TextComponentString("No player found with user name  " + playerName), false);
            }
        } else {
            itemmainhand = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getHeldItemMainhand() : ItemStack.EMPTY;
            itemoffhand = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getHeldItemOffhand() : ItemStack.EMPTY;
            if (EntityBijuManager.cloakLevel((EntityPlayer)entity) > 0) {
                EntityBijuManager.addCloakXp((EntityPlayer)entity, (int)xp2add);
            } else if (itemmainhand.getItem() == (new ItemStack(ItemEightGatesTrue.block, 1)).getItem()) {
                ItemEightGatesTrue.addBattleXP((EntityPlayer)entity, (int)xp2add);
            }else if (itemmainhand.getItem() == (new ItemStack(ItemEightGates.block, 1)).getItem()) {
                ItemEightGates.addBattleXP((EntityPlayer)entity, (int)xp2add);
            } else if (itemmainhand.getItem() instanceof ItemJutsu.Base) {
                ItemJutsu.addBattleXP((EntityPlayer)entity, (int)xp2add);
            } else if (itemoffhand.getItem() == (new ItemStack(ItemEightGatesTrue.block, 1)).getItem()) {
                ItemEightGatesTrue.addBattleXP((EntityPlayer)entity, (int)xp2add);
            }else if (itemoffhand.getItem() == (new ItemStack(ItemEightGates.block, 1)).getItem()) {
                ItemEightGates.addBattleXP((EntityPlayer)entity, (int)xp2add);
            } else if (itemoffhand.getItem() instanceof ItemJutsu.Base) {
                ItemJutsu.addBattleXP((EntityPlayer)entity, (int)xp2add);
            }
        }
    }
}
