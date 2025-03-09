package com.qdd.narutofix.network;

import net.minecraftforge.fml.relauncher.Side;

import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;

public class PacketRegister {
    private static int nextID = 0;
    public PacketRegister() {
        PACKET_HANDLER.registerMessage(PacketUseJutsu.class,PacketUseJutsu.class,nextID++, Side.SERVER);
        PACKET_HANDLER.registerMessage(PacketSwitchhatbot.Handler.class,PacketSwitchhatbot.class,nextID++, Side.SERVER);
        PACKET_HANDLER.registerMessage(PacketOpenJutsugui.class,PacketOpenJutsugui.class,nextID++, Side.SERVER);
        PACKET_HANDLER.registerMessage(PacketSwitchNextJutsu.class,PacketSwitchNextJutsu.class,nextID++, Side.SERVER);
        PACKET_HANDLER.registerMessage(PacketCap.Handler.class,PacketCap.class,nextID++, Side.CLIENT);
        PACKET_HANDLER.registerMessage(PacketIzanagi.Handler.class,PacketIzanagi.class,nextID++, Side.SERVER);
    }
}
