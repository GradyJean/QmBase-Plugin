package com.qm.plugin.mybatismate.model.code;

import com.qm.plugin.mybatismate.model.dir.DirMeta;
import com.qm.plugin.mybatismate.model.table.TableMeta;
import com.qm.plugin.mybatismate.util.NameConverter;
import com.qm.plugin.mybatismate.util.PackageResolver;

import java.util.List;

/**
 * 代码生成元数据类
 */
public class CodeGenMeta {
    /**
     * 表元数据
     */
    private TableMeta tableMeta;
    /**
     * 实体类元数据
     */
    private EntityClassMeta entityMeta;
    /**
     * Mapper接口元数据
     */
    private JavaClassMeta mapperMeta;
    /**
     * Mapper XML元数据
     */
    private CodeSourceMeta mapperXmlMeta;
    /**
     * 是否使用Lombok
     */
    private boolean useLombok;
    /**
     * 是否生成Mapper基础方法
     */
    private boolean generateMapperBaseMethod;

    private CodeGenMeta() {

    }

    public TableMeta getTableMeta() {
        return tableMeta;
    }

    public EntityClassMeta getEntityMeta() {
        return entityMeta;
    }

    public JavaClassMeta getMapperMeta() {
        return mapperMeta;
    }

    public CodeSourceMeta getMapperXmlMeta() {
        return mapperXmlMeta;
    }

    public boolean isUseLombok() {
        return useLombok;
    }

    public boolean isGenerateMapperBaseMethod() {
        return generateMapperBaseMethod;
    }

    public void setUseLombok(boolean useLombok) {
        this.useLombok = useLombok;
    }

    public void setGenerateMapperBaseMethod(boolean generateMapperBaseMethod) {
        this.generateMapperBaseMethod = generateMapperBaseMethod;
    }

    public static CodeGenMeta getInstance(TableMeta tableMeta, List<DirMeta> dirMetas) {
        CodeGenMeta codeGenMeta = new CodeGenMeta();
        codeGenMeta.tableMeta = tableMeta;
        for (DirMeta dirMeta : dirMetas) {
            String suffix = dirMeta.getSuffix();
            String className = NameConverter.tableNameToClassName(tableMeta.getTableName()) + suffix;

            switch (dirMeta.getDirType()) {
                case ENTITY -> {
                    String packageName = PackageResolver.resolvePackageName(dirMeta.getDirPath());
                    codeGenMeta.entityMeta = new EntityClassMeta();
                    codeGenMeta.entityMeta.setOutputPath(dirMeta.getDirPath());
                    codeGenMeta.entityMeta.setPackageName(packageName);
                    codeGenMeta.entityMeta.setClassName(className);
                }
                case MAPPER -> {
                    String packageName = PackageResolver.resolvePackageName(dirMeta.getDirPath());
                    codeGenMeta.mapperMeta = new JavaClassMeta();
                    codeGenMeta.mapperMeta.setOutputPath(dirMeta.getDirPath());
                    codeGenMeta.mapperMeta.setPackageName(packageName);
                    codeGenMeta.mapperMeta.setClassName(className);
                }
                case MAPPER_XML -> {
                    codeGenMeta.mapperXmlMeta = new CodeSourceMeta();
                    codeGenMeta.mapperXmlMeta.setOutputPath(dirMeta.getDirPath());
                    codeGenMeta.mapperXmlMeta.setClassName(className);
                }
            }
        }
        return codeGenMeta;
    }
}
