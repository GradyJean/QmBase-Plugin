package com.qmvector.plugin.mybatismate.util;

import java.io.File;

/**
 * 用于解析目录路径并推导出 Java 包名的工具类。
 * 假设目录结构遵循标准的 Maven 项目布局，如 src/main/java/com/example/demo。
 */
public class PackageResolver {
    /**
     * 根据给定的目录路径推导出 Java 包名。
     * 假设目录结构遵循标准的 Maven 项目布局，如 src/main/java/com/example/demo。
     *
     * @param dirPath 目录路径，例如 src/main/java/com/example/demo
     * @return 推导出的包名，例如 com.example.demo
     * @throws IllegalArgumentException 如果目录路径不符合预期格式
     */
    public static String resolvePackageName(String dirPath) {
        // 假设 dirPath 是标准 Java 源码路径，如 src/main/java/com/example/demo
        // 这里将 java 之后的部分转换为包名
        String normalizedPath = dirPath.replace(File.separatorChar, '/');
        int javaIndex = normalizedPath.indexOf("src/main/java/");
        if (javaIndex == -1 || javaIndex + 14 >= normalizedPath.length()) {
            throw new IllegalArgumentException("目录路径中缺少 /java/ 段，无法推导包名: " + dirPath);
        }
        String packagePart = normalizedPath.substring(javaIndex + 14);
        return packagePart.replace('/', '.');
    }
}
