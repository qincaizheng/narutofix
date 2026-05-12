package com.qdd.narutofix.network;

import com.qdd.narutofix.entity.susanoo.SusanooEntityBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNarutofixSusanooFire implements IMessage, IMessageHandler<PacketNarutofixSusanooFire, IMessage> {

    private int chargeTicks;

    public PacketNarutofixSusanooFire() {
    }

    public PacketNarutofixSusanooFire(int chargeTicks) {
        this.chargeTicks = chargeTicks;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.chargeTicks);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.chargeTicks = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketNarutofixSusanooFire message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        player.getServerWorld().addScheduledTask(() -> {
            Entity riding = player.getRidingEntity();
            if (riding instanceof SusanooEntityBase) {
                ((SusanooEntityBase) riding).getEntityData().setInteger("narutofix_chargeTicks", message.chargeTicks);
                ((SusanooEntityBase) riding).fireHeldWeaponFor(player);
            }
        });
        return null;
    }
}
