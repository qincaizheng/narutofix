package com.qdd.narutofix.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.cap.JutsuInventoryCapability;
import com.qdd.narutofix.keybind.KeyLoader;
import com.qdd.narutofix.network.PacketOpenJutsugui;
import com.qdd.narutofix.network.PacketUseJutsu;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.PlayerTracker;
import net.narutomod.procedure.ProcedureSync;

import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;

@Mod.EventBusSubscriber(modid = "narutofix")
public class onKeyEvent {
    private static final String shouldTargetLockOnEntity = "shouldTargetLockOnEntity";
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event){
        EntityPlayer player = Minecraft.getMinecraft().player;
        IJutsuInventory jutsu_inv=player.getCapability(JutsuInventoryCapability.Jutsu_INV, null);
//        System.out.println("onKeyInput");
        if (KeyLoader.LockOnEntity.isPressed())        {

            if (FMLClientHandler.instance().isGUIOpen(GuiChat.class) || player == null) {
                return;
            }
            boolean flag = player.getEntityData().getBoolean("shouldTargetLockOnEntity");
//            System.out.println(flag);
            player.getEntityData().setBoolean(shouldTargetLockOnEntity, !flag);
            ProcedureSync.EntityNBTTag.sendToServer(player, shouldTargetLockOnEntity, !flag);
        }
        if (KeyLoader.usejutsu.isPressed()){
            // 发送数据包到服务器执行物品使用
            PACKET_HANDLER.sendToServer(new PacketUseJutsu());
        }
        if(KeyLoader.openjutsugui.isPressed()){
            if(player.hasCapability(JutsuInventoryCapability.Jutsu_INV, null)&& PlayerTracker.isNinja(player)) {
                player.getCapability(JutsuInventoryCapability.Jutsu_INV, null).setIzanagiSize((int) Math.min(PlayerTracker.getBattleXp(player)/2000,9));
            }
            PACKET_HANDLER.sendToServer(new PacketOpenJutsugui());
        }

    }


}
