package com.qdd.narutofix.items;

import com.qdd.narutofix.NarutoFix;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemSharingan;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSixTomoeRinnegan extends ItemDojutsu.Base {
    public ItemSixTomoeRinnegan(ItemArmor.ArmorMaterial armorMaterial) {
        super(armorMaterial);
        this.setRegistryName(NarutoFix.MODID, "six_tomoe_rinnegan");
        this.setTranslationKey(NarutoFix.MODID + ".six_tomoe_rinnegan");
        this.setCreativeTab(TabModTab.tab);
        this.setMaxDamage(0);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "narutofix:textures/models/armor/rinneganhelmet_tomoe.png";
    }
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
        return super.getArmorModel(living, stack, slot, defaultModel);
    }

    @Override
    public void setOwner(ItemStack stack, EntityLivingBase entityIn) {
        super.setOwner(stack, entityIn);
        this.setColor(stack, 1 + entityIn.getRNG().nextInt(0x00FFFFFF) | 0x20000000);
    }

    @Override
    public void copyOwner(ItemStack toStack, ItemStack fromStack) {
        super.copyOwner(toStack, fromStack);
        this.setColor(toStack, this.resolveColor(fromStack));
    }

    public void setColor(ItemStack stack, int color) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger("color", color);
    }

    public int getColor(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getInteger("color") : 0;
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu1") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.narutofix.six_tomoe_rinnegan.skill1"));
        tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu2") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.narutofix.six_tomoe_rinnegan.skill2"));
        tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu3") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.narutofix.six_tomoe_rinnegan.skill3"));
        tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.narutofix.six_tomoe_skill4") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.narutofix.six_tomoe_rinnegan.skill4"));
        tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.narutofix.six_tomoe_skill5") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.narutofix.six_tomoe_rinnegan.skill5"));
        tooltip.add(I18n.translateToLocal("tooltip.narutofix.six_tomoe_rinnegan.switch"));
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        super.onArmorTick(world, player, stack);
        SixTomoeRinneganLogic.tickEquippedEye(player, stack);
    }
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
        SixTomoeRinneganLogic.onUpdate(stack, world, entity, this);
    }

    private int resolveColor(ItemStack stack) {
        if (stack.getItem() instanceof ItemSixTomoeRinnegan) {
            return ((ItemSixTomoeRinnegan) stack.getItem()).getColor(stack);
        }
        if (stack.getItem() instanceof ItemSharingan.Base) {
            return ((ItemSharingan.Base) stack.getItem()).getColor(stack);
        }
        return 0;
    }
}