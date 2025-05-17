package com.qdd.narutofix.items;

import com.google.common.collect.Multimap;
import com.qdd.narutofix.command.SetSusanooColor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodModVariables;
import net.narutomod.Particles;
import net.narutomod.PlayerRender;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityBeamBase;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.item.ItemEightGates;
import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureOnLeftClickEmpty;
import net.narutomod.procedure.ProcedureUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
@ElementsNarutomodMod.ModElement.Tag
public class ItemEightGatesTrue extends ElementsNarutomodMod.ModElement {
    @GameRegistry.ObjectHolder("narutomod:eightgatestrue")
    public static final Item block = null;
    public static final int ENTITYID = 670;
    public static final int ENTITYID2 = 10670;
    public static final int ENTITYID3 = 11670;
    private static final ResourceLocation HIRUDORA_TEXTURE = new ResourceLocation("narutomod:textures/WhiteTiger.png");
    private static final ResourceLocation SEKIZO_TEXTURE = new ResourceLocation("narutomod:textures/longcube_white.png");
    private static final ResourceLocation NGDRAGON_TEXTURE = new ResourceLocation("narutomod:textures/dragon_red.png");
    private static final int NGD_SUSPEND_TIME = 20;
    private static Random rng = new Random();

    public ItemEightGatesTrue(ElementsNarutomodMod instance) {
        super(instance, 282);
    }

    public void initElements() {
        this.elements.items.add((Supplier)() -> new ItemEightGatesTrue.RangedItem());
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:eightgates", "inventory"));
    }

    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ItemEightGatesTrue.RangedItem.AttackHook());
        ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.MAIN_HAND);
        ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.OFF_HAND);
    }

    public static void logBattleXP(EntityPlayer player) {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() != block) {
            stack = player.getHeldItemOffhand();
        }

        if (stack.getItem() == block && ((ItemEightGatesTrue.RangedItem)block).getMaxOpenableGate(stack) < 1.0F) {
            ((ItemEightGatesTrue.RangedItem)block).addBattleXP(stack, 1);
        }

    }

    public static void addBattleXP(EntityPlayer player, int add) {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() != block) {
            stack = player.getHeldItemOffhand();
        }

        if (stack.getItem() == block) {
            ((ItemEightGatesTrue.RangedItem)block).addBattleXP(stack, add);
        }

    }

    private static void closeGates(EntityLivingBase entity) {
        ItemStack stack = entity.getHeldItemMainhand();
        if (stack.getItem() != block) {
            stack = entity.getHeldItemOffhand();
        }

        if (stack.getItem() == block) {
            ((ItemEightGatesTrue.RangedItem)stack.getItem()).closeGates(stack, entity);
        }

    }

    public static int getGatesOpened(EntityLivingBase entity) {
        ItemStack stack = entity.getHeldItemMainhand();
        if (stack.getItem() != block) {
            stack = entity.getHeldItemOffhand();
        }

        return stack.getItem() == block ? (int)((ItemEightGatesTrue.RangedItem)stack.getItem()).getGateOpened(stack) : 0;
    }


    public static class Properties {
        final int gate;
        final String name;
        final int xpRequired;
        final int particles;
        final int particleColor;
        final int strength;
        final int speed;
        final int resistance;
        final int health;
        final float damage;
        final boolean canFly;

        protected Properties(int gt, String nm, int xp, int pt, int col, int str, int spd, int res, int hth, float dmg, boolean fly) {
            if (gt >= 0 && gt <= 8) {
                this.gate = gt;
                this.name = nm;
                this.xpRequired = xp;
                this.particles = pt;
                this.particleColor = col;
                this.strength = str;
                this.speed = spd;
                this.resistance = res;
                this.health = hth;
                this.damage = dmg;
                this.canFly = fly;
            } else {
                throw new IllegalArgumentException("Eight gates dude! Can't be negative or greater than 8.");
            }
        }

        public void activate(EntityLivingBase entity) {
            if (this.gate >= 1 && this.gate <= 8 && !entity.world.isRemote) {
                if (this.particles > 0) {
                    Particles.spawnParticle(entity.world, Particles.Types.SMOKE, entity.posX, entity.posY + 0.8, entity.posZ, this.particles, 0.2, 0.4, 0.2, (double)0.0F, 0.1, (double)0.0F, new int[]{this.particleColor, 40, 5, 240, entity.getEntityId()});
                }

                entity.fallDistance = 0.0F;
                entity.removePotionEffect(MobEffects.SATURATION);
                if (entity.ticksExisted % 10 == 0) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 12, 8, false, false));
                    entity.addPotionEffect(new PotionEffect(MobEffects.HASTE, 12, 3, false, false));
                    entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 12, this.strength, false, false));
                    entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 12, this.resistance, false, false));
                    entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 12, this.speed, false, false));
                    if (entity.getHealth() > 0.0F && (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isCreative())) {
                        if (this.damage >= 0.0F) {
                            if (entity.ticksExisted % 80 == 0) {
                                entity.hurtResistantTime = 10;
                                entity.attackEntityFrom(ProcedureUtils.SPECIAL_DAMAGE, this.damage * 8.0F);
                            }
                        } else {
                            entity.setHealth(entity.getHealth() - this.damage);
                        }
                    }
                }

                if (this.canFly && entity instanceof EntityPlayer && !((EntityPlayer)entity).capabilities.allowFlying) {
                    ((EntityPlayer)entity).capabilities.allowFlying = true;
                    ((EntityPlayer)entity).sendPlayerAbilities();
                }
            }

        }

        public void deActivate(EntityLivingBase entity) {
            if (!entity.world.isRemote && entity instanceof EntityPlayer) {
                PlayerRender.setColorMultiplier((EntityPlayer)entity, 0);
            }

            if (!entity.world.isRemote && this.gate > 1 && (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isCreative())) {
                if (this.canFly && entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer)entity;
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                }
            }

        }
    }

    public static class RangedItem extends Item {
        private final UUID GATE_MODIFIER = UUID.fromString("f6944d0f-5c81-45db-9261-6a9ad9fe4840");
        private static final String GATE_KEY = "gateOpened";
        private static final String SEKIZO_KEY = "sekizoPunchCount";
        private static final String OWNER_KEY = "ownerUUID";
        private static final String XP_KEY = "battleExperience";
        private final ItemEightGatesTrue.Properties[] GATE = new ItemEightGatesTrue.Properties[]{new ItemEightGatesTrue.Properties(0, "", 0, 0, 0, 0, 0, 0, 0, 0.0F, false), new ItemEightGatesTrue.Properties(1, I18n.translateToLocal("chattext.eightgates.gate1"), 220, 0, 0, 3, 2, 0, 10, -1.0F, false), new ItemEightGatesTrue.Properties(2, I18n.translateToLocal("chattext.eightgates.gate2"), 240, 0, 0, 4, 16, 0, 40, -5.0F, false), new ItemEightGatesTrue.Properties(3, I18n.translateToLocal("chattext.eightgates.gate3"), 280, 20, 285212671, 5, 32, 1, 60, -3.0F, false), new ItemEightGatesTrue.Properties(4, I18n.translateToLocal("chattext.eightgates.gate4"), 360, 25, 419430399, 7, 64, 2, 60, 0.4F, false), new ItemEightGatesTrue.Properties(5, I18n.translateToLocal("chattext.eightgates.gate5"), 520, 30, 553648127, 15, 68, 2, 60, 0.6F, false), new ItemEightGatesTrue.Properties(6, I18n.translateToLocal("chattext.eightgates.gate6"), 1, 30, 805371648, 31, 72, 3, 60, 0.8F, false), new ItemEightGatesTrue.Properties(7, I18n.translateToLocal("chattext.eightgates.gate7"), 1480, 30, 805306623, 84, 76, 3, 60, 1.0F, false), new ItemEightGatesTrue.Properties(8, I18n.translateToLocal("chattext.eightgates.gate8"), 2760, 30, 822018048, 349, 80, 4, 60, 1.2F, true)};
        private static int inc = 0;

        public RangedItem() {
            this.setMaxDamage(0);
            this.setFull3D();
            this.setTranslationKey("narutofix.eightgatestrue");
            this.setRegistryName("eightgatestrue");
            this.maxStackSize = 1;
            this.setCreativeTab(TabModTab.tab);
        }

        private int getUseCount(ItemStack stack, int timeLeft) {
            return this.getMaxItemUseDuration(stack) - timeLeft;
        }

        public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
            if (!world.isRemote && entityLivingBase instanceof EntityPlayer && !entityLivingBase.isSneaking()) {
                EntityPlayer entity = (EntityPlayer)entityLivingBase;
                switch ((int)this.getGateOpened(itemstack)) {
                    case 7:
                        this.attackHirudora(entity);
                        entity.sendStatusMessage(new TextComponentString(I18n.translateToLocal("entity.entityhirudora.name")), true);
                        break;
                    case 8:
                        Entity bullet = new ItemEightGates.EntityNGDragon(entity);
                        world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:yagai")), SoundCategory.NEUTRAL, 2.0F, 1.0F);
                        world.spawnEntity(bullet);
                        entity.sendStatusMessage(new TextComponentString(I18n.translateToLocal("entity.entityngdragon.name")), true);
                }
            }

        }

        public void attackHirudora(EntityLivingBase attacker) {
            ItemEightGates.EntityHirudora bullet = new ItemEightGates.EntityHirudora(attacker);
            attacker.world.playSound((EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:hirudora")), SoundCategory.NEUTRAL, 2.0F, 1.0F);
            attacker.world.spawnEntity(bullet);
        }

        private int getSekizoPunchNum(ItemStack stack, int tick) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            int punch = stack.getTagCompound().getInteger("sekizoPunchCount");
            int i = punch >= 0 && punch < 4 ? punch + 1 : 4;
            stack.getTagCompound().setInteger("sekizoPunchCount", i);
            return punch;
        }

        public int attackSekizo(ItemStack itemstack, EntityLivingBase attacker) {
            World world = attacker.world;
            int punchnum = this.getSekizoPunchNum(itemstack, attacker.ticksExisted);
            if (punchnum >= 0) {
                ItemEightGates.EntitySekizo bullet = new ItemEightGates.EntitySekizo(attacker);
                bullet.shoot((double)30.0F, (float)ProcedureUtils.getModifiedAttackDamage(attacker) * 1.0F * (float)Math.pow((double)2.0F, (double)punchnum));
                world.playSound((EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:sekizo")), SoundCategory.NEUTRAL, 2.0F, 1.0F);
                world.spawnEntity(bullet);
            }

            return punchnum;
        }

        public void attackAsakujaku(EntityLivingBase attacker) {
            Vec3d vec3d1 = attacker.getLookVec();
            this.attackAsakujaku(attacker, vec3d1.x, vec3d1.y, vec3d1.z);
        }

        public void attackAsakujaku(final EntityLivingBase attacker, double x, double y, double z) {
            World world = attacker.world;
            Vec3d vec3d = attacker.getPositionEyes(1.0F);

            for(int i = 0; i < 10; ++i) {
                Entity bullet = new EntitySmallFireball(world, attacker, x, y, z) {
                    public void onUpdate() {
                        super.onUpdate();
                        if (this.ticksExisted > 12) {
                            this.setDead();
                        }

                    }

                    public void onImpact(RayTraceResult result) {
                        if (!this.world.isRemote) {
                            if (result.entityHit != null) {
                                if (result.entityHit.equals(this.shootingEntity) || result.entityHit instanceof EntitySmallFireball) {
                                    return;
                                }

                                result.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), (float)ProcedureUtils.getModifiedAttackDamage(attacker) * 0.5F);
                                result.entityHit.setFire(10);
                            }

                            boolean flag = ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
                            this.world.newExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, 2.0F, false, flag);
                            this.setDead();
                        }

                    }

                    protected float getMotionFactor() {
                        return 1.1F;
                    }
                };
                bullet.setPosition(vec3d.x, vec3d.y, vec3d.z);
                world.spawnEntity(bullet);
            }

        }

        public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
            if (!attacker.world.isRemote) {
                int gateOpened = (int)this.getGateOpened(itemstack);
                switch (gateOpened) {
                    case 6:
                        this.attackAsakujaku(attacker);
                        attacker.sendStatusMessage(new TextComponentString(I18n.translateToLocal("entity.entityasakujaku.name")), true);
                        break;
                    case 8:
                        int k = this.attackSekizo(itemstack, attacker);
                        if (k >= 0) {
                            attacker.sendStatusMessage(new TextComponentString(I18n.translateToLocalFormatted("entity.entitysekizo.name", new Object[]{k + 1})), true);
                            break;
                        }
                    case 7:
                        if (attacker.equals(target)) {
                            target = ProcedureUtils.objectEntityLookingAt(attacker, (double)18.0F + (double)5.0F * (double)(gateOpened - 7), (double)3.0F).entityHit;
                            if (!(target instanceof EntityLivingBase)) {
                                return true;
                            }

                            Vec3d vec = target.getPositionVector().subtract(attacker.getPositionVector()).normalize();
                            attacker.rotationYaw = ProcedureUtils.getYawFromVec(vec);
                            attacker.rotationPitch = ProcedureUtils.getPitchFromVec(vec);
                            attacker.setPositionAndUpdate(target.posX - vec.x, target.posY - vec.y + (double)0.5F, target.posZ - vec.z);
                            attacker.attackTargetEntityWithCurrentItem(target);
                        }
                }
            }

            return super.onLeftClickEntity(itemstack, attacker, target);
        }

        private float getMaxOpenableGate(ItemStack stack) {
            int xp = this.getBattleXP(stack);

            for(int i = 8; i > 0; --i) {
                if (xp >= this.GATE[i].xpRequired) {
                    return (float)i;
                }
            }

            return 0.0F;
        }

        public float getGateOpened(ItemStack stack) {
            return stack.hasTagCompound() ? stack.getTagCompound().getFloat("gateOpened") : 6.0F;
        }

        private void setGateOpened(ItemStack stack, EntityLivingBase entity, float gate) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            gate = MathHelper.clamp(gate, 6.0F, entity instanceof EntityPlayer ? (((EntityPlayer)entity).isCreative() ? 8.0F : this.getMaxOpenableGate(stack)) : 7.0F);
            stack.getTagCompound().setFloat("gateOpened", gate);
        }

        private void setBattleXp(ItemStack stack, int xp) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            stack.getTagCompound().setInteger("battleExperience", xp);
        }

        private void addBattleXP(ItemStack stack, int add) {
            this.setBattleXp(stack, this.getBattleXP(stack) + add);
        }

        private int getBattleXP(ItemStack stack) {
            return stack.hasTagCompound() ? stack.getTagCompound().getInteger("battleExperience") : 0;
        }

        public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
            if (player.isSneaking()) {
                float increments = 0.05F;
                float gateOpened = this.getGateOpened(stack);
                if (gateOpened >= 4.0F) {
                    for(int i = 0; i < (int)gateOpened * 10; ++i) {
                        Particles.spawnParticle(player.world, Particles.Types.SMOKE, player.posX, player.posY, player.posZ, 1, (double)1.0F, (double)0.0F, (double)1.0F, (itemRand.nextDouble() - (double)0.5F) * (double)2.0F, (double)0.5F, (itemRand.nextDouble() - (double)0.5F) * (double)2.0F, new int[]{285212671, 30, 0});
                    }

                    if (gateOpened < 4.0F + increments) {
                        player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:opengate")), SoundCategory.NEUTRAL, 1.0F, 1.0F);
                    }

                    if (gateOpened >= 8.0F - increments && gateOpened < 8.0F) {
                        player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:eightgatesrelease")), SoundCategory.NEUTRAL, 2.0F, 1.0F);
                    }

                    if (gateOpened >= 4.0F + increments && player.ticksExisted % 10 == 0) {
                        player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:explosion")), SoundCategory.NEUTRAL, 0.1F, 0.9F - itemRand.nextFloat() * 0.3F);
                    }
                }

                if (player instanceof EntityPlayer) {
                    if (gateOpened >= 3.0F && PlayerRender.getColorMultiplier((EntityPlayer)player) == 0) {
                        PlayerRender.setColorMultiplier((EntityPlayer)player, -1330642944);
                    }

                    ((EntityPlayer)player).sendStatusMessage(new TextComponentString(this.GATE[(int)gateOpened].name), true);
                }

                this.setGateOpened(stack, player, gateOpened + increments);
            }

        }

        public EntityLivingBase getOwner(ItemStack stack) {
            UUID id = ProcedureUtils.getOwnerId(stack);
            return id == null ? null : ProcedureUtils.searchLivingMatchingId(id);
        }

        protected void setOwner(ItemStack stack, EntityLivingBase owner) {
            ProcedureUtils.setOriginalOwner(owner, stack);
            stack.setStackDisplayName(stack.getDisplayName() + " (" + owner.getName() + ")");
        }

        private boolean isOwner(ItemStack stack, EntityLivingBase entity) {
            if (ProcedureUtils.getOwnerId(stack) == null) {
                this.setOwner(stack, entity);
            }

            return ProcedureUtils.isOriginalOwner(entity, stack) || entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative();
        }

        public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
            super.onUpdate(itemstack, world, entity, par4, par5);
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase)entity;
                if (!this.isOwner(itemstack, player)) {
                    return;
                }

                float gateOpened = this.getGateOpened(itemstack);
                if (!player.getHeldItemMainhand().equals(itemstack) && !player.getHeldItemOffhand().equals(itemstack)) {
                    this.closeGates(itemstack, player);
                } else {
                    this.GATE[(int)gateOpened].activate(player);
                    if (gateOpened >= 1.0F && gateOpened >= this.getMaxOpenableGate(itemstack) && entity.ticksExisted % 40 == 8) {
                        this.addBattleXP(itemstack, 1);
                    }
                }
            }

        }

        private void closeGates(ItemStack itemstack, EntityLivingBase player) {
            float gateOpened = this.getGateOpened(itemstack);
            if (gateOpened > 0.0F) {
                this.setGateOpened(itemstack, player, 0.0F);
                this.GATE[(int)gateOpened].deActivate(player);
                itemstack.getTagCompound().removeTag("sekizoPunchCount");
            }

        }

        private String printAttributeModifiers(ItemStack stack) {
            ++inc;
            EntityLivingBase owner = this.getOwner(stack);
            if (owner != null) {
                IAttributeInstance iattributeinstance = owner.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
                if (iattributeinstance == null) {
                    return inc + "-null";
                }

                if (iattributeinstance.getModifiers().isEmpty()) {
                    return inc + "-empty";
                }

                Iterator var4 = iattributeinstance.getModifiers().iterator();
                if (var4.hasNext()) {
                    AttributeModifier attributemodifier = (AttributeModifier)var4.next();
                    return inc + "-" + attributemodifier.toString();
                }
            }

            return "";
        }

        public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
            Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
            int gateOpened = (int)this.getGateOpened(stack);
            if ((slot == EntityEquipmentSlot.MAINHAND || slot == EntityEquipmentSlot.OFFHAND) && gateOpened > 0) {
                double health = (double)this.GATE[gateOpened].health;
                multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(this.GATE_MODIFIER, "8gates.maxhealth", health, 0));
            }

            return multimap;
        }

        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
            super.addInformation(itemstack, world, list, flag);
            list.add(I18n.translateToLocal("tooltip.eightgates.opengates"));
            int max = (int)this.getMaxOpenableGate(itemstack);

            for(int i = 1; i <= 8; ++i) {
                list.add((i <= max ? TextFormatting.GRAY : TextFormatting.DARK_GRAY) + this.GATE[i].name + " " + TextFormatting.ITALIC + I18n.translateToLocalFormatted("tooltip.eightgates.requiredxp", new Object[]{this.GATE[i].xpRequired}) + TextFormatting.RESET);
            }

            list.add(TextFormatting.GREEN + I18n.translateToLocalFormatted("tooltip.eightgates.currentxp", new Object[]{this.getBattleXP(itemstack)}) + TextFormatting.WHITE);
        }

        public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
            ItemStack stack = entity.getHeldItem(hand);
            if (this.isOwner(stack, entity)) {
                entity.setActiveHand(hand);
                return new ActionResult(EnumActionResult.SUCCESS, stack);
            } else {
                return new ActionResult(EnumActionResult.FAIL, stack);
            }
        }

        public EnumAction getItemUseAction(ItemStack itemstack) {
            return EnumAction.BOW;
        }

        public int getMaxItemUseDuration(ItemStack itemstack) {
            return 72000;
        }

        public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
            return false;
        }

        public static class AttackHook {
            @SubscribeEvent
            public void onLivingAttack(LivingAttackEvent event) {
                if (event.getSource().getTrueSource() instanceof EntityLivingBase && event.getSource().getTrueSource() == event.getSource().getImmediateSource()) {
                    EntityLivingBase attacker = (EntityLivingBase)event.getSource().getTrueSource();
                    int gateOpened = ItemEightGatesTrue.getGatesOpened(attacker);
                    EntityLivingBase target = event.getEntityLiving();
                    if (gateOpened >= 5) {
                        if (gateOpened >= 7) {
                            target.world.playSound((EntityPlayer)null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 1.0F, target.getRNG().nextFloat() * 0.5F + 0.5F);
                        }

                        Vec3d vec = attacker.getPositionVector().subtract(target.getPositionVector()).normalize();
                        int i = 1;

                        for(int j = 25; i <= j; ++i) {
                            Vec3d vec1 = vec.scale(0.06 * (double)i);
                            Particles.spawnParticle(attacker.world, Particles.Types.SONIC_BOOM, target.posX, target.posY + 1.4, target.posZ, 1, (double)0.0F, (double)0.0F, (double)0.0F, vec1.x, vec1.y, vec1.z, new int[]{16777215 | (int)((1.0F - (float)i / (float)j) * 64.0F) << 24, i * 2, (int)(5.0F * (1.0F + (float)i / (float)j * 0.5F))});
                        }
                    }

                    if (gateOpened >= 2) {
                        ProcedureUtils.pushEntity(attacker, target, (double)10.0F, 0.2F * (float)gateOpened + (!(attacker instanceof EntityPlayer) && !(target instanceof EntityPlayer) ? 1.0F : 2.0F));
                    }
                }

            }

            @SubscribeEvent
            public void onDeath(LivingDeathEvent event) {
                ItemEightGatesTrue.closeGates(event.getEntityLiving());
            }
        }
    }

   
}
