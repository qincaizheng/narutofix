package com.qdd.narutofix.event;

import com.qdd.narutofix.handler.RegisterHadler;
import com.qdd.narutofix.util.SusanooFistHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.procedure.ProcedureUtils;

@Mod.EventBusSubscriber
public class ObsidianChokutoEvent {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() == RegisterHadler.obsidianchokuto) {
            event.getToolTip().clear();
            event.getToolTip().add(event.getItemStack().getDisplayName());
            for (String s : I18n.format("item.obsidianchokuto.tooltip").split("==")) {
                event.getToolTip().add(s);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerMount(EntityMountEvent event) {
        if (event.getEntityMounting() instanceof EntityPlayer && SusanooFistHelper.isSusanoo(event.getEntityBeingMounted()) && event.isMounting()) {
            EntityPlayer player = (EntityPlayer) event.getEntityMounting();
            if (player.world.isRemote) {
                return;
            }

            player.inventory.clearMatchingItems(RegisterHadler.obsidianchokuto, -1, -1, null);
            if (!SusanooFistHelper.shouldKeepFist(event.getEntityBeingMounted())) {
                return;
            }

            ItemStack stack = new ItemStack(RegisterHadler.obsidianchokuto);
            if (player.inventory.getFirstEmptyStack() != -1) {
                ProcedureUtils.swapItemToSlot(player, EntityEquipmentSlot.MAINHAND, stack.copy());
                if (!SusanooFistHelper.shouldUseSingleFist(event.getEntityBeingMounted()) && player.inventory.getFirstEmptyStack() != -1) {
                    ProcedureUtils.swapItemToSlot(player, EntityEquipmentSlot.OFFHAND, stack.copy());
                    player.setHeldItem(EnumHand.MAIN_HAND, stack);
                }
                player.sendMessage(new TextComponentTranslation("msg.obsidianchokuto.success"));
            } else {
                player.sendMessage(new TextComponentTranslation("msg.obsidianchokuto.fail"));
            }
        }
    }
}
