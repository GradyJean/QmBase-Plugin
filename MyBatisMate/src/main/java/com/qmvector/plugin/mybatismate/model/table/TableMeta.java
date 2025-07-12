package com.qmvector.plugin.mybatismate.model.table;

import com.intellij.database.model.DasTableKey;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;

import java.util.*;

/**
 * 表结构元数据模型类。
 * 用于封装数据库表的名称、注释和字段信息，
 * 供代码生成器或插件内部逻辑使用。
 */
public class TableMeta {
    /**
     * 表名，例如：user、order_table 等
     */
    private final String tableName;

    /**
     * 表注释，通常为数据库中该表的备注信息
     */
    private final String comment;

    /**
     * 字段列表，包含该表中所有列的元信息
     */
    private final List<ColumnMeta> columns;
    /**
     * 主键集合，包含该表的所有主键字段名
     */
    private final Set<String> primaryKeys;

    /**
     * 构造函数，初始化表元信息。
     *
     * @param tableName 表名
     * @param comment   表注释
     * @param columns   表字段信息列表
     */
    public TableMeta(String tableName, String comment, List<ColumnMeta> columns, Set<String> primaryKeys) {
        this.tableName = tableName;
        this.comment = comment;
        this.columns = columns;
        this.primaryKeys = primaryKeys;
    }

    /**
     * 获取表名。
     *
     * @return 表名字符串
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 获取表注释。
     *
     * @return 表注释字符串
     */
    public String getComment() {
        return comment == null || comment.isEmpty() ? "" : comment;
    }

    /**
     * 获取字段信息列表。
     *
     * @return 字段元信息列表
     */
    public List<ColumnMeta> getColumns() {
        return columns;
    }

    /**
     * 获取主键字段名集合，返回一个逗号分隔的字符串。
     *
     * @return 主键字段名字符串
     */
    public String getPrimaryKeys() {
        return primaryKeys == null ? "" : String.join(",", primaryKeys);
    }

    /**
     * 获取表的元数据实例。
     * 该方法从 DbTable 对象中提取表名、注释和字段信息，
     * 并返回一个 TableMeta 实例。
     *
     * @param table 数据库表对象，通常是 BasicModTable 的实例
     * @return TableMeta 对象，封装了表的元信息
     */
    public static TableMeta getInstance(DbTable table) {
        // 获取表名、注释和字段信息
        String tableName = table.getName();
        String comment = table.getComment();
        // 获取表的所有列信息
        List<ColumnMeta> columnMetas = new ArrayList<>();
        // 获取主键列信息
        DasTableKey dasTableKey = DasUtil.getPrimaryKey(table);
        // 使用 Set 存储主键列名，避免重复
        Set<String> primaryKeyColumns = new HashSet<>();
        // 如果表有主键，获取主键列名
        if (dasTableKey != null) {
            // 迭代主键列引用，添加到主键列集合中
            Iterator<String> iterator = dasTableKey.getColumnsRef().iterate();
            while (iterator.hasNext()) {
                // 将主键列名添加到集合中
                primaryKeyColumns.add(iterator.next());
            }
        }
        DasUtil.getColumns(table).forEach(column -> {
            String columnName = column.getName();
            String columnComment = column.getComment();
            String dataType = column.getDasType().getSpecification();
            dataType = dataType.replaceAll("\\s*unsigned", "")// 去掉 unsigned
                    .replaceAll("\\(.*\\)", "")          // 去掉括号内容
                    .trim().toLowerCase();
            ColumnMeta columnMeta = new ColumnMeta(primaryKeyColumns.contains(columnName), columnName, dataType, columnComment);
            columnMetas.add(columnMeta);
        });
        return new TableMeta(tableName, comment, columnMetas, primaryKeyColumns);
    }
}
