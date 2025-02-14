package com.qdd.narutofix.event;

import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.gui.CustomContainer;
import com.qdd.narutofix.gui.CustomGuiContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.qdd.narutofix.keybind.KeyLoader;
import net.narutomod.procedure.ProcedureSync;

@Mod.EventBusSubscriber(modid = "narutofix")
public class onKeyEvent {
    private static final String shouldTargetLockOnEntity = "shouldTargetLockOnEntity";
    private static final String targetLockOnEntityId = "targetLockOnEntityId";
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/inventory.png");

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event){
//        System.out.println("onKeyInput");
        if (KeyLoader.LockOnEntity.isPressed())
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (FMLClientHandler.instance().isGUIOpen(net.minecraft.client.gui.GuiChat.class) || player == null) {
                return;
            }
            boolean flag = player.getEntityData().getBoolean("shouldTargetLockOnEntity");
//            System.out.println(flag);
            player.getEntityData().setBoolean(shouldTargetLockOnEntity, !flag);
            ProcedureSync.EntityNBTTag.sendToServer(player, shouldTargetLockOnEntity, !flag);
        }
    }
    private static boolean hasTargetLockOnEntity(EntityLivingBase entity) {
        return entity.getEntityData().hasKey(targetLockOnEntityId);
    }

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event){
        if (event.getGui() instanceof GuiInventory && !(event.getGui() instanceof CustomGuiContainer)){
            event.setCanceled(true);
            Minecraft.getMinecraft().player.openGui(NarutoFix.instance,1,Minecraft.getMinecraft().player.world,0,0,0);


        }
    }

}
