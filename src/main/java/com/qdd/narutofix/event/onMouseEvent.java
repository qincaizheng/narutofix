package com.qdd.narutofix.event;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.procedure.ProcedureSync;

@Mod.EventBusSubscriber(modid = "narutofix")
public final class onMouseEvent {
    private static final String shouldTargetLockOnEntity = "shouldTargetLockOnEntity";
    private static final String targetLockOnEntityId = "targetLockOnEntityId";

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onMouseInput(MouseEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (FMLClientHandler.instance().isGUIOpen(net.minecraft.client.gui.GuiChat.class) || player == null) {
            return;
        }
        if (event.getButton() == 1 && onMouseEvent.hasTargetLockOnEntity(player)) {
            //boolean flag = player.getEntityData().getBoolean("shouldTargetLockOnEntity");
            boolean flag = !event.isButtonstate();
            player.getEntityData().setBoolean(shouldTargetLockOnEntity, !flag);
            ProcedureSync.EntityNBTTag.sendToServer(player, shouldTargetLockOnEntity, !flag);
        }
    }
    private static boolean hasTargetLockOnEntity(EntityLivingBase entity) {
        return entity.getEntityData().hasKey(targetLockOnEntityId);
    }
}
