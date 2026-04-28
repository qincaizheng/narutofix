package com.qdd.narutofix.event;

import com.qdd.narutofix.util.DojutsuEyeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;

@SideOnly(Side.CLIENT)
public final class EquippedEyeRenderLayer implements LayerRenderer<AbstractClientPlayer> {
    private static boolean registered;

    private final RenderPlayer renderer;
    private final ModelBiped defaultArmorModel = new ModelBiped(1.0F);

    private EquippedEyeRenderLayer(RenderPlayer renderer) {
        this.renderer = renderer;
    }

    public static void registerLayers() {
        if (registered) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        Set<RenderPlayer> renderers = new HashSet<>(minecraft.getRenderManager().getSkinMap().values());
        for (RenderPlayer renderPlayer : renderers) {
            renderPlayer.addLayer(new EquippedEyeRenderLayer(renderPlayer));
        }

        registered = true;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack equippedEye = DojutsuEyeHelper.getVirtualEye(player);
        if (equippedEye.isEmpty() || !(equippedEye.getItem() instanceof ItemArmor)) {
            return;
        }

        ItemStack headStack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!headStack.isEmpty() && ItemStack.areItemsEqual(headStack, equippedEye) && ItemStack.areItemStackTagsEqual(headStack, equippedEye)) {
            return;
        }

        ItemArmor armor = (ItemArmor) equippedEye.getItem();
        if (armor.getEquipmentSlot() != EntityEquipmentSlot.HEAD) {
            return;
        }

        ModelBiped armorModel = ForgeHooksClient.getArmorModel(player, equippedEye, EntityEquipmentSlot.HEAD, this.defaultArmorModel);
        if (armorModel == null) {
            armorModel = this.defaultArmorModel;
        }

        armorModel.setModelAttributes(this.renderer.getMainModel());
        armorModel.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
        setHeadOnlyVisible(armorModel);

        GlStateManager.pushMatrix();
        renderArmorModel(player, equippedEye, armor, armorModel, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderArmorModel(AbstractClientPlayer player, ItemStack equippedEye, ItemArmor armor, ModelBiped armorModel, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(getArmorResource(player, equippedEye, EntityEquipmentSlot.HEAD, null));

        if (armor.hasOverlay(equippedEye)) {
            int color = armor.getColor(equippedEye);
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color & 255) / 255.0F;
            GlStateManager.color(red, green, blue, 1.0F);
            armorModel.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            Minecraft.getMinecraft().getTextureManager().bindTexture(getArmorResource(player, equippedEye, EntityEquipmentSlot.HEAD, "overlay"));
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        armorModel.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        if (equippedEye.hasEffect()) {
            LayerArmorBase.renderEnchantedGlint(this.renderer, player, armorModel, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    private ResourceLocation getArmorResource(AbstractClientPlayer player, ItemStack stack, EntityEquipmentSlot slot, String type) {
        ItemArmor armor = (ItemArmor) stack.getItem();
        String texture = armor.getArmorMaterial().getName();
        String domain = "minecraft";
        int separator = texture.indexOf(':');
        if (separator >= 0) {
            domain = texture.substring(0, separator);
            texture = texture.substring(separator + 1);
        }

        String defaultTexture = String.format("%s:textures/models/armor/%s_layer_1%s.png", domain, texture, type == null ? "" : String.format("_%s", type));
        return new ResourceLocation(ForgeHooksClient.getArmorTexture(player, stack, defaultTexture, slot, type));
    }

    private void setHeadOnlyVisible(ModelBiped armorModel) {
        armorModel.setVisible(false);
        armorModel.bipedHead.showModel = true;
        armorModel.bipedHeadwear.showModel = true;
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}