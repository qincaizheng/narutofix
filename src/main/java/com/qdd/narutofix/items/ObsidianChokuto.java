package com.qdd.narutofix.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import com.google.common.collect.Multimap;
import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.entity.susanoo.SusanooEntityBase;
import com.qdd.narutofix.util.SusanooFistHelper;
import net.narutomod.creativetab.TabModTab;

import java.util.UUID;

public class ObsidianChokuto extends Item {
    private static final UUID ObsidianChokutoModifier = UUID.fromString("17d4bc31-cd15-46cb-ac99-37e50cd8272c");

    public ObsidianChokuto() {
        this.setTranslationKey("narutofix.obsidianchokuto");
        this.setRegistryName(NarutoFix.MODID, "obsidianchokuto");
        this.setMaxStackSize(1);
        this.setCreativeTab(TabModTab.tab);
        this.setMaxDamage(0);
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ObsidianChokutoModifier, "Obsidian Chokuto modifier", 50, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ObsidianChokutoModifier, "Obsidian Chokuto modifier", 1, 0));
            multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(ObsidianChokutoModifier, "Obsidian Chokuto modifier", 9, 0));
            multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ObsidianChokutoModifier, "Obsidian Chokuto modifier", 9, 0));
            multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ObsidianChokutoModifier, "Obsidian Chokuto modifier", 9, 0));
            multimap.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(ObsidianChokutoModifier, "Obsidian Chokuto modifier", 4, 0));
        }

        return multimap;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
        return false;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (entityIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityIn;
            if (SusanooFistHelper.shouldKeepFist(player.getRidingEntity())) {
                return;
            }
        }
        stack.shrink(1);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos,
                                           EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (this.fireMountedSusanooWeapon(player)) {
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn,
                                                    EnumHand handIn) {
        if (this.fireMountedSusanooWeapon(playerIn)) {
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    private boolean fireMountedSusanooWeapon(EntityPlayer player) {
        Entity riding = player.getRidingEntity();
        if (riding instanceof SusanooEntityBase) {
            if (!player.world.isRemote) {
                ((SusanooEntityBase) riding).fireHeldWeaponFor(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (player.getRNG().nextFloat() < 0.2) {
            entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 150);
            if (entity instanceof EntityLivingBase) {
                ((EntityLivingBase) entity).knockBack(player, 0.5f, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
            }
            BlockPos pos = entity.getPosition();
            entity.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1, 1);
            player.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0, 0, 1);
            player.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, pos.getX(), pos.getY(), pos.getZ(), 1, 2, 1, 0, 40);
            player.sendMessage(new TextComponentTranslation("msg.obsidianchokuto.heavy"));
        }
        return false;
    }
}
