package com.qdd.narutofix.handler;

import com.qdd.narutofix.network.PacketFly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;

public class TailsHandler {
    public static void SevenTails(EntityPlayer player,boolean in){
        if(!player.isCreative()&&!player.isSpectator()){
            player.capabilities.allowFlying=in;
            if (player instanceof EntityPlayerMP){
                PacketFly packet =new PacketFly();
                packet.allowFlying=in;
                PACKET_HANDLER.sendTo(packet, (EntityPlayerMP) player);
            }
        }
    }
}
