package com.qm.plugin.mybatismate.model.checkbox;

public class CheckBoxMeta {
    private final boolean useLombok;
    private final boolean generateMapperBaseMethod;

    public CheckBoxMeta(boolean useLombok, boolean generateMapperBaseMethod) {
        this.useLombok = useLombok;
        this.generateMapperBaseMethod = generateMapperBaseMethod;
    }

    public boolean isUseLombok() {
        return useLombok;
    }

    public boolean isGenerateMapperBaseMethod() {
        return generateMapperBaseMethod;
    }
}
