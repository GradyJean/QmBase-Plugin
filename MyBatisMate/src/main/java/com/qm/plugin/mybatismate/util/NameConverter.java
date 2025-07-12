package com.qm.plugin.mybatismate.util;

/**
 * 名称转换工具类，用于将数据库表名和字段名转换为 Java 类名和属性名。
 * 提供了将下划线命名或连字符命名的字符串转换为驼峰命名的静态方法。
 *
 */
public final class NameConverter {

    private NameConverter() {
        // 工具类不需要实例化
    }

    /**
     * 将数据库表名转换为 Java 类名（首字母大写驼峰）
     * 示例：user_info -> UserInfo
     *
     * @param tableName 表名（下划线或连字符命名）
     * @return 类名（首字母大写驼峰）
     */
    public static String tableNameToClassName(String tableName) {
        return toCamelCase(tableName, true);
    }

    /**
     * 将数据库字段名转换为 Java 属性名（首字母小写驼峰）
     * 示例：user_name -> userName
     *
     * @param columnName 字段名（下划线或连字符命名）
     * @return 字段名（首字母小写驼峰）
     */
    public static String columnNameToFieldName(String columnName) {
        return toCamelCase(columnName, false);
    }

    /**
     * 通用下划线/连字符/空格转驼峰方法
     * <p>
     * 处理策略：
     * 1. 如果输入为 null 或空字符串，直接返回原输入。
     * 2. 如果输入字符串本身已是驼峰格式（不包含下划线、连字符或空格），则根据 capitalizeFirst 参数调整首字母大小写后返回。
     * 3. 否则，将下划线('_')、连字符('-')和空格(' ')视为分隔符，将后续字符转为大写，其他字符转为小写。
     * 4. 数字字符保持原样，不影响大小写转换。
     *
     * @param input           输入的下划线、连字符或空格命名字符串
     * @param capitalizeFirst 是否首字母大写
     * @return 转换后的驼峰命名字符串；若输入为空或 null 则原样返回
     */
    public static String toCamelCase(String input, boolean capitalizeFirst) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // 如果不包含下划线、连字符和空格，且首字母大小写符合预期，直接返回调整后的字符串
        if (!input.contains("_") && !input.contains("-") && !input.contains(" ")) {
            if (capitalizeFirst) {
                return input.substring(0, 1).toUpperCase() + input.substring(1);
            } else {
                return input.substring(0, 1).toLowerCase() + input.substring(1);
            }
        }

        StringBuilder sb = new StringBuilder();
        boolean nextUpper = capitalizeFirst;

        for (char c : input.toCharArray()) {
            if (c == '_' || c == '-' || c == ' ') {
                nextUpper = true;
            } else if (nextUpper) {
                sb.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }

        return sb.toString();
    }
}
