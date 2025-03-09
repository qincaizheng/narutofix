package com.qdd.narutofix.keybind;

import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.settings.KeyBinding;

public class KeyLoader {
    public static KeyBinding LockOnEntity;
    public static KeyBinding switchhatbot;
    public static KeyBinding usejutsu;
    public static KeyBinding openjutsugui;
    public static KeyBinding[] switchjutsus =new KeyBinding[9];
    public static KeyBinding Izanagi;

    public KeyLoader()
    {
        KeyLoader.LockOnEntity =CreateKey("LockOnEntity",Keyboard.KEY_O);
        KeyLoader.switchhatbot =CreateKey("switchhatbot",Keyboard.KEY_LMENU);
        KeyLoader.usejutsu =CreateKey("usejutsu",Keyboard.KEY_G);
        KeyLoader.openjutsugui =CreateKey("openjutsugui",Keyboard.KEY_P);
        for(int i = 0; i < 9; i++){
            KeyLoader.switchjutsus[i] = CreateKey("switchjutsu_"+(i+1),Keyboard.KEY_1+i,KeyModifier.ALT);
        }
        KeyLoader.Izanagi =CreateKey("Izanagi",Keyboard.KEY_K);
    }

    public KeyBinding CreateKey(String name , int keyCode){
        KeyBinding newkey= new KeyBinding("key.narutofix."+name, keyCode, "key.categories.narutofix");
        ClientRegistry.registerKeyBinding(newkey);
        return newkey;
    }

    public KeyBinding CreateKey(String name , int keyCode, KeyModifier keyModifier){
        KeyBinding newkey= new KeyBinding("key.narutofix."+name, KeyConflictContext.IN_GAME, keyModifier, keyCode, "key.categories.narutofix");
        ClientRegistry.registerKeyBinding(newkey);
        return newkey;
    }
}
