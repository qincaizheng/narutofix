package com.qdd.narutofix.items;

import com.qdd.narutofix.Configs;
import com.qdd.narutofix.NarutoFix;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.item.ItemSharingan;

import javax.annotation.Nullable;
import java.util.List;


@ElementsNarutomodMod.ModElement.Tag
public class Sharingan1 extends ElementsNarutomodMod.ModElement {
    @GameRegistry.ObjectHolder("narutomod:sharinganhelmet1")
    public static final Item helmet = null;

    public Sharingan1(ElementsNarutomodMod instance) {
        super(instance, 1001);
    }


    public void initElements() {
        ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("SHARINGAN1", "narutomod:sharingan1_", 1024, new int[]{1, 3, 2, 2}, 0, (SoundEvent)null, 0.0F);
        this.elements.items.add(() -> (new ItemSharingan.Base(enuma) {
            public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
                super.onArmorTick(world, entity, itemstack);
            }
            public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
                return "narutomod:textures/sharinganhelmet.png";
            }
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                super.addInformation(stack, worldIn, tooltip, flagIn);
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("tooltip.sharingan1.descr")+ Configs.miss1 + TextFormatting.WHITE);
            }

        }).setTranslationKey("narutofix.sharinganhelmet1").setRegistryName("sharinganhelmet1").setCreativeTab(TabModTab.tab));
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:sharinganhelmet1", "inventory"));
    }
}
