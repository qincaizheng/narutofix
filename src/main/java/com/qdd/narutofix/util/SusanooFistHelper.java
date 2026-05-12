package com.qdd.narutofix.util;

import com.qdd.narutofix.entity.susanoo.SusanooEntityBase;
import com.qdd.narutofix.entity.susanoo.SusanooSkeletonEntity;
import net.minecraft.entity.Entity;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.entity.EntitySusanooSkeleton;

public final class SusanooFistHelper {
    private SusanooFistHelper() {
    }

    public static boolean isSusanoo(Entity entity) {
        return entity instanceof EntitySusanooBase || entity instanceof SusanooEntityBase;
    }

    public static boolean shouldKeepFist(Entity entity) {
        return entity instanceof EntitySusanooSkeleton.EntityCustom
                || entity instanceof SusanooSkeletonEntity;
    }

    public static boolean shouldUseSingleFist(Entity entity) {
        if (entity instanceof EntitySusanooSkeleton.EntityCustom) {
            return !((EntitySusanooSkeleton.EntityCustom) entity).isFullBody();
        }
        if (entity instanceof SusanooSkeletonEntity) {
            return !((SusanooSkeletonEntity) entity).isFullBody();
        }
        return false;
    }
}
