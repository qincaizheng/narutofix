package com.qdd.narutofix.handler;

import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.keybind.KeyLoader;
import com.qdd.narutofix.network.PacketIzanagi;
import ibxm.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.qdd.narutofix.potion.PotionLoader;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;
import static com.qdd.narutofix.cap.JutsuInventoryCapability.Jutsu_INV;


@Mod.EventBusSubscriber
public class IzanagiHandler {

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityPlayer && entity.isPotionActive(PotionLoader.PotionIzanagi)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        PotionEffect effect = event.getPotionEffect();
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityPlayer && entity.isPotionActive(PotionLoader.PotionIzanagi)) {
            if (effect.getPotion().isBadEffect()) {
                entity.removeActivePotionEffect(effect.getPotion());
            }
        }
    }



    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        IJutsuInventory inv=entity.getCapability(Jutsu_INV, null);
        if (entity instanceof EntityPlayer && inv.isIzanagi()) {
            event.setCanceled(true);
            entity.clearActivePotions();
            inv.Izanagi();
            entity.addPotionEffect(new PotionEffect(PotionLoader.PotionIzanagi,3600));
            inv.setIzanagi(false);
            PacketIzanagi message = new PacketIzanagi();
            message.isIzanagi=false;
            PACKET_HANDLER.sendToServer(message);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onKeybind(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        IJutsuInventory inv=player.getCapability(Jutsu_INV, null);
        if(KeyLoader.Izanagi.isKeyDown()){
            if (inv != null) {
                boolean tmp = inv.isIzanagi();
                inv.setIzanagi(!tmp);
                PacketIzanagi message = new PacketIzanagi();
                message.isIzanagi=!tmp;
                PACKET_HANDLER.sendToServer(message);
            }
        }
    }


}
