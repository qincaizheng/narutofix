package com.qdd.narutofix.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import com.qdd.narutofix.ClientProxy;
import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import com.qdd.narutofix.keybind.KeyLoader;
import com.qdd.narutofix.network.PacketSwitchNextJutsu;
import com.qdd.narutofix.network.PacketSwitchhatbot;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;

@Mod.EventBusSubscriber(Side.CLIENT)
public class JutsuHandler {
    private static boolean switchjutsupresssed=false;
    static boolean[] switchjutsuspressed = new boolean[KeyLoader.switchjutsus.length];

    @SubscribeEvent
    public static void onTickEvent(TickEvent.ClientTickEvent event){

        if(event.phase == TickEvent.Phase.END) return; // Only really needs to be once per tick

        if(NarutoFix.proxy instanceof ClientProxy){

            EntityPlayer player = Minecraft.getMinecraft().player;

            if(player != null){
                if(!player.hasCapability(JutsuInventoryCapability.Jutsu_INV, null))return;
                IJutsuInventory Jutsu_INV= player.getCapability(JutsuInventoryCapability.Jutsu_INV, null);

                if(KeyLoader.switchhatbot.isKeyDown() && Minecraft.getMinecraft().inGameHasFocus){
                    if(!switchjutsupresssed){
                        switchjutsupresssed = true;
                    }
                }else{
                    switchjutsupresssed = false;
                }

                for(int i = 0; i < KeyLoader.switchjutsus.length; i++){
                    if(KeyLoader.switchjutsus[i].isKeyDown() && Minecraft.getMinecraft().inGameHasFocus){
                        if(!switchjutsuspressed[i]){
                            switchjutsuspressed[i] = true;
                            // Packet building
                            switchJutsu(Jutsu_INV, i);
                        }
                    }else{
                        switchjutsuspressed[i] = false;
                    }
                }

            }
        }
    }

    @SubscribeEvent
    public static void onMouseEvent(MouseEvent event){
        EntityPlayer player = Minecraft.getMinecraft().player;
        IJutsuInventory Jutsu_INV= player.getCapability(JutsuInventoryCapability.Jutsu_INV, null);
        ItemStack wand = Jutsu_INV.getItems().getStackInSlot(Jutsu_INV.getSelected());
        if(wand == null) return;

        if(Minecraft.getMinecraft().inGameHasFocus && event.getDwheel() != 0 && switchjutsupresssed){

            event.setCanceled(true);

            int d = Jutsu_INV.getSelected()+(event.getDwheel()>0?-1:1);
            d=d>8?0:d;
            d=d<0?8:d;
            PacketSwitchhatbot packet = new PacketSwitchhatbot();
            packet.selected = d;
            Jutsu_INV.setSelected(d);
            // 同步到服务端
            PACKET_HANDLER.sendToServer(packet);
        }
    }

    public static void onKeyEvent() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player.hasCapability(JutsuInventoryCapability.Jutsu_INV, null)){
            IJutsuInventory jutsu_inv= player.getCapability(JutsuInventoryCapability.Jutsu_INV, null);
            ItemStack itemstack = jutsu_inv.getItems().getStackInSlot(jutsu_inv.getSelected());
            if(itemstack != null){
//                ItemJutsu.Base.switchNextJutsu(itemstack, (EntityLivingBase) player);
                PACKET_HANDLER.sendToServer(new PacketSwitchNextJutsu());
            }
        }
    }

    private static void switchJutsu(IJutsuInventory jutsu_inv, int slot){
        PacketSwitchhatbot packet = new PacketSwitchhatbot();
        packet.selected = slot;
        jutsu_inv.setSelected(slot);
        // 同步到服务端
        PACKET_HANDLER.sendToServer(packet);
    }
}
