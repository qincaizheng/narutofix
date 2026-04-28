package com.qdd.narutofix.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import com.qdd.narutofix.network.PacketFly;

import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;

public class TailsHandler {
    public static void SevenTails(EntityPlayer player,boolean in){
        if(!player.isCreative()&&!player.isSpectator()){
            player.capabilities.isFlying=in;
            player.sendPlayerAbilities();
            if (!player.world.isRemote){
                PacketFly packet =new PacketFly();
                packet.isFlying=in;
                PACKET_HANDLER.sendTo(packet, (EntityPlayerMP) player);
            }
        }
    }
}
