package com.qdd.narutofix.items;

import com.qdd.narutofix.handler.ModSounds;
import com.qdd.narutofix.util.DojutsuEyeHelper;
import com.qdd.narutofix.util.NarutomodProcedureHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.PlayerTracker;
import net.narutomod.gui.GuiNinjaScroll;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemYoton;
import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.potion.PotionParalysis;
import net.narutomod.procedure.ProcedureAmaterasuExtinguishEntities;
import net.narutomod.procedure.ProcedureBanShoTenin;
import net.narutomod.procedure.ProcedureChibakuTenseiOnKeyPressed;
import net.narutomod.procedure.ProcedureOuterPath;
import net.narutomod.procedure.ProcedureRinneganHelmetTickEvent;
import net.narutomod.procedure.ProcedureShinraTenseiOnKeyPressed;
import net.narutomod.procedure.ProcedureSusanoo;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.entity.EntitySusanooBase;

import com.qdd.narutofix.network.PacketAmenotejikaraOverlay;
import javax.annotation.Nullable;
import java.util.Map;

public final class SixTomoeRinneganLogic {
    public static final int SKILL_SUSANOO = 3;
    public static final int SKILL_GENJUTSU = 4;

    private static final String SKILL_GROUP_KEY = "six_tomoe_skill_group";
    private static final String AMENOTEJIKARA_COOLDOWN_KEY = "six_tomoe_amenotejikara_cd";
    private static final String AMENOTEJIKARA_OVERLAY_UNTIL_KEY = "six_tomoe_amenotejikara_overlay_until";
    private static final String AMENOTEJIKARA_VISUAL_UNTIL_KEY = "six_tomoe_amenotejikara_visual_until";
    private static final String GENJUTSU_COOLDOWN_KEY = "six_tomoe_genjutsu_cd";
    private static final double AMENOTEJIKARA_CHAKRA_USAGE = 50.0D;
    private static final double AMATERASU_CHAKRA_USAGE = 100.0D;
    private static final double GENJUTSU_CHAKRA_USAGE = 300.0D;
    private static final int AMENOTEJIKARA_EFFECT_DURATION = 5;
    private static final int GENJUTSU_DURATION = 200;
    private static final int GENJUTSU_COOLDOWN = 1200;
    private static final int AMENOTEJIKARA_COOLDOWN = 100;

    private SixTomoeRinneganLogic() {
    }

    public static void handleCustomKeyJ(boolean isPressed, ItemStack stack, EntityPlayer player) {
        if (isPressed || player.world.isRemote) {
            return;
        }

        castAmenotejikara(player);
    }

    public static void handleCustomKeyK(boolean isPressed, ItemStack stack, EntityPlayer player) {
        if (player.world.isRemote) {
            return;
        }

        int currentGroup = getCurrentGroup(stack);
        if (currentGroup == 0) {
            executeSkillGroupProcedure(player, () -> ProcedureShinraTenseiOnKeyPressed.executeProcedure(NarutomodProcedureHelper.createKeyContext(player, isPressed)));
        } else if (currentGroup == 1) {
            Map<String, Object> dependencies = NarutomodProcedureHelper.createEntityWorldContext(player, player.world);
            dependencies.put("is_pressed", isPressed);
            executeSkillGroupProcedure(player, () -> ProcedureBanShoTenin.executeProcedure(dependencies));
        } else if (!isPressed && currentGroup == 2) {
            executeSkillGroupProcedure(player, () -> ProcedureChibakuTenseiOnKeyPressed.executeProcedure(NarutomodProcedureHelper.createPlayerContext(player)));
        } else if (!isPressed && currentGroup == 3) {
            Map<String, Object> dependencies = NarutomodProcedureHelper.createKeyContext(player, false);
            RayTraceResult rayTraceResult = traceLookBlock(player, 5.0D);
            if (rayTraceResult == null || rayTraceResult.getBlockPos() == null) {
                return;
            }
            dependencies.put("x", rayTraceResult.getBlockPos().getX());
            dependencies.put("y", rayTraceResult.getBlockPos().getY());
            dependencies.put("z", rayTraceResult.getBlockPos().getZ());
            executeSkillGroupProcedure(player, () -> ProcedureOuterPath.executeProcedure(dependencies));
        }
    }

    public static void handleCustomKeyL(boolean isPressed, ItemStack stack, EntityPlayer player) {
        if (player.world.isRemote) {
            return;
        }

        castAmaterasu(isPressed, stack, player);
    }

    public static void handleAdditionalSkill(int skillId, boolean isPressed, ItemStack stack, EntityPlayer player) {
        if (skillId == SKILL_SUSANOO) {
            if (!isPressed) {
                ProcedureSusanoo.execute(player);
            }
        } else if (skillId == SKILL_GENJUTSU) {
            if (!isPressed) {
                castGenjutsu(player);
            }
        }
    }

    public static boolean onSwitchJutsuKey(boolean isPressed, ItemStack stack, EntityPlayer player) {
        if (isPressed || stack.isEmpty()) {
            return true;
        }

        if (player.getRidingEntity() instanceof com.qdd.narutofix.entity.susanoo.SusanooEntityBase) {
            com.qdd.narutofix.entity.susanoo.SusanooSummonHandler.upgradeSusanoo(player);
            return true;
        }
        if (player.getRidingEntity() instanceof EntitySusanooBase) {
            ProcedureSusanoo.upgrade(player);
            return true;
        }

        int nextGroup = getCurrentGroup(stack) + 1;
        if (nextGroup > 3) {
            nextGroup = 0;
        }

        setCurrentGroup(stack, nextGroup);
        if (!player.world.isRemote) {
            player.sendStatusMessage(new TextComponentTranslation(getGroupTranslationKey(nextGroup)), true);
        }
        return true;
    }

    public static void tickEquippedEye(EntityPlayer player, ItemStack stack) {
        runEquippedEyeTick(player.world, player, stack);
        if (!player.world.isRemote) {
            updateAmenotejikaraOverlay(player);
            updateAmenotejikaraVisual(player);
            DojutsuEyeHelper.applyWornDojutsuState(player, stack);
            DojutsuEyeHelper.markDojutsuAsWorn(player, player.world);
        }
    }

    public static void onUpdate(ItemStack stack, World world, Entity entity, Item item) {
        if (world.isRemote || !(entity instanceof EntityPlayer)) {
            return;
        }

        // Ensure NBT exists for Susanoo activation check in ProcedureSusanoo.execute()
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        EntityPlayer player = (EntityPlayer) entity;
        if (!DojutsuEyeHelper.hasEffectiveEye(player, item)) {
            return;
        }

        DojutsuEyeHelper.applyWornDojutsuState(player, stack);
        DojutsuEyeHelper.markDojutsuAsWorn(player, world);
    }

    public static void updateEnabledJutsu(EntityPlayer player, ItemStack stack) {
        if (player.world.isRemote || player.ticksExisted % 20 != 0 || stack.isEmpty()) {
            return;
        }

        GuiNinjaScroll.enableJutsu(player, (ItemJutsu.Base) ItemYoton.block, ItemYoton.SEALING9D, true);
        GuiNinjaScroll.enableJutsu(player, (ItemJutsu.Base) ItemYoton.block, ItemYoton.SEALING10, true);
    }

    private static void runEquippedEyeTick(World world, EntityPlayer player, ItemStack stack) {
        Map<String, Object> dependencies = NarutomodProcedureHelper.createHelmetTickContext(world, player, stack);
        RinneganHelmetContext.withSixTomoeHelmet(() -> ProcedureRinneganHelmetTickEvent.executeProcedure(dependencies));
        updateEnabledJutsu(player, stack);
    }

    private static void castAmenotejikara(EntityPlayer player) {
        if (!checkCooldown(player, AMENOTEJIKARA_COOLDOWN_KEY)) {
            return;
        }
        if (!consumeChakra(player, AMENOTEJIKARA_CHAKRA_USAGE)) {
            return;
        }

        RayTraceResult rayTraceResult = ProcedureUtils.objectEntityLookingAt(player, 40.0D);
        if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.ENTITY || rayTraceResult.entityHit == null) {
            return;
        }

        Entity target = rayTraceResult.entityHit;
        if (target == player) {
            return;
        }

        double playerX = player.posX;
        double playerY = player.posY;
        double playerZ = player.posZ;
        float playerYaw = player.rotationYaw;
        float playerPitch = player.rotationPitch;

        player.dismountRidingEntity();
        target.dismountRidingEntity();
        spawnAmenotejikaraEffect(player, playerX, playerY, playerZ);
        spawnAmenotejikaraEffect(player, target.posX, target.posY, target.posZ);
        player.setPositionAndUpdate(target.posX, target.posY, target.posZ);
        target.setPositionAndUpdate(playerX, playerY, playerZ);
        player.rotationYaw = target.rotationYaw;
        player.rotationPitch = target.rotationPitch;
        target.rotationYaw = playerYaw;
        target.rotationPitch = playerPitch;
        activateAmenotejikaraVisual(player);
        playAmenotejikaraSound(player, playerX, playerY, playerZ);
        playAmenotejikaraSound(player, target.posX, target.posY, target.posZ);
        setCooldown(player, AMENOTEJIKARA_COOLDOWN_KEY, AMENOTEJIKARA_COOLDOWN);
    }

    private static void castAmaterasu(boolean isPressed, ItemStack stack, EntityPlayer player) {
        double chakraUsage = getOwnedChakraUsage(stack, AMATERASU_CHAKRA_USAGE, 3.0D, player);
        double cooldown = player.getEntityData().getDouble("amaterasu_cd");

        if (isPressed) {
            if (!player.isCreative() && Chakra.pathway(player).getAmount() < chakraUsage * 1.25D) {
                Chakra.pathway(player).warningDisplay();
                return;
            }

            if (!player.getEntityData().getBoolean("amaterasu_active") && !player.isSneaking()) {
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:amaterasu2")),
                        SoundCategory.NEUTRAL, 1.0F, 1.0F);
                cooldown = player.world.getTotalWorldTime() + 300L;
                consumeChakra(player, chakraUsage);
            }

            player.getEntityData().setBoolean("amaterasu_active", true);
            if (cooldown - player.world.getTotalWorldTime() < 2000L) {
                cooldown += 10.0D;
            }
            player.getEntityData().setDouble("amaterasu_cd", cooldown);
            consumeChakra(player, chakraUsage * 0.25D);

            RayTraceResult trace = ProcedureUtils.objectEntityLookingAt(player, 30.0D);
            if (trace == null) {
                return;
            }

            double amplifier = PlayerTracker.getNinjaLevel(player) / 15.0D;
            if (trace.typeOfHit == RayTraceResult.Type.ENTITY && trace.entityHit instanceof EntityLivingBase) {
                ((EntityLivingBase) trace.entityHit).addPotionEffect(new PotionEffect(PotionAmaterasuFlame.potion, 10000, (int) amplifier, false, false));
            } else if (trace.getBlockPos() != null && trace.sideHit != null) {
                Map<String, Object> dependencies = NarutomodProcedureHelper.createPlayerContext(player);
                dependencies.put("x", trace.getBlockPos().getX() + trace.sideHit.getDirectionVec().getX());
                dependencies.put("y", trace.getBlockPos().getY() + trace.sideHit.getDirectionVec().getY());
                dependencies.put("z", trace.getBlockPos().getZ() + trace.sideHit.getDirectionVec().getZ());
                RinneganHelmetContext.withSixTomoeHelmet(() -> net.narutomod.block.BlockAmaterasuBlock.placeBlock(player.world,
                        new net.minecraft.util.math.BlockPos((int) dependencies.get("x"), (int) dependencies.get("y"), (int) dependencies.get("z")),
                        (int) amplifier));
            }
            return;
        }

        if (player.isSneaking()) {
            RayTraceResult trace = traceLookBlock(player, 50.0D);
            if (trace != null && trace.getBlockPos() != null) {
                Map<String, Object> dependencies = NarutomodProcedureHelper.createPlayerContext(player);
                dependencies.put("x", trace.getBlockPos().getX());
                dependencies.put("y", trace.getBlockPos().getY());
                dependencies.put("z", trace.getBlockPos().getZ());
                ProcedureAmaterasuExtinguishEntities.executeProcedure(dependencies);
            }
        } else if (player.getEntityData().getBoolean("amaterasu_active") && !player.isCreative()) {
            int duration = (int) Math.max(0.0D, (cooldown - player.world.getTotalWorldTime()) * 0.5D);
            player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, duration, 2));
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration * 6, 0));
        }
        player.getEntityData().setBoolean("amaterasu_active", false);
    }

    private static void castGenjutsu(EntityPlayer player) {
        if (!checkCooldown(player, GENJUTSU_COOLDOWN_KEY)) {
            return;
        }
        if (!consumeChakra(player, GENJUTSU_CHAKRA_USAGE)) {
            return;
        }

        RayTraceResult trace = ProcedureUtils.objectEntityLookingAt(player, 30.0D);
        if (trace == null || !(trace.entityHit instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase target = (EntityLivingBase) trace.entityHit;
        player.world.playSound(null, target.posX, target.posY, target.posZ,
                net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:genjutsu")),
                SoundCategory.NEUTRAL, 1.0F, 1.0F);
        target.addPotionEffect(new PotionEffect(PotionParalysis.potion, GENJUTSU_DURATION, 1, false, false));
        target.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, GENJUTSU_DURATION + 40, 0, false, true));
        target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, GENJUTSU_DURATION, 0, false, true));
        if (target instanceof EntityPlayerMP) {
            ProcedureSync.MobAppearanceParticle.send((EntityPlayerMP) target, player.getEntityId());
        }
        setCooldown(player, GENJUTSU_COOLDOWN_KEY, GENJUTSU_COOLDOWN);
    }

    private static void executeSkillGroupProcedure(EntityPlayer player, Runnable runnable) {
        if (!player.world.isRemote) {
            runnable.run();
        }
    }

    private static boolean checkCooldown(EntityPlayer player, String key) {
        long now = player.world.getTotalWorldTime();
        long availableAt = player.getEntityData().getLong(key);
        if (availableAt > now) {
            player.sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted", (availableAt - now + 19L) / 20L), true);
            return false;
        }
        return true;
    }

    private static void setCooldown(EntityPlayer player, String key, int ticks) {
        player.getEntityData().setLong(key, player.world.getTotalWorldTime() + ticks);
    }

    private static void activateAmenotejikaraVisual(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, AMENOTEJIKARA_EFFECT_DURATION, 0, false, false));
        player.getEntityData().setLong(AMENOTEJIKARA_VISUAL_UNTIL_KEY, player.world.getTotalWorldTime() + AMENOTEJIKARA_EFFECT_DURATION);
        if (player instanceof EntityPlayerMP) {
            player.getEntityData().setLong(AMENOTEJIKARA_OVERLAY_UNTIL_KEY, player.world.getTotalWorldTime() + AMENOTEJIKARA_EFFECT_DURATION);
            PacketAmenotejikaraOverlay.activate((EntityPlayerMP) player, AMENOTEJIKARA_EFFECT_DURATION);
        }
    }

    private static void updateAmenotejikaraOverlay(EntityPlayer player) {
        long overlayUntil = player.getEntityData().getLong(AMENOTEJIKARA_OVERLAY_UNTIL_KEY);
        if (overlayUntil == 0L || player.world.getTotalWorldTime() < overlayUntil) {
            return;
        }

        player.getEntityData().removeTag(AMENOTEJIKARA_OVERLAY_UNTIL_KEY);
        if (player instanceof EntityPlayerMP) {
            PacketAmenotejikaraOverlay.deactivate((EntityPlayerMP) player);
        }
    }

    private static void updateAmenotejikaraVisual(EntityPlayer player) {
        long visualUntil = player.getEntityData().getLong(AMENOTEJIKARA_VISUAL_UNTIL_KEY);
        if (visualUntil == 0L) {
            return;
        }
        if (player.world.getTotalWorldTime() >= visualUntil) {
            player.getEntityData().removeTag(AMENOTEJIKARA_VISUAL_UNTIL_KEY);
            return;
        }

        if (player.world instanceof net.minecraft.world.WorldServer) {
            net.minecraft.world.WorldServer world = (net.minecraft.world.WorldServer) player.world;
            double x = player.posX;
            double y = player.posY + player.height * 0.6D;
            double z = player.posZ;
            world.spawnParticle(EnumParticleTypes.SPELL_INSTANT, x, y, z, 4, 0.18D, 0.32D, 0.18D, 0.0D);
            world.spawnParticle(EnumParticleTypes.END_ROD, x, y, z, 6, 0.15D, 0.28D, 0.15D, 0.005D);
        }
    }

    private static void playAmenotejikaraSound(EntityPlayer player, double x, double y, double z) {
        player.world.playSound(null, x, y, z,
                ModSounds.AMENOTEJIKARA,
                SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    private static void spawnAmenotejikaraEffect(EntityPlayer player, double x, double y, double z) {
        Particles.spawnParticle(player.world, Particles.Types.SMOKE, x, y + player.height * 0.5D, z,
                18, 0.35D, 0.55D, 0.35D, 0.0D, 0.01D, 0.0D);
        if (!player.world.isRemote && player.world instanceof net.minecraft.world.WorldServer) {
            ((net.minecraft.world.WorldServer) player.world).spawnParticle(EnumParticleTypes.SPELL_INSTANT,
                    x, y + player.height * 0.5D, z, 12, 0.25D, 0.45D, 0.25D, 0.0D);
            ((net.minecraft.world.WorldServer) player.world).spawnParticle(EnumParticleTypes.END_ROD,
                    x, y + player.height * 0.5D, z, 18, 0.2D, 0.45D, 0.2D, 0.01D);
        }
    }

    private static boolean consumeChakra(EntityPlayer player, double amount) {
        return player.isCreative() || Chakra.pathway(player).consume(amount);
    }

    private static double getOwnedChakraUsage(ItemStack stack, double baseUsage, double foreignMultiplier, EntityPlayer player) {
        if (stack.getItem() instanceof ItemDojutsu.Base && ((ItemDojutsu.Base) stack.getItem()).isOwner(stack, player)) {
            return baseUsage;
        }
        return baseUsage * foreignMultiplier;
    }

    private static String getGroupTranslationKey(int group) {
        return "message.narutofix.six_tomoe_group" + group;
    }

    private static int getCurrentGroup(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound().getInteger(SKILL_GROUP_KEY);
    }

    private static void setCurrentGroup(ItemStack stack, int path) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger(SKILL_GROUP_KEY, path);
    }

    @Nullable
    private static RayTraceResult traceLookBlock(EntityLivingBase entity, double distance) {
        Vec3d start = entity.getPositionEyes(1.0F);
        Vec3d look = entity.getLookVec();
        Vec3d end = start.add(look.x * distance, look.y * distance, look.z * distance);
        return entity.world.rayTraceBlocks(start, end, false, false, true);
    }
}
