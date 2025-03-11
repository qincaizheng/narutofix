package com.qdd.narutofix.items;

import com.google.common.base.CharMatcher;
import com.qdd.narutofix.Configs;
import com.qdd.narutofix.NarutoFix;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.narutomod.Chakra;
import net.narutomod.creativetab.TabModTab;

import javax.annotation.Nullable;
import java.util.*;

public class ItemSealScroll extends Item {
    public static final String SEAL_DATA_KEY = "sealData";
    public static final String[] FACING_KEYS = new String[] { "rotation", "rot", "facing", "face", "direction", "dir", "front", "forward" };
    public static Set<String> ALLOWED_TILES = new HashSet<>();
    public ItemSealScroll() {
        this.setTranslationKey("narutofix.sealscroll");
        this.setRegistryName(NarutoFix.MODID, "sealscroll");
        this.setMaxStackSize(1);
        this.setCreativeTab(TabModTab.tab);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        if (hasSealData(stack))
        {
            return super.getItemStackDisplayName(stack)+" Seal "+stack.getTagCompound().getString("block");
        }

        return  super.getItemStackDisplayName(stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
//        System.out.println("onItemUse");
        ItemStack stack = player.getHeldItem(hand);
        if (hasSealData(stack))
        {
            try
            {
                Vec3d vec = player.getLookVec();
                EnumFacing facing2 = EnumFacing.getFacingFromVector((float) vec.x, 0f, (float) vec.z);
                BlockPos pos2 = pos;
                Block containedblock = getBlock(stack);
                int meta = getMeta(stack);
                IBlockState containedstate = getBlockState(stack);
                if (!world.getBlockState(pos2).getBlock().isReplaceable(world, pos2))
                {
                    pos2 = pos.offset(facing);
                }

                if (world.getBlockState(pos2).getBlock().isReplaceable(world, pos2) && containedblock != null)
                {
                    boolean canPlace = containedblock.canPlaceBlockAt(world, pos2);

                    if (canPlace)
                    {
                        if (player.canPlayerEdit(pos, facing, stack) && world.mayPlace(containedblock, pos2, false, facing, (Entity) null))
                        {
                            boolean set = false;

                            // Handles Blockstate rotation
                            Iterator<IProperty<?>> iterator = containedblock.getDefaultState().getPropertyKeys().iterator();
                            while (iterator.hasNext())
                            {
                                IProperty prop = iterator.next();
                                Object[] allowedValues = prop.getAllowedValues().toArray();

                                if (prop instanceof PropertyDirection && this.equal(allowedValues, EnumFacing.HORIZONTALS))
                                {
                                    BlockSnapshot snapshot = new BlockSnapshot(world, pos2, containedstate);
                                    BlockEvent.PlaceEvent event = new BlockEvent.PlaceEvent(snapshot, world.getBlockState(pos), player, hand);
                                    MinecraftForge.EVENT_BUS.post(event);

                                    if (!event.isCanceled())
                                    {
                                        world.setBlockState(pos2, containedstate.withProperty(prop, containedblock instanceof BlockStairs ? facing2 : facing2.getOpposite()));
                                        set = true;
                                    }
                                }
                                else if (prop instanceof PropertyDirection && this.equal(allowedValues, EnumFacing.VALUES))
                                {
                                    facing2 = EnumFacing.getFacingFromVector((float) vec.x, (float) vec.y, (float) vec.z);

                                    BlockSnapshot snapshot = new BlockSnapshot(world, pos2, containedstate);
                                    BlockEvent.PlaceEvent event = new BlockEvent.PlaceEvent(snapshot, world.getBlockState(pos), player, hand);
                                    MinecraftForge.EVENT_BUS.post(event);

                                    if (!event.isCanceled())
                                    {
                                        world.setBlockState(pos2, containedstate.withProperty(prop, facing2.getOpposite()));
                                        set = true;
                                    }
                                }
                            }

                            // If the blockstate doesn't handle rotation, try to
                            // change rotation via NBT
                            if (!set && !getSealData(stack).isEmpty())
                            {
                                NBTTagCompound tag = getSealData(stack);
                                Set<String> keys = tag.getKeySet();
                                keytester: for (String key : keys)
                                {
                                    for (String facingKey : FACING_KEYS)
                                    {
                                        if (key.toLowerCase().equals(facingKey))
                                        {
                                            String type = NBTBase.getTypeName(tag.getTagId(key));
                                            switch (type)
                                            {
                                                case "TAG_String":
                                                    tag.setString(key, CharMatcher.javaUpperCase().matchesAllOf(tag.getString(key)) ? facing2.getOpposite().getName().toUpperCase() : facing2.getOpposite().getName());
                                                    break;
                                                case "TAG_Int":
                                                    tag.setInteger(key, facing2.getOpposite().getIndex());
                                                    break;
                                                case "TAG_Byte":
                                                    tag.setByte(key, (byte) facing2.getOpposite().getIndex());
                                                    break;
                                                default:
                                                    break;
                                            }

                                            break keytester;
                                        }
                                    }
                                }
                            }

                            if (!set)
                            {
                                BlockSnapshot snapshot = new BlockSnapshot(world, pos2, containedstate);
                                BlockEvent.PlaceEvent event = new BlockEvent.PlaceEvent(snapshot, world.getBlockState(pos), player, hand);
                                MinecraftForge.EVENT_BUS.post(event);

                                if (!event.isCanceled())
                                {
                                    world.setBlockState(pos2, containedstate);
                                    set = true;
                                }
                            }

                            if (set)
                            {
                                TileEntity tile = world.getTileEntity(pos2);
                                if (tile != null)
                                {
                                    NBTTagCompound tag = getSealData(stack);
                                    updateSealLocation(tag, pos2);
                                    tile.readFromNBT(tag);
//									tile.readFromNBT(getSealData(stack));
//									tile.setPos(pos2);
                                }
                                clearSealData(stack);
                                player.playSound(containedblock.getSoundType().getPlaceSound(), 1.0f, 0.5f);
//                                player.setHeldItem(hand, ItemStack.EMPTY);
                                player.getEntityData().removeTag("overrideKey");
                                return EnumActionResult.SUCCESS;
                            }
                        }

                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        } else if (isAllowed(world.getBlockState(pos).getBlock())){
            if (Chakra.pathway(player).consume(100d)){
            if (storeSealData(world.getTileEntity(pos),world,pos, world.getBlockState(pos),stack)) {
                if(world.getTileEntity(pos) != null ){
                    emptyTileEntity(world.getTileEntity(pos));
                }
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                return EnumActionResult.SUCCESS;
        }}}else{clearSealData(stack);}

        return EnumActionResult.FAIL;
    }

    public static boolean isAllowed(Block block){
        String name = block.getRegistryName().toString();
        if (ALLOWED_TILES.contains(name))
            return false;
        else
        {
            boolean contains = true;
            for (String s : ALLOWED_TILES)
            {
                if (s.contains("*"))
                {
                    if(name.contains(s.replace("*", "")))
                        contains = false;
                }
            }
            return contains;
        }
    }

    public static void emptyTileEntity(TileEntity te)
    {
        if (te != null)
        {
            for (EnumFacing facing : EnumFacing.VALUES)
            {
                if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing))
                {
                    IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
                    for (int i = 0; i < itemHandler.getSlots(); i++)
                    {
                        itemHandler.extractItem(i, 64, false);
                    }
                }
            }

            if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
            {
                IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                for (int i = 0; i < itemHandler.getSlots(); i++)
                {
                    itemHandler.extractItem(i, 64, false);
                }
            }

            if (te instanceof IInventory)
            {
                IInventory inv = (IInventory) te;
                inv.clear();
            }

            if (te instanceof IItemHandler)
            {
                IItemHandler itemHandler = (IItemHandler) te;
                for (int i = 0; i < itemHandler.getSlots(); i++)
                {
                    itemHandler.extractItem(i, 64, false);
                }
            }

            te.markDirty();
        }
    }

    public static boolean hasSealData(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            return tag.hasKey(SEAL_DATA_KEY) && tag.hasKey("block") && tag.hasKey("meta") && tag.hasKey("stateid");
        }
        return false;
    }

    public static boolean storeSealData(@Nullable TileEntity tile, World world, BlockPos pos, IBlockState state, ItemStack stack)
    {
        if (stack.isEmpty())
            return false;

        NBTTagCompound chest = new NBTTagCompound();
        if (tile != null)
            chest = tile.writeToNBT(chest);

        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        if (tag.hasKey(SEAL_DATA_KEY))
            return false;

        tag.setTag(SEAL_DATA_KEY, chest);

        ItemStack drop = new ItemStack(state.getBlock().getItemDropped(state, itemRand, 0), 1, state.getBlock().damageDropped(state));

        tag.setString("block", state.getBlock().getRegistryName().toString());
        System.out.println(state.getBlock().getRegistryName().toString());
        tag.setInteger("meta", drop.getItemDamage());
        tag.setInteger("stateid", Block.getStateId(state));
        stack.setTagCompound(tag);
        return true;
    }

    public static void clearSealData(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            tag.removeTag(SEAL_DATA_KEY);
            tag.removeTag("block");
            tag.removeTag("meta");
            tag.removeTag("stateid");
        }
    }

    public static void updateSealLocation(NBTTagCompound tag, BlockPos pos)
    {
        tag.setInteger("x", pos.getX());
        tag.setInteger("y", pos.getY());
        tag.setInteger("z", pos.getZ());
    }

    public static NBTTagCompound getSealData(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            return tag.getCompoundTag(SEAL_DATA_KEY);
        }
        return null;
    }

    public static Block getBlock(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            int id = tag.getInteger("stateid");
            return Block.getStateById(id).getBlock();
        }
        return Blocks.AIR;
    }

    public static int getMeta(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            int meta = tag.getInteger("meta");
            return meta;
        }
        return 0;
    }

    public static ItemStack getItemStack(ItemStack stack)
    {
        return new ItemStack(getBlock(stack), 1, getMeta(stack));
    }

    public static IBlockState getBlockState(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            int id = tag.getInteger("stateid");
            return Block.getStateById(id);
        }
        return Blocks.AIR.getDefaultState();
    }

    public static boolean isLocked(BlockPos pos, World world)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            te.writeToNBT(tag);
            return tag.hasKey("Lock") ? !tag.getString("Lock").equals("") : false;
        }

        return false;
    }

    private boolean equal(Object[] a, Object[] b)
    {
        if (a.length != b.length)
            return false;

        List<Object> lA = Arrays.asList(a);
        List<Object> lB = Arrays.asList(b);

        return lA.containsAll(lB);
    }
}
