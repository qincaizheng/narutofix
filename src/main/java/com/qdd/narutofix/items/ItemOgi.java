package com.qdd.narutofix.items;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.Chakra;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.*;
import net.narutomod.gui.overlay.OverlayChakraDisplay;
import net.narutomod.item.*;
import net.narutomod.potion.PotionChakraRegeneration;
import net.narutomod.procedure.ProcedurePretaPath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemOgi extends ItemSenjutsu.RangedItem implements ItemOnBody.Interface {
    public static final ItemJutsu.JutsuEnum PRETASHIELD = new ItemJutsu.JutsuEnum(3,"chattext.rinnegan.path3",'S',1,10f,new ItemOgi.PreTaShield());
    private static final ItemJutsu.JutsuEnum SAGEMODE=new ItemJutsu.JutsuEnum(0, "tooltip.senjutsu.sagemode", 'S', 1,(double)10.0F, new ItemSenjutsu.SageMode());
    private static final ItemJutsu.JutsuEnum MEDMODE=new ItemJutsu.JutsuEnum(1, "cellular_activation", 'A', 1,(double)20.0F, new EntityCellularActivation.EC.Jutsu());
    private static final ItemJutsu.JutsuEnum WOODBUDDHA=new ItemJutsu.JutsuEnum(2, "buddha_1000", 'S', 1,(double)5000.0F, new EntityBuddha1000.EC.Jutsu());
    private static final ItemJutsu.JutsuEnum BEAM=new ItemJutsu.JutsuEnum(4, "jintonbeam", 'S', 1, (double)500.0F, new ItemJinton.EntityBeam.Jutsu());
    private static final ItemJutsu.JutsuEnum THUNDER =new ItemJutsu.JutsuEnum(5, "inton_raiha", 'S',1, (double)100.0F, new EntityIntonRaiha.EC.Jutsu());


    public ItemOgi() {
        super(SAGEMODE, MEDMODE, WOODBUDDHA,PRETASHIELD, BEAM, THUNDER);
        this.setTranslationKey("narutofix.ogi");
        this.setRegistryName("narutofix.ogi");
        this.setCreativeTab(TabModTab.tab);
    }
    @Override
    public void setSageType(ItemStack stack, ItemSenjutsu.Type type) {
    }

    @Override
    public ItemSenjutsu.Type getSageType(ItemStack stack) {
        return ItemSenjutsu.Type.NONE;
    }

    @Override
    public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
        this.enableAllJutsus(itemstack,true);
        if(!itemstack.hasTagCompound())
            itemstack.setTagCompound(new NBTTagCompound());
        itemstack.getTagCompound().setBoolean("SageModeActivated", true);
        if(!ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity,itemstack.getItem())) {
            this.setOwner(itemstack, (EntityLivingBase) entity);
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
        super.onUsingTick(stack, player, timeLeft);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entity, int timeLeft) {
        super.onPlayerStoppedUsing(itemstack, world, entity, Math.min(timeLeft, 67000));
        this.setCurrentJutsuCooldown(itemstack,0);
    }


    public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
        attacker.addPotionEffect(new PotionEffect(PotionChakraRegeneration.potion, 100, 0, false, false));
        return super.onLeftClickEntity(itemstack, attacker, target);
    }

    public ItemOnBody.BodyPart showOnBody() {
        return ItemOnBody.BodyPart.NONE;
    }
    public boolean showSkinLayer() {
        return true;
    }
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
        super.addInformation(itemstack, world, list, flag);
    }
    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
            return null;

    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
        ItemStack stack = entity.getHeldItem(hand);
        ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
        ActionResult<ItemStack>  res =super.onItemRightClick(world, entity, hand);
        if (jutsu == WOODBUDDHA &&!world.isRemote) {
            entity.world.spawnEntity(new ItemSenjutsu.EntitySitPlatform(entity));
        }
        return res;
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return null;
    }

    public static class PreTaShield implements ItemJutsu.IJutsuCallback {
        public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
            boolean f1 = false;
            if (!entity.world.isRemote) {
                if (entity instanceof EntityLivingBase && Chakra.pathway((EntityLivingBase)entity).consume(10d)) {
                    if (entity instanceof EntityLivingBase) {
                        ((EntityLivingBase)entity).swingArm(EnumHand.MAIN_HAND);
                    }

                    f1 = entity.getRidingEntity() instanceof EntityPretaShield.EntityCustom;
                    if (!f1) {
                        Entity entityToSpawn = new EntityPretaShield.EntityCustom((EntityPlayer)entity);
                        entity.world.spawnEntity(entityToSpawn);
                    }
                } else if (entity instanceof EntityPlayer) {
                    Chakra.pathway((EntityPlayer)entity).warningDisplay();
                }
            }
            return true;
        }
    }
}
