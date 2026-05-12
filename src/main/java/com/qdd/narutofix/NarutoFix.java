package com.qdd.narutofix;

import com.qdd.narutofix.command.CommandAddChakra;
import com.qdd.narutofix.command.SetCDandPower;
import com.qdd.narutofix.command.SetSusanooColor;
import com.qdd.narutofix.command.ToNinjaRealm;
import com.qdd.narutofix.command.CommandAwakeKekkeiGenkai;
import com.qdd.narutofix.command.CommandBodyEnergy;
import com.qdd.narutofix.command.CommandClearDojutsuState;
import com.qdd.narutofix.command.CommandGetDojutsuState;
import com.qdd.narutofix.command.CommandOpenWheelMenu;
import com.qdd.narutofix.command.CommandSoulEnergy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = NarutoFix.MODID,
        name = NarutoFix.NAME,
        useMetadata = true,
        dependencies = "required-after:narutomod;required-after:mixinbooter@[7,)",
        guiFactory = "com.qdd.narutofix.gui.NarutoFixGuiFactory"
)
public class NarutoFix {
    public static final String MODID = "narutofix";
    public static final String NAME = "narutofix";
    public static final SimpleNetworkWrapper PACKET_HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel("narutofix");

    @SidedProxy(clientSide = "com.qdd.narutofix.ClientProxy",
            serverSide = "com.qdd.narutofix.CommonProxy")
    public static CommonProxy proxy;

    @Instance(NarutoFix.MODID)
    public static NarutoFix instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new ToNinjaRealm());
        event.registerServerCommand(new SetCDandPower());
        event.registerServerCommand(new SetSusanooColor());
        event.registerServerCommand(new CommandAwakeKekkeiGenkai());
        event.registerServerCommand(new CommandClearDojutsuState());
        event.registerServerCommand(new CommandGetDojutsuState());
        event.registerServerCommand(new CommandOpenWheelMenu());
        event.registerServerCommand(new CommandSoulEnergy());
        event.registerServerCommand(new CommandBodyEnergy());
        event.registerServerCommand(new CommandAddChakra());
    }
}
