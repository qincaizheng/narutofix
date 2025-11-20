package com.qdd.narutofix.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import com.qdd.narutofix.NarutoFix;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.PlayerTracker;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.item.ItemJutsu;

import javax.annotation.Nullable;
import java.util.List;

public class CopyJutsuScroll extends ItemJutsu.Base {
    private static final ItemJutsu.JutsuEnum EMPTY=new ItemJutsu.JutsuEnum(0,"empty",'D',0,0,null);
    public static ItemJutsu.JutsuEnum.Type EXPAND;
    private ItemStack stack;
    public CopyJutsuScroll() {
        super(EXPAND,EMPTY);
        stack=ItemStack.EMPTY;
        this.setTranslationKey("copyjutsuscroll");
        this.setRegistryName(NarutoFix.MODID, "copyjutsuscroll");
        this.setMaxStackSize(1);
        this.setCreativeTab(TabModTab.tab);
        this.addPropertyOverride(new ResourceLocation(NarutoFix.MODID, "scroll_type"), new IItemPropertyGetter() {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                if(stack.getItem() instanceof CopyJutsuScroll && ((CopyJutsuScroll)stack.getItem()).hasstack()){
                    return 1F;
                }
                return 0F;
            }
        });
    }
    public boolean hasstack(){
        return stack!=ItemStack.EMPTY;
    }
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
        if(stack!=ItemStack.EMPTY){
            ItemJutsu.Base jutsu = (ItemJutsu.Base) stack.getItem();
            list.add((new TextComponentTranslation("tooltip.general.shift")).getUnformattedComponentText());
            list.add((">") + (1) + ": " + ItemJutsu.getCurrentJutsu(stack).getName() + " (XP: " + TextFormatting.GREEN + jutsu.getCurrentJutsuXp(stack) + TextFormatting.GRAY + "/" + jutsu.getCurrentJutsuRequiredXp(stack) + ")");
        }else {
            super.addInformation(itemstack, world, list, flag);
        }
    }

    public void copy(ItemStack stack){
        if(stack.getItem() instanceof ItemJutsu.Base) {
            this.stack = stack.copy();
        }
    }
    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
        if(this.stack.getItem() instanceof ItemJutsu.Base) {
            ItemJutsu.Base jutsu = (ItemJutsu.Base) stack.getItem();
            jutsu.onUsingTick(this.stack, player, timeLeft);
        }
    }


    @Override
    public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entity, int timeLeft) {
        if(this.stack.getItem() instanceof ItemJutsu.Base) {
            ItemJutsu.Base jutsu = (ItemJutsu.Base) stack.getItem();
            jutsu.onPlayerStoppedUsing(this.stack, world, entity, timeLeft);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
        if(this.stack.getItem() instanceof ItemJutsu.Base) {
            ItemJutsu.Base jutsu = (ItemJutsu.Base) stack.getItem();
            return jutsu.onItemRightClick(world, entity, hand);
        }
        return super.onItemRightClick(world, entity, hand);
    }
    public EnumActionResult canActivateJutsu(ItemStack stack, ItemJutsu.JutsuEnum jutsuIn, EntityPlayer entity) {
        if(this.stack.getItem() instanceof ItemJutsu.Base) {
            ItemJutsu.Base jutsu = (ItemJutsu.Base) stack.getItem();
            return jutsu.canActivateJutsu(this.stack, jutsuIn, entity);
        }
        return super.canActivateJutsu(stack, jutsuIn, entity);
    }
}
