package com.qdd.narutofix.network;

import net.minecraftforge.fml.relauncher.Side;

import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;

public class PacketRegister {
    public PacketRegister() {
        PACKET_HANDLER.registerMessage(PacketOpenCustomInventory.class,PacketOpenCustomInventory.class,0, Side.SERVER);
    }
}
