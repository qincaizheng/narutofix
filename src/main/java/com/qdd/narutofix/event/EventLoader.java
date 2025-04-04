package com.qdd.narutofix.event;


import com.qdd.narutofix.Configs;
import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.cap.IJutsuInventory;
import com.qdd.narutofix.cap.JutsuCapabilityProvider;
import com.qdd.narutofix.command.SetSusanooColor;
import com.qdd.narutofix.network.PacketCap;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.CommandEvent;
import net.narutomod.NarutomodModVariables;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.entity.EntityGedoStatue;
import net.narutomod.entity.EntitySusanooBase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;
import static com.qdd.narutofix.cap.JutsuInventoryCapability.Jutsu_INV;
import static net.narutomod.PlayerTracker.addBattleXp;


@Mod.EventBusSubscriber
public final class EventLoader {
    private static final String BATTLEXP = NarutomodModVariables.BATTLEXP;

    @SubscribeEvent
    public static void onCommand(CommandEvent event) throws CommandException {
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
                    addBattleXp((EntityPlayer) event.getSender().getCommandSenderEntity(),(double) Double.parseDouble(parameters[0]));  // 执行原命令
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

    @SubscribeEvent
//    @SideOnly(Side.SERVER)
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof EntityPlayer) {
//            System.out.println(1);
            event.addCapability(new ResourceLocation(NarutoFix.MODID, "jutsu_inv"), new JutsuCapabilityProvider());
        }
    }

    @SubscribeEvent
//    @SideOnly(Side.SERVER)
    public static void onPlayerClone(PlayerEvent.Clone event)    {
        Capability<IJutsuInventory> capability = Jutsu_INV;
        Capability.IStorage<IJutsuInventory> storage = capability.getStorage();

        if (event.getOriginal().hasCapability(capability, null) && event.getEntityPlayer().hasCapability(capability, null))
        {
            NBTBase nbt = storage.writeNBT(capability, event.getOriginal().getCapability(capability, null), null);
            storage.readNBT(capability, event.getEntityPlayer().getCapability(capability, null), null, nbt);
        }
    }

    @SubscribeEvent
    public static void onPlayerjoin(EntityJoinWorldEvent event){
//        System.out.println(555);
        if ( event.getEntity() instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
//            System.out.println(321);
            if (player.hasCapability(Jutsu_INV, null))
            {
//                System.out.println(312);
                PacketCap message = new PacketCap();

                Capability.IStorage<IJutsuInventory> storage = Jutsu_INV.getStorage();

                message.nbt = (NBTTagCompound) storage.writeNBT(Jutsu_INV, player.getCapability(Jutsu_INV, null), null);

                PACKET_HANDLER.sendTo(message, player);
            }
        }
    }

    @SubscribeEvent
    public static void onEntitilinghurt(LivingHurtEvent event){
        if(event.getEntity() instanceof EntitySusanooBase){
            EntitySusanooBase susanooBase= (EntitySusanooBase) event.getEntity();
            if(event.getSource().getTrueSource()==susanooBase.getOwnerPlayer()){
                event.setAmount(0f);
            }
        }
    }
}
