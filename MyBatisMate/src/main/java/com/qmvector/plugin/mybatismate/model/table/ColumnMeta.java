package com.qmvector.plugin.mybatismate.model.table;

import com.intellij.psi.PsiType;
import com.qmvector.plugin.mybatismate.util.NameConverter;
import com.qmvector.plugin.mybatismate.util.SqlTypeMapper;

/**
 * 表字段元数据模型。
 * 用于描述数据库表中字段的属性，包括名称、类型、主键标识以及注释。
 * 该类常用于代码生成、表结构展示等插件功能中。
 */
public class ColumnMeta {
    // 字段名（列名）
    private final String columnName;

    // 字段的数据类型（如 varchar、int 等）
    private final String sqlType;

    // 是否为主键字段
    private final boolean isPrimaryKey;

    // 字段的注释（从数据库中读取的描述信息）
    private final String comment;

    /**
     * 构造字段元数据对象。
     *
     * @param isPrimaryKey 是否为主键
     * @param columnName   字段名
     * @param sqlType      字段类型
     * @param comment      字段注释
     */
    public ColumnMeta(boolean isPrimaryKey, String columnName, String sqlType, String comment) {
        this.columnName = columnName;
        this.sqlType = sqlType;
        this.isPrimaryKey = isPrimaryKey;
        this.comment = comment;
    }

    /**
     * 获取字段名。
     *
     * @return 字段名
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * 判断是否为主键。
     *
     * @return true 表示是主键，false 表示不是
     */
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    /**
     * 获取字段注释。
     *
     * @return 字段注释信息
     */
    public String getComment() {
        return comment == null || comment.isEmpty() ? "" : comment;
    }

    /**
     * 获取字段对应的 Java 类型。
     * 使用 SqlTypeMapper 将数据库类型映射到 Java 类型。
     *
     * @return 对应的 Java 类型
     */
    public PsiType getPsiType() {
        return SqlTypeMapper.mapToPsiType(sqlType);
    }

    /**
     * 获取字段对应的 Java 名称（驼峰命名）。
     * 使用 NameConverter 将数据库字段名转换为 Java 属性名。
     *
     * @return 对应的 Java 属性名
     */
    public String getJavaName() {
        return NameConverter.columnNameToFieldName(columnName);
    }

}
