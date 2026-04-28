package com.qdd.narutofix.network;

import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.items.SixTomoeRinneganLogic;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ActivateSixTomoeSkillMessage implements IMessage {
    private int skillId;
    private boolean pressed;

    public ActivateSixTomoeSkillMessage() {
    }

    public ActivateSixTomoeSkillMessage(int skillId, boolean pressed) {
        this.skillId = skillId;
        this.pressed = pressed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.skillId = buf.readInt();
        this.pressed = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.skillId);
        buf.writeBoolean(this.pressed);
    }

    public static class Handler implements IMessageHandler<ActivateSixTomoeSkillMessage, IMessage> {
        @Override
        public IMessage onMessage(ActivateSixTomoeSkillMessage message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack stack = DojutsuEyeHelper.getMatchingEye(player, ModItems.SIX_TOMOE_RINNEGAN);
                if (!stack.isEmpty()) {
                    SixTomoeRinneganLogic.handleAdditionalSkill(message.skillId, message.pressed, stack, player);
                }
            });
            return null;
        }
    }
}