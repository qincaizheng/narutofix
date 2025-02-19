package com.qdd.narutofix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemSharingan;
import net.narutomod.procedure.ProcedureSpecialJutsu1OnKeyPressed;
import net.narutomod.procedure.ProcedureSpecialJutsu2OnKeyPressed;
import net.narutomod.procedure.ProcedureSpecialJutsu3OnKeyPressed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = {ProcedureSpecialJutsu1OnKeyPressed.class,ProcedureSpecialJutsu2OnKeyPressed.class, ProcedureSpecialJutsu3OnKeyPressed.class})
public class mixinProcedureSpecialJutsuOnKeyPressed {

    @Inject(method = "executeProcedure",at=@At(value = "INVOKE",target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;",shift = At.Shift.AFTER))
    private static void mixinexecuteProcedure(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        ItemStack helmet = ItemStack.EMPTY;
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            for (int i=3;i<player.inventory.armorInventory.size();i++){
                if (player.inventory.armorInventory.get(i).getItem() instanceof ItemSharingan.Base){
                    helmet = player.inventory.armorInventory.get(i);
                    break;
                }
            }
        }
    }


}
