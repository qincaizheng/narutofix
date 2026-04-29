package com.qdd.narutofix.network;

import com.qdd.narutofix.network.ActivateSixTomoeSkillMessage;
import com.qdd.narutofix.network.CycleEquippedEyeMessage;
import com.qdd.narutofix.network.OpenEyeStorageMessage;
import net.minecraftforge.fml.relauncher.Side;

import static com.qdd.narutofix.NarutoFix.PACKET_HANDLER;

public class PacketRegister {
    private static int nextID = 0;

    public PacketRegister() {
        PACKET_HANDLER.registerMessage(PacketUseJutsu.class, PacketUseJutsu.class, nextID++, Side.SERVER);
        PACKET_HANDLER.registerMessage(PacketSwitchhatbot.Handler.class, PacketSwitchhatbot.class, nextID++, Side.SERVER);
        PACKET_HANDLER.registerMessage(PacketOpenJutsugui.class, PacketOpenJutsugui.class, nextID++, Side.SERVER);
        PACKET_HANDLER.registerMessage(PacketSwitchNextJutsu.class, PacketSwitchNextJutsu.class, nextID++, Side.SERVER);
        PACKET_HANDLER.registerMessage(PacketCap.Handler.class, PacketCap.class, nextID++, Side.CLIENT);
        PACKET_HANDLER.registerMessage(PacketIzanagi.Handler.class, PacketIzanagi.class, nextID++, Side.SERVER);
        PACKET_HANDLER.registerMessage(PacketFly.Handler.class, PacketFly.class, nextID++, Side.CLIENT);
        PACKET_HANDLER.registerMessage(PacketColor.Handler.class, PacketColor.class, nextID++, Side.CLIENT);
        PACKET_HANDLER.registerMessage(OpenEyeStorageMessage.Handler.class, OpenEyeStorageMessage.class, nextID++, Side.SERVER);
        PACKET_HANDLER.registerMessage(CycleEquippedEyeMessage.Handler.class, CycleEquippedEyeMessage.class, nextID++, Side.SERVER);
        PACKET_HANDLER.registerMessage(ActivateSixTomoeSkillMessage.Handler.class, ActivateSixTomoeSkillMessage.class, nextID++, Side.SERVER);
        PACKET_HANDLER.registerMessage(PacketSyncSoulEnergy.Handler.class, PacketSyncSoulEnergy.class, nextID++, Side.CLIENT);
        PACKET_HANDLER.registerMessage(PacketSyncBodyEnergy.Handler.class, PacketSyncBodyEnergy.class, nextID++, Side.CLIENT);
        PACKET_HANDLER.registerMessage(PacketSyncChakra.Handler.class, PacketSyncChakra.class, nextID++, Side.CLIENT);
    }
}
