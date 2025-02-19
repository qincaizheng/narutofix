package com.qdd.narutofix.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.Name("NarutoFix plugin")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"com.qdd.narutofix.core"})
@IFMLLoadingPlugin.SortingIndex(-10)
public class FixPlugin implements IFMLLoadingPlugin{
    private static final String[] TRANSFORMERS = {
            "com.qdd.narutofix.core.FixTransformer"
    };

    @Override
    public String[] getASMTransformerClass() {
        return TRANSFORMERS;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
