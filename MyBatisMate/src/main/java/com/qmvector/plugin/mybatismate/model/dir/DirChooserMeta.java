package com.qmvector.plugin.mybatismate.model.dir;

// 目录选择器配置类
public class DirChooserMeta {
    // 目录选择器对外显示名称
    private final String title;
    // 生成文件默认后缀(不是文件后缀)例如:userPO.java userMapper.java,PO和Mapper是默认后缀
    private final String defaultSuffix;
    // 生成文件后缀，例如.java .xml
    private final String fileSuffix;

    public DirChooserMeta(String title, String defaultSuffix, String fileSuffix) {
        this.title = title;
        this.defaultSuffix = defaultSuffix;
        this.fileSuffix = fileSuffix;
    }

    public String getTitle() {
        return title;
    }

    public String getDefaultSuffix() {
        return defaultSuffix;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

}
