package com.qdd.narutofix.event;


import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.CommandEvent;
import net.narutomod.NarutomodModVariables;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.entity.EntityGedoStatue;
import net.narutomod.procedure.ProcedureSync;
import net.minecraft.client.Minecraft;




@Mod.EventBusSubscriber(modid = "narutofix")
public final class EventLoader {
    private static final String BATTLEXP = NarutomodModVariables.BATTLEXP;

    @SubscribeEvent
    public static void onCommandAddNinjaXp(CommandEvent event) throws CommandException {
        ICommandSender sender = event.getSender();
        MinecraftServer server=sender.getServer() ;
        String[] parameters= event.getParameters();
        Entity vessel = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(EntityGedoStatue.ENTITY_UUID);
//        System.out.println(event.getCommand().getName());
        if (event.getCommand().getName().equals("addninjaxp")) {
            // 在这里修改命令参数或命令执行逻辑
            // 例如，修改第一个参数
            if (parameters.length < 2) {
                String[] newArr = new String[parameters.length + 1];
                newArr[0] = sender.getName();
                newArr[1] = parameters[0];
//                System.out.println(parameters.toString());
                if (server != null) {
//                    System.out.println("parameters");
                    addBattleXp((EntityPlayer) event.getSender().getCommandSenderEntity(),(double) Double.parseDouble(parameters[0]),true);  // 执行原命令
                    event.setCanceled(true);
                }

            }
        }
        if (event.getCommand().getName().equals("locateEntity")) {
            if (parameters.length == 4 && parameters[2].equals("gedo")&&parameters[3].equals("10")) {
                System.out.println(parameters[2] + parameters[3]);
                for (int i =1 ;i<10;i++) {
                    EntityBijuManager.setVesselByTails(vessel, (int) i);
                }
                event.setCanceled(true);
            }
        }
        }


    public static double getBattleXp(EntityPlayer player) {
        return player.getEntityData().getDouble(BATTLEXP);
    }
    private static void sendBattleXPToTracking(EntityPlayerMP player) {
        ProcedureSync.EntityNBTTag.sendToTracking(player, BATTLEXP, getBattleXp(player));
    }
    private static void addBattleXp(EntityPlayer entity, double xp, boolean sendMessage) {
        entity.getEntityData().setDouble(BATTLEXP, getBattleXp(entity) + xp);
        if (entity instanceof EntityPlayerMP) {
            sendBattleXPToTracking((EntityPlayerMP)entity);
            if (sendMessage) {
                entity.sendStatusMessage(new TextComponentString(
                        net.minecraft.util.text.translation.I18n.translateToLocal("chattext.ninjaexperience")+
                                String.format("%.1f", getBattleXp(entity))), true);
            }
        }
    }

}
