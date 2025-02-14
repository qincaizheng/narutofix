package com.qdd.narutofix.keybind;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.settings.KeyBinding;

public class KeyLoader {
    public static KeyBinding LockOnEntity;

    public KeyLoader()
    {
        KeyLoader.LockOnEntity = new KeyBinding("key.narutofix.LockOnEntity", Keyboard.KEY_O, "key.categories.narutofix");

        ClientRegistry.registerKeyBinding(KeyLoader.LockOnEntity);
    }
}
