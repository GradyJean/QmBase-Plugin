package com.qmvector.plugin.mybatismate.model.dir;

public class DirMeta {
    private String dirPath;
    private String suffix;
    private DirType dirType;

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getDirPath() {
        return dirPath;
    }

    public String getSuffix() {
        return suffix;
    }

    public DirType getDirType() {
        return dirType;
    }

    public void setDirType(DirType dirType) {
        this.dirType = dirType;
    }
}
