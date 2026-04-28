package com.qdd.narutofix.items;

import net.minecraft.item.Item;
import net.narutomod.item.ItemRinnegan;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class RinneganHelmetContext {
    private static final Field HELMET_FIELD;
    private static final Field MODIFIERS_FIELD;

    static {
        Field helmetField = null;
        Field modifiersField = null;

        try {
            helmetField = ItemRinnegan.class.getDeclaredField("helmet");
            helmetField.setAccessible(true);
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(helmetField, helmetField.getModifiers() & ~Modifier.FINAL);
        } catch (ReflectiveOperationException ignored) {
            helmetField = null;
            modifiersField = null;
        }

        HELMET_FIELD = helmetField;
        MODIFIERS_FIELD = modifiersField;
    }

    private RinneganHelmetContext() {
    }

    public static void withSixTomoeHelmet(Runnable action) {
        if (HELMET_FIELD == null || MODIFIERS_FIELD == null) {
            action.run();
            return;
        }

        synchronized (RinneganHelmetContext.class) {
            Item previousHelmet = ItemRinnegan.helmet;
            try {
                HELMET_FIELD.set(null, ModItems.SIX_TOMOE_RINNEGAN);
                action.run();
            } catch (IllegalAccessException ignored) {
                action.run();
            } finally {
                try {
                    HELMET_FIELD.set(null, previousHelmet);
                } catch (IllegalAccessException ignored) {
                }
            }
        }
    }
}