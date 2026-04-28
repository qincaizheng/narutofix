package com.qdd.narutofix.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public final class NarutomodProcedureHelper {
    private NarutomodProcedureHelper() {
    }

    public static Map<String, Object> createPlayerContext(EntityPlayer player) {
        Map<String, Object> context = createEntityWorldContext(player, player.world);
        putPlayerCoordinates(context, player);
        return context;
    }

    public static Map<String, Object> createKeyContext(EntityPlayer player, boolean isPressed) {
        Map<String, Object> context = createPlayerContext(player);
        context.put("is_pressed", isPressed);
        return context;
    }

    public static Map<String, Object> createEntityWorldContext(Entity entity, World world) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("entity", entity);
        context.put("world", world);
        return context;
    }

    public static Map<String, Object> createHelmetTickContext(World world, EntityPlayer player, ItemStack stack) {
        Map<String, Object> context = createPlayerContext(player);
        context.put("itemstack", stack);
        context.put("world", world);
        return context;
    }

    public static void putPlayerCoordinates(Map<String, Object> context, EntityPlayer player) {
        context.put("x", (int) player.posX);
        context.put("y", (int) player.posY);
        context.put("z", (int) player.posZ);
    }

    public static void putBlockPos(Map<String, Object> context, BlockPos pos) {
        context.put("x", pos.getX());
        context.put("y", pos.getY());
        context.put("z", pos.getZ());
    }
}