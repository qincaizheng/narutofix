package com.qdd.narutofix.gui;

import com.qdd.narutofix.Config;
import com.qdd.narutofix.NarutoFix;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class NarutoFixGuiFactory extends DefaultGuiFactory {

    public NarutoFixGuiFactory() {
        super(NarutoFix.MODID, GuiConfig.getAbridgedConfigPath(Config.config.toString()));
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parent) {
        return new GuiConfig(parent, getConfigElements(), this.modid, false, false, this.title);
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        list.addAll(new ConfigElement(Config.config
                .getCategory(Configuration.CATEGORY_GENERAL))
                .getChildElements());

        return list;
    }
}