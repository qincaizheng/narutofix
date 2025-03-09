package com.qdd.narutofix.core;
import com.google.common.collect.Lists;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

@SuppressWarnings("unused")
public class MixinPlugin implements ILateMixinLoader {

    // ILateMixinLoader接口只有一个你要实现的方法 你在这里返回给第三方模组mixin的config的文件名就行
    //不要在意例子的具体内容(
    @Override
    public List<String> getMixinConfigs() {
        return Lists.newArrayList("mixins.narutofix.json");
    }
}



