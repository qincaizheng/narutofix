package com.qdd.narutofix.keybind;

import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.items.ModItems;
import com.qdd.narutofix.items.SixTomoeRinneganLogic;
import com.qdd.narutofix.network.ActivateSixTomoeSkillMessage;
import com.qdd.narutofix.network.CycleEquippedEyeMessage;
import com.qdd.narutofix.network.OpenEyeStorageMessage;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class EyeKeyHandler {
    private final KeyBinding cycleEyeKey = new KeyBinding("key.narutofix.cycle_eye", Keyboard.KEY_R, "key.categories.narutofix");
    private final KeyBinding openEyeStorageKey = new KeyBinding("key.narutofix.open_eye_storage", Keyboard.KEY_V, "key.categories.narutofix");
    private final KeyBinding sixTomoeSusanooKey = new KeyBinding("key.narutofix.six_tomoe_skill4", Keyboard.KEY_X, "key.categories.narutofix");
    private final KeyBinding sixTomoeGenjutsuKey = new KeyBinding("key.narutofix.six_tomoe_skill5", Keyboard.KEY_H, "key.categories.narutofix");
    private boolean lastCycleState;
    private boolean lastOpenState;
    private boolean lastSusanooState;
    private boolean lastGenjutsuState;

    public void register() {
        ClientRegistry.registerKeyBinding(this.cycleEyeKey);
        ClientRegistry.registerKeyBinding(this.openEyeStorageKey);
        ClientRegistry.registerKeyBinding(this.sixTomoeSusanooKey);
        ClientRegistry.registerKeyBinding(this.sixTomoeGenjutsuKey);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().currentScreen != null) {
            return;
        }

        this.processKeys();
    }

    private void processKeys() {
        boolean cycleDown = this.cycleEyeKey.isKeyDown();
        if (cycleDown && !this.lastCycleState) {
            NarutoFix.PACKET_HANDLER.sendToServer(new CycleEquippedEyeMessage());
        }
        this.lastCycleState = cycleDown;

        boolean openDown = this.openEyeStorageKey.isKeyDown();
        if (openDown && !this.lastOpenState) {
            NarutoFix.PACKET_HANDLER.sendToServer(new OpenEyeStorageMessage());
        }
        this.lastOpenState = openDown;

        boolean susanooDown = this.sixTomoeSusanooKey.isKeyDown();
        if (susanooDown != this.lastSusanooState) {
            NarutoFix.PACKET_HANDLER.sendToServer(new ActivateSixTomoeSkillMessage(SixTomoeRinneganLogic.SKILL_SUSANOO, susanooDown));
        }
        this.lastSusanooState = susanooDown;

        boolean genjutsuDown = this.sixTomoeGenjutsuKey.isKeyDown();
        if (genjutsuDown != this.lastGenjutsuState) {
            NarutoFix.PACKET_HANDLER.sendToServer(new ActivateSixTomoeSkillMessage(SixTomoeRinneganLogic.SKILL_GENJUTSU, genjutsuDown));
        }
        this.lastGenjutsuState = genjutsuDown;
    }

    private boolean isSixTomoeRinneganActive() {
        return Minecraft.getMinecraft().player != null
                && DojutsuEyeHelper.hasEffectiveEye(Minecraft.getMinecraft().player, ModItems.SIX_TOMOE_RINNEGAN);
    }
}