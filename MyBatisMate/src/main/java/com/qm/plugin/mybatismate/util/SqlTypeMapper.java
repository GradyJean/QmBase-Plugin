package com.qm.plugin.mybatismate.util;

import com.intellij.psi.PsiType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.qm.plugin.mybatismate.util.PsiTypeResolver.javaClassToPsiType;

/**
 * 数据库字段类型 → Java 类型映射工具类
 */
public final class SqlTypeMapper {

    private static final Map<String, PsiType> SQL_TO_PSI_TYPE = new HashMap<>();
    private static final PsiType DEFAULT_TYPE = javaClassToPsiType(Object.class);

    static {
        //======================== 通用 ========================
        SQL_TO_PSI_TYPE.put("varchar", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("char", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("text", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("integer", javaClassToPsiType(Long.class));
        SQL_TO_PSI_TYPE.put("date", javaClassToPsiType(LocalDate.class));
        SQL_TO_PSI_TYPE.put("timestamp", javaClassToPsiType(LocalDateTime.class));
        SQL_TO_PSI_TYPE.put("time", javaClassToPsiType(LocalTime.class));
        SQL_TO_PSI_TYPE.put("boolean", javaClassToPsiType(Boolean.class));
        SQL_TO_PSI_TYPE.put("blob", javaClassToPsiType(byte[].class));
        SQL_TO_PSI_TYPE.put("numeric", javaClassToPsiType(BigDecimal.class));
        SQL_TO_PSI_TYPE.put("json", javaClassToPsiType(String.class));
        // ======================== MySQL ========================
        SQL_TO_PSI_TYPE.put("longtext", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("tinyint", javaClassToPsiType(Integer.class));
        SQL_TO_PSI_TYPE.put("smallint", javaClassToPsiType(Integer.class));
        SQL_TO_PSI_TYPE.put("int", javaClassToPsiType(Integer.class));
        SQL_TO_PSI_TYPE.put("bigint", javaClassToPsiType(Long.class));
        SQL_TO_PSI_TYPE.put("float", javaClassToPsiType(Float.class));
        SQL_TO_PSI_TYPE.put("double", javaClassToPsiType(Double.class));
        SQL_TO_PSI_TYPE.put("decimal", javaClassToPsiType(BigDecimal.class));
        SQL_TO_PSI_TYPE.put("datetime", javaClassToPsiType(LocalDateTime.class));
        SQL_TO_PSI_TYPE.put("bit", javaClassToPsiType(Boolean.class));
        SQL_TO_PSI_TYPE.put("mediumtext", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("tinytext", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("mediumint", javaClassToPsiType(Integer.class));
        SQL_TO_PSI_TYPE.put("year", javaClassToPsiType(Integer.class));
        SQL_TO_PSI_TYPE.put("enum", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("set", javaClassToPsiType(String.class));
        // ======================== Oracle ========================
        SQL_TO_PSI_TYPE.put("number", javaClassToPsiType(BigDecimal.class));
        SQL_TO_PSI_TYPE.put("varchar2", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("nvarchar2", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("clob", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("nchar", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("long", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("raw", javaClassToPsiType(byte[].class));
        SQL_TO_PSI_TYPE.put("long raw", javaClassToPsiType(byte[].class));
        SQL_TO_PSI_TYPE.put("bfile", javaClassToPsiType(byte[].class));
        SQL_TO_PSI_TYPE.put("timestamp with time zone", javaClassToPsiType(LocalDateTime.class));
        SQL_TO_PSI_TYPE.put("timestamp with local time zone", javaClassToPsiType(LocalDateTime.class));
        // ======================== PostgreSQL ========================
        SQL_TO_PSI_TYPE.put("serial", javaClassToPsiType(Integer.class));
        SQL_TO_PSI_TYPE.put("bigserial", javaClassToPsiType(Long.class));
        SQL_TO_PSI_TYPE.put("int4", javaClassToPsiType(Integer.class));
        SQL_TO_PSI_TYPE.put("int8", javaClassToPsiType(Long.class));
        SQL_TO_PSI_TYPE.put("bool", javaClassToPsiType(Boolean.class));
        SQL_TO_PSI_TYPE.put("jsonb", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("uuid", javaClassToPsiType(String.class));
        SQL_TO_PSI_TYPE.put("bytea", javaClassToPsiType(byte[].class));
        // ======================== SQLite ========================
        SQL_TO_PSI_TYPE.put("real", javaClassToPsiType(Double.class));
    }

    private SqlTypeMapper() {
        // 工具类不允许实例化
    }

    /**
     * 将数据库字段类型映射为 Java 类型
     *
     * @param sqlType 数据库字段类型（不区分大小写，如 "VARCHAR"、"int" 等）
     * @return Java 类型的 Class 对象（如 String.class、Integer.class），默认返回 Object.class
     */
    public static PsiType mapToPsiType(String sqlType) {
        if (sqlType == null || sqlType.isEmpty()) {
            return DEFAULT_TYPE;
        }

        String key = sqlType.toLowerCase(Locale.ROOT);
        // 有些字段可能带有长度限制如 varchar(255)
        int idx = key.indexOf("(");
        if (idx != -1) {
            key = key.substring(0, idx);
        }
        return SQL_TO_PSI_TYPE.getOrDefault(key, DEFAULT_TYPE);
    }
}
