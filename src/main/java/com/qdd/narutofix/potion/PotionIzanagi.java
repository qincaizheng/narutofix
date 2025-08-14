package com.qdd.narutofix.potion;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import com.qdd.narutofix.NarutoFix;

public class PotionIzanagi extends Potion {
    // 构造方法：注册药水效果的基本属性
    public PotionIzanagi() {
        super(false, 0xFF0000); // true表示负面效果，0xFF0000是药水粒子颜色（红色）
        this.setPotionName("potion.izanagi");
        this.setRegistryName(new ResourceLocation(NarutoFix.MODID, "izanagi"));
        this.setIconIndex(5, 1); // 如果使用自定义材质需要设置
    }
}